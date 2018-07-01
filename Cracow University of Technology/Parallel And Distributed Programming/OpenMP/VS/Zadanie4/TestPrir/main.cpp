#include "stdafx.h"
#include <iostream>
#include <omp.h>
#include <string>
#include <fstream>
#include <vector>
#include <stdio.h>
#include <stdlib.h>

using namespace std;

int checkArgumentsNumber(int argc)
{
	int maxArgumentCount = 3;
	if (argc != maxArgumentCount)
	{
		std::cout << "Program wymaga dwoch argumentow.\n";
		return 0;
	}

	return 1;
}

int getThreadsNumber(char** argv)
{
	int numberOfThreads = strtol(argv[1], NULL, 10);
	return numberOfThreads;
}

int checkThreadsNumber(int numberOfThreads)
{
	if (numberOfThreads <= 0) {
		std::cout << "Ilosc watkow powinna byc wieksza od 0.\n";
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

void printOutput(double start, double end, std::vector<std::string> vecOfRes)
{
	double time = 1000 * (end - start);
	std::cout << "Time: " << time << "ms\n";
	for (int i = 0; ((unsigned)i) < vecOfRes.size(); i++)
		std::cout << vecOfRes[i] << "\n";
}

int prime_number(string tnumber, int numOfThreads)
{
	int prime = 1;
	long long j;

	long long number = atoll(tnumber.c_str());
	long long sqrtNumber = sqrtl(number);
	bool flag = false;

	#pragma omp parallel for shared(number, prime, flag, sqrtNumber) num_threads(numOfThreads) private(j) schedule(guided, 1)
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

int main(int argc, char *argv[])
{
	if (!checkArgumentsNumber(argc))
		return -1;

	int threadsNumber = getThreadsNumber(argv);
	if (!checkThreadsNumber(threadsNumber))
		return -1;

	ifstream inFile;
	inFile.open(argv[2]);
	int isOk = isFileOk(inFile);
	if (!isOk)
		return -1;

	std::vector<std::string> vecOfStr = getFileLines(inFile);
	std::vector<std::string> vecOfRes;

	double start = omp_get_wtime();
	for (int i = 0; ((unsigned)i) < vecOfStr.size(); i++)
	{
		int isPrime = prime_number(vecOfStr[i], threadsNumber);
		vecOfRes.push_back(vecOfStr[i] + ": " + (isPrime ? "prime" : "composite"));
	}

	double end = omp_get_wtime();
	printOutput(start, end, vecOfRes);

	return 0;
}



