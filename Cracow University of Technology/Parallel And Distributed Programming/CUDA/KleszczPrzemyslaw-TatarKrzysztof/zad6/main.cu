#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <stdio.h>
#include <stdlib.h>

using namespace std;

#define BLOCK_SIZE 100

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

__global__ void prime(long long int* numbers, int* results)
{
	unsigned int numberIndex = blockIdx.x;
	unsigned long long int number = numbers[numberIndex];

	long long j;
	unsigned long long int sqrtNumber = rint(sqrt((double)number));

	__shared__ 
	bool flag;
	flag = false;

	for (j = (threadIdx.x * (sqrtNumber / blockDim.x) + 2); j < (threadIdx.x * (sqrtNumber / blockDim.x)) + (sqrtNumber / blockDim.x) + 2; j++)
	{
		__syncthreads();
		if (flag)
			continue;

		if (number % j == 0)
		{
			flag = true;
			results[numberIndex] = 0;
		}
	}

	if (!flag)
		results[numberIndex] = 1;
}


int main(int argc, char *argv[])
{
	if (!checkArgumentsNumber(argc))
		return -1;

	ifstream inFile;
	inFile.open(argv[1]);
	int isOk = isFileOk(inFile);
	if (!isOk)
		return -1;

	std::vector<std::string> vecOfStr = getFileLines(inFile);
	std::vector<std::string> vecOfRes;

	long long int* tabOfNumbers_l = new long long int[vecOfStr.size()];
	for (int i = 0; i < vecOfStr.size(); i++)
		tabOfNumbers_l[i] = atoll(vecOfStr[i].c_str());

	long long int* devNumbers;
	cudaMalloc((void**)&devNumbers, vecOfStr.size() * sizeof(long long int));
	int* devResults;
	cudaMalloc((void**)&devResults, vecOfStr.size() * sizeof(int));

	cudaMemcpy(devNumbers, tabOfNumbers_l, vecOfStr.size() * sizeof(long long int), cudaMemcpyHostToDevice);
	int* results = new int[vecOfStr.size()];
	cudaMemset(devResults, 0, vecOfStr.size() * sizeof(int));

	cudaEvent_t timeStart, timeEnd;
	float time;
	cudaEventCreate(&timeStart);
	cudaEventCreate(&timeEnd);
	cudaEventRecord(timeStart, 0);

	prime<<<vecOfStr.size(),BLOCK_SIZE>>>(devNumbers, devResults);

	cudaDeviceSynchronize();
	cudaEventRecord(timeEnd, 0);
	cudaEventSynchronize(timeEnd);
	cudaEventElapsedTime(&time, timeStart, timeEnd);

	cout << "Time: " << time << "ms\n";
	cudaMemcpy(results, devResults, vecOfStr.size() * sizeof(int), cudaMemcpyDeviceToHost);
	for (size_t i = 0; i < vecOfStr.size(); i++)
		cout << vecOfStr[i] << ": " << (results[i] ? "prime" : "composite") << "\n";

	cudaFree(devNumbers);
	cudaFree(devResults);
	free(tabOfNumbers_l);
	free(results);

	return 0;
}