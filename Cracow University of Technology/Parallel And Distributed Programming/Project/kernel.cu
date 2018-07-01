#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <vector>
#include <fstream>
#include <cstring>
#include <iostream>
#include <cuda_runtime.h>
#include <algorithm>
#include <curand.h>

using namespace std;

#define N 9
#define n 3
#define STEPS 18

int *devNewSudokus;
int *devOldSudokus;
int *devGaps;
int *devGapsNumber;
int *devBoardIndex;

int *devIsCompleted;
int *devResult;

int *board = new int[N * N];
int *result = new int[N * N];

int checkArgumentsNumber(int argc)
{
	int maxArgumentCount = 4;
	if (argc != maxArgumentCount)
	{
		std::cout << "Program wymaga trzech argumentów.\n";
		return 0;
	}

	return 1;
}

int loadSudoku(char *nameOfFile, int *board)
{
	FILE * fileToRead = fopen(nameOfFile, "r");
	if (fileToRead == NULL)
	{
		printf("Problem z zaladowaniem pliku wejsciowego.\n");
		return 0;
	}

	char number;
	for (int i = 0; i < N; i++)
	{
		for (int j = 0; j < N; j++)
		{
			if (!fscanf(fileToRead, "%c\n", &number))
			{
				printf("Blad podczas ladowania pliku wejsciowego\n");
				return 0;
			}

			if (number >= '1' && number <= '9')
				board[i * N + j] = (int)(number - '0');
			else
				board[i * N + j] = 0;
		}
	}

	return 1;
}

void initializeCudaMemoryBfs()
{
	const int sk = pow(2, 26);
	cudaMalloc(&devGaps, sk * sizeof(int));
	cudaMalloc(&devGapsNumber, (sk / 81 + 1) * sizeof(int));
	cudaMalloc(&devNewSudokus, sk * sizeof(int));
	cudaMalloc(&devOldSudokus, sk * sizeof(int));
	cudaMalloc(&devBoardIndex, sizeof(int));

	cudaMemset(devBoardIndex, 0, sizeof(int));
	cudaMemset(devNewSudokus, 0, sk * sizeof(int));
	cudaMemset(devOldSudokus, 0, sk * sizeof(int));
}

void initializeCudaMemoryBacktrack() 
{
	cudaMalloc(&devIsCompleted, sizeof(int));
	cudaMalloc(&devResult, N * N * sizeof(int));
	cudaMemset(devIsCompleted, 0, sizeof(int));
	cudaMemcpy(devResult, board, N * N * sizeof(int), cudaMemcpyHostToDevice);
}

void freeMemory()
{
	cudaFree(devGaps);
	cudaFree(devGapsNumber);
	cudaFree(devNewSudokus);
	cudaFree(devOldSudokus);
	cudaFree(devBoardIndex);
	cudaFree(devIsCompleted);
	cudaFree(devResult);

	delete[] board;
	delete[] result;
}

void printBoard(int *board)
{
	for (int i = 0; i < N; i++)
	{
		if (i % n == 0)
			printf("-----------------------\n");

		for (int j = 0; j < N; j++)
		{
			if (j % n == 0)
				printf("| ");

			printf("%d ", board[i * N + j]);
		}

		printf("|\n");
	}

	printf("-----------------------\n");
}

__device__
void resetBitmap(bool *bitMap, int length) 
{
	for (int i = 0; i < length; i++) 
		bitMap[i] = false;
}

__device__
bool checkRow(const int *board, bool* seen, int row)
{
	for (int i = 0; i < N; i++) 
	{
		int val = board[row * N + i];
		if (val != 0)
		{
			if (seen[val - 1])
				return false;
			else
				seen[val - 1] = true;
		}
	}

	return true;
}

__device__
bool checkColumn(const int *board, bool* seen, int col)
{
	for (int j = 0; j < N; j++)
	{
		int val = board[j * N + col];
		if (val != 0)
		{
			if (seen[val - 1])
				return false;
			else
				seen[val - 1] = true;
		}
	}

	return true;
}

