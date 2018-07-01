#include "stdafx.h"
#include <mpi.h>
#include <stdio.h>
#include <string>
#include <iostream>
#include <cstdlib>
#include <cmath>
#include <fstream>
#include <vector>
#include <sstream>

using namespace std;
double sstime;

template <typename T>
std::string to_stringx(T value)
{
	std::ostringstream os;
	os << value;
	return os.str();
}

int checkArgumentsNumber(int argc)
{
	int maxArgumentCount = 2;
	if (argc != maxArgumentCount)
	{
		std::cout << "Program wymaga jednego argumentu.\n";
		return 0;
	}

	return 1;
}

int isFileOk(ifstream& inFile)
{
	if (inFile.fail())
	{
		cout << "Problem z zaladowaniem pliku.\n";
		return 0;
	}

	return 1;
}

std::vector<std::string> getFileLines(ifstream& inFile)
{
	std::vector<std::string> vecOfStr;
	char str[255];
	while (inFile) {
		inFile.getline(str, 255);
		if (inFile)
			vecOfStr.push_back(str);
	}

	return vecOfStr;
}

int prime_number(string tnumber)
{
	int prime = 1;
	long long j;

	long long number = atoll(tnumber.c_str());
	long long sqrtNumber = sqrtl(number);

	bool flag = false;

	for (j = 2; j < sqrtNumber; j++)
	{
		if (flag)
			continue;

		if (number % j == 0)
		{
			prime = 0;
			flag = true;
		}
	}

	return prime;
}

int world_size; 
long long* tableOfRes;
int* tableOfResb;
int k;
vector <string> allResults;

int main(int argc, char** argv)
{
	MPI_Init(NULL, NULL);
	MPI_Comm_size(MPI_COMM_WORLD, &world_size);
	int world_rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
	if (world_rank == 0)
	{
		if (!checkArgumentsNumber(argc))
			return -1;

		ifstream inFile;
		inFile.open(argv[1]);
		int isOk = isFileOk(inFile);
		if (!isOk)
			return -1;

		std::vector<std::string> vecOfStr = getFileLines(inFile);
		if (world_rank == 0)
			sstime = MPI_Wtime();

		for (int j = world_size - 1; j > -1; j--)
		{
			k = 0;
			int size = 0;
			for (int i = vecOfStr.size() * j / world_size; i < vecOfStr.size() * (j + 1) / world_size; i++)
				++size;

			tableOfRes = new long long[size];
			for (int i = vecOfStr.size() * j / world_size; i < vecOfStr.size() * (j + 1) / world_size; i++)
			{
				long long number = atoll(vecOfStr[i].c_str());
				tableOfRes[k++] = number;
			}

			if (j != 0)
			{
				MPI_Send(&k, 1, MPI_INT, j, 0, MPI_COMM_WORLD);
				MPI_Send(tableOfRes, k, MPI_LONG_LONG, j, 0, MPI_COMM_WORLD);
			}
		}
	}
	else
	{
		MPI_Recv(&k, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		tableOfRes = new long long[k];
		MPI_Recv(tableOfRes, k, MPI_LONG_LONG, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
	}

	tableOfResb = new int[k];
	for (int i = 0; i < k; i++)
	{
		int isPrime = prime_number(to_stringx(tableOfRes[i]));
		tableOfResb[i] = isPrime;
	}

	if (world_rank != 0)
	{
		MPI_Send(&k, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
		MPI_Send(tableOfRes, k, MPI_LONG_LONG, 0, 0, MPI_COMM_WORLD);
		MPI_Send(tableOfResb, k, MPI_INT, 0, 0, MPI_COMM_WORLD);
	}
	else
	{
		for (int j = 0; j < k; j++)
		{
			string ind = (tableOfResb[j] ? "prime" : "composite");
			allResults.push_back(to_stringx(tableOfRes[j]) + ": " + ind + "\n");
		}

		for (int i = 1; i < world_size; i++)
		{
			MPI_Recv(&k, 1, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			tableOfRes = new long long[k];
			MPI_Recv(tableOfRes, k, MPI_LONG_LONG, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			MPI_Recv(tableOfResb, k, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

			for (int j = 0; j < k; j++)
			{
				string ind = (tableOfResb[j] ? "prime" : "composite");
				allResults.push_back(to_stringx(tableOfRes[j]) + ": " + ind + "\n");
			}
		}

		double endTime = 1000 * (MPI_Wtime() - sstime);
		std::cout << "Time: " << endTime << "ms\n";
		for (int i = 0; i < allResults.size(); i++)
			cout << allResults[i];
	}

	MPI_Finalize();
	return 0;
}