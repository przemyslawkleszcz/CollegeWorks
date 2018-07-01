// Lab_dot_prod.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <iostream>
#include <cmath>
#include <ctime>
#include "timethrd.h"

using namespace std;
#define NUMB_THREADS 10

int _tmain(int argc, _TCHAR* argv[])
{
	HANDLE hThread[NUMB_THREADS];
	DWORD ThreadID[NUMB_THREADS];
	THREAD_DATA tDat[NUMB_THREADS];
	double *X = NULL, *Y = NULL;
	double dotProd;

	int N, ip, np, loc_N;
	int ntimes;

	DWORD(__stdcall*funTab[])(LPVOID lpParam) =
	{
		/*ThreadFunc1,
		ThreadFunc2,*/
		ThreadFunc3,
		/*ThreadFunc4*/
	};

#ifdef _DEBUG
	cout << "START dotProd: DEBUG VERSION\n";
#else
	cout << "START dotProd: RELEASE VERSION\n";
#endif
	cout << "Get N\n";
	cin >> N;
	cout << "Get number of processors\n";
	cin >> np;
	cout << "Get number of repetitions\n";
	cin >> ntimes;

	loc_N = N / np;
	N = loc_N * np;

	cout << "actual N = " << N << endl;
	cout << "np = " << np << endl;

	try
	{
		X = new double[N];
		Y = new double[N];
	}
	catch (bad_alloc aa)
	{
		cout << "memory allocation error\n";
		system("pause");
		exit(1);
	}

	//preparation of X, Y
	int i;
	for (i = 0; i < N; i++)
	{
		X[i] = (double)(i + 1);
		Y[i] = 1.0;
	}

	for (size_t imethod = 0; imethod < sizeof(funTab) / sizeof(funTab[0]); ++imethod)
	{
		cout << "--------------------------\n";
		cout << "Method " << imethod << "\n";

		DWORD time_st = GetTickCount();

		for (ip = 0; ip < np; ip++)
		{
			//wypelnic dane dla potoku ip
			tDat[ip].loc_N = loc_N;
			tDat[ip].N = N;
			tDat[ip].NoThreads = np;
			tDat[ip].ntimes = ntimes;
			tDat[ip].threadNo = ip;
			tDat[ip].X = X;
			tDat[ip].Y = Y;
			tDat[ip].res = 0;

			//stworzyc potok ip
			hThread[ip] = CreateThread(NULL, 0, funTab[imethod], &tDat[ip], 0, NULL);
		}

		//zawiesic pierwotny potok dokad potoki liczace nie skonca prace
		WaitForMultipleObjects(np, hThread, true, INFINITE);

		//Check
		bool IsOK = true;
		for (ip = 0; ip < np; ip++)
		{
			if (tDat[ip].ret != 0) //TODO
			{
				cout << " Thread # " << ip + 1 << " failed" << endl;
				IsOK = false;
			}
			CloseHandle(hThread[ip]);
		}

		if (!IsOK)
		{
			system("pause");
			exit(1);
		}

		//reduction: sumowanie 
		dotProd = 0.0;

		for (ip = 0; ip < np; ip++)
		{
			dotProd += tDat[ip].res;
		}

		cout << "WTime = " << GetTickCount() - time_st << " ms\n";

		//check
		double sum = 0.0;
		for (i = 0; i < N; i++)
		{
			sum += X[i] * Y[i];
		}

		if ((fabs(sum - dotProd)) > 1.0e-9)
		{
			cout << "error\n";
			cout << "!!!!!!!!!!!\n";
		}
	}

	cout << "END MV\n";
	system("pause");

	delete[] X;
	delete[] Y;

	return 0;
}