__device__
bool checkSubBoard(const int *board, bool* seen, int row, int col)
{
	int ridx = row / n;
	int cidx = col / n;
	for (int i = 0; i < n; i++)
	{
		for (int j = 0; j < n; j++)
		{
			int val = board[(ridx * n + i) * N + (cidx * n + j)];
			if (val != 0)
			{
				if (seen[val - 1])
					return false;
				else
					seen[val - 1] = true;
			}
		}
	}

	return true;
}

__device__
bool validBoard(const int *board, int index) 
{
	int r = index / 9;
	int c = index % 9;

	if ((board[index] < 1) || (board[index] > 9))
		return false;

	bool seen[N];
	resetBitmap(seen, N);

	if (!checkRow(board, seen, r))
		return false;

	resetBitmap(seen, N);

	if (!checkColumn(board, seen, c))
		return false;

	resetBitmap(seen, N);

	if (!checkSubBoard(board, seen, r, c))
		return false;

	//ok
	return true;
}

__global__
void Backtrack(int* newSudokus,
	const int numberOfPossibleBoards,
	int* gaps,
	int* gapsNumber,
	int* isCompleted,
	int* result) 
{
	int tid = blockDim.x * blockIdx.x + threadIdx.x;
	int *currentBoard;
	int *currentEmptySpaces;
	int currentNumEmptySpaces;

	while ((*isCompleted == 0) && (tid < numberOfPossibleBoards))
	{
		int emptyIndex = 0;
		currentBoard = newSudokus + tid * 81;
		currentEmptySpaces = gaps + tid * 81;
		currentNumEmptySpaces = gapsNumber[tid];

		while ((emptyIndex >= 0) && (emptyIndex < currentNumEmptySpaces)) 
		{
			//wyciągamy indeks miejsca 0 na podstawie kolekcji currentEmptySpaces
			//wstawiamy następna liczbę w puste miejsce
			currentBoard[currentEmptySpaces[emptyIndex]]++;

			if (!validBoard(currentBoard, currentEmptySpaces[emptyIndex])) 
			{
				if (currentBoard[currentEmptySpaces[emptyIndex]] >= 9) 
				{
					//jesli wychodzi poza zakres pól - backtrack
					currentBoard[currentEmptySpaces[emptyIndex]] = 0;
					emptyIndex--;
				}
			}
			else
				emptyIndex++;
		}

		if (emptyIndex == currentNumEmptySpaces) 
		{
			*isCompleted = 1; // znaleziono
			for (int i = 0; i < N * N; i++)
				result[i] = currentBoard[i];
		}

		tid += gridDim.x * blockDim.x;
	}
}

__device__
void testColumns(int* previousSudokus, int row, int attempt, unsigned int index, int* works) {
	for (int column = 0; column < N; column++)
		if (previousSudokus[row * N + column + N * N * index] == attempt)
			*works = 0;
}

__device__
void testRows(int* previousSudokus, int column, int attempt, unsigned int index, int* works) {
	for (int row = 0; row < N; row++)
		if (previousSudokus[(row * N) + column + (N * N * index)] == attempt)
			*works = 0;
}

__device__
void testBlock(int* previousSudokus, int col, int row, int attempt, unsigned int index, int* works)
{
	for (int r = n * (row / n); r < n; r++)
	{
		for (int c = n * (col / n); c < n; c++)
			if (previousSudokus[r * N + c + N * N * index] == attempt)
				*works = 0;
	}
}

__device__
void copySudoku(
	int* previousSudokus,
	int* newSudokus,
	int* boardIndex,
	int* gaps,
	int* gapsNumber,
	int row,
	int col,
	int tid,
	int attempt)
{
	int nextBoardIndex = atomicAdd(boardIndex, 1);
	int emptyIndex = 0;
	for (int r = 0; r < 9; r++)
	{
		for (int c = 0; c < 9; c++)
		{
			newSudokus[nextBoardIndex * 81 + r * 9 + c] = previousSudokus[tid * 81 + r * 9 + c];
			if (previousSudokus[tid * 81 + r * 9 + c] == 0 && (r != row || c != col))
			{
				gaps[emptyIndex + 81 * nextBoardIndex] = r * 9 + c;
				emptyIndex++;
			}
		}
	}

	gapsNumber[nextBoardIndex] = emptyIndex;
	newSudokus[nextBoardIndex * 81 + row * 9 + col] = attempt;
}

__global__
void
Bfs(int* oldSudokus,
	int* newSudokus,
	int totalSudokus,
	int* boardIndex,
	int* gaps,
	int* gapsNumber)
{
	unsigned int tid = blockIdx.x * blockDim.x + threadIdx.x;

	//dywergencja
	while (tid < totalSudokus)
	{
		int found = 0;
		for (int i = (tid * N * N); (i < (tid * N * N) + N * N) && (found == 0); i++)
		{
			if (oldSudokus[i] == 0) //znaleziono
			{
				found = 1;
				int temp = i - N * N * tid;
				int row = temp / N;
				int col = temp % N;

				for (int attempt = 1; attempt <= N; attempt++)
				{
					int works = 1;
					testColumns(oldSudokus, row, attempt, tid, &works);
					testRows(oldSudokus, col, attempt, tid, &works);
					testBlock(oldSudokus, col, row, attempt, tid, &works);

					if (works == 1)
						copySudoku(oldSudokus, newSudokus, boardIndex, gaps, gapsNumber, row, col, tid, attempt);
				}
			}
		}

		tid += blockDim.x * gridDim.x;
	}
}

int findPossibleBoards(unsigned int blocks, unsigned int threads)
{
	int numberOfPossibleBoards;
	int totalSudokus = 1;

	cudaMemcpy(devOldSudokus, board, N * N * sizeof(int), cudaMemcpyHostToDevice);
	Bfs<<<blocks, threads>>>(devOldSudokus, devNewSudokus, totalSudokus, devBoardIndex, devGaps, devGapsNumber);

	for (int i = 0; i < STEPS; i++)
	{
		cudaMemcpy(&numberOfPossibleBoards, devBoardIndex, sizeof(int), cudaMemcpyDeviceToHost);
		cudaMemset(devBoardIndex, 0, sizeof(int));

		if (i % 2 == 0)
			Bfs<<<blocks, threads>>>(devNewSudokus, devOldSudokus, numberOfPossibleBoards, devBoardIndex, devGaps, devGapsNumber);
		else
			Bfs<<<blocks, threads>>>(devOldSudokus, devNewSudokus, numberOfPossibleBoards, devBoardIndex, devGaps, devGapsNumber);
	}

	cudaMemcpy(&numberOfPossibleBoards, devBoardIndex, sizeof(int), cudaMemcpyDeviceToHost);
	printf("Liczba uzyskanych wariantów konfiguracji: %d\n", numberOfPossibleBoards);
	return numberOfPossibleBoards;
}

int main(int argc, char* argv[])
{
	if (!checkArgumentsNumber(argc))
		return -1;

	int blocks = atoi(argv[1]);
	int threads = atoi(argv[2]);
	char* nameOfFile = argv[3];

	if (!loadSudoku(nameOfFile, board))
		return -1;

	initializeCudaMemoryBfs();
	initializeCudaMemoryBacktrack();

	cudaEvent_t timeStart, timeEnd;
	float time;
	cudaEventCreate(&timeStart);
	cudaEventCreate(&timeEnd);
	cudaEventRecord(timeStart, 0);

	int numberOfPossibleBoards = findPossibleBoards(blocks, threads);

	if (STEPS % 2 == 1)
		devNewSudokus = devOldSudokus;

	Backtrack<<<blocks, threads>>>(devNewSudokus, numberOfPossibleBoards, devGaps, devGapsNumber, devIsCompleted, devResult);

	cudaDeviceSynchronize();
	cudaEventRecord(timeEnd, 0);
	cudaEventSynchronize(timeEnd);
	cudaEventElapsedTime(&time, timeStart, timeEnd);

	memset(result, 0, N * N * sizeof(int));
	cudaMemcpy(result, devResult, N * N * sizeof(int), cudaMemcpyDeviceToHost);
	printBoard(result);
	cout << "Time: " << time << "ms\n";

	freeMemory();
	return 0;
}
