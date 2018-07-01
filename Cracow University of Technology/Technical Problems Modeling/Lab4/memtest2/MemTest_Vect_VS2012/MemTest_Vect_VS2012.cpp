// MemTest_Vect_VS2012.cpp : Defines the entry point for the console application.
//

// Obliczenie dot = X*X
// odczyt duzej tablicy z pamieci glownej
//vectoryzowanie obliczen

#include "stdafx.h"
#include<iostream>
#include <cmath>
#include <emmintrin.h>
#include "windows.h"
using namespace std;


int _tmain(int argc, _TCHAR* argv[])
{
#ifdef VERSION_X64
	#ifdef _DEBUG
		cout << "VERSION DEBUG\n";
	#else
		cout << "VERSION RELEASE\n";
	#endif

	int N, ntimes, i, it;
	DWORD t_s;
	double *X = NULL, t_elaps;
	register double dot, dot1;
	const int L1 = 4096;  //ilosc slow double w cache L1 = 32 K

	cout << "Input: N, ntimes\n";
	cin >> N >> ntimes;

	N = N / L1;
	N = N*L1;
	//teraz N jest wielokrotne L1 - dzieli sie bez reszty na 4, 8 ,..., 2*exp(k) <= 4096

	cout << "N = " << N << " ntimes = " << ntimes << endl;

	try
	{
		X = (double *)_aligned_malloc(N*sizeof(double), 16);
		if (!X)
			throw 10;
	}
	catch (int)
	{
		cout << "Memory allocation error\n";
		exit(1);
	}

	for (i = 0; i<N; i++)
	{
		X[i] = sqrt((double)(i + 1));
	}

	//--------Odczyt klasyczny (kod naiwny)
	t_s = GetTickCount();
	for (it = 0; it<ntimes; it++)
	{
		dot = 0.0;
		for (i = 0; i<N; i++)
		{
			dot += X[i] * X[i];
		}
	}
	t_elaps = (double)(GetTickCount() - t_s);

	cout << "classical access:                           " << t_elaps << " msek" << endl;

	//---------------SSE2----------------------//
	//this approach does not support piplines of processor
	__m128d c1, c2, c3, c4, sum;
	__declspec(align(16)) double res[2];

	t_s = GetTickCount();
	//number of double words in cache line 64B is 8
	for (it = 0; it<ntimes; it++)
	{
		sum = _mm_setzero_pd();

		for (i = 0; i<N; i += 8)
		{
			//_mm_prefetch((const char *)(&X[i+8]), _MM_HINT_NTA);
			_mm_prefetch((const char *)(&X[i + 8]), _MM_HINT_T0);
			c1 = _mm_load_pd(&X[i]);     //load X[i], X[i+1]   to c1
			c2 = _mm_load_pd(&X[i + 2]);   //load X[i+2], X[i+3] to c2
			c3 = _mm_load_pd(&X[i + 4]);   //load X[i+4], X[i+5] to c3
			c4 = _mm_load_pd(&X[i + 6]);   //load X[i+6], X[i+7] to c4

			//this fragment of code generates hazards of pipelines
			c1 = _mm_mul_pd(c1, c1);    //tmp <- c1*c1
			sum = _mm_add_pd(sum, c1);  //sum = sum + c1*c1
			c2 = _mm_mul_pd(c2, c2);    //tmp <- c2*c2
			sum = _mm_add_pd(sum, c2);  //sum = sum + c1*c1 + c2*c2 
			c3 = _mm_mul_pd(c3, c3);    //tmp <- c3*c3
			sum = _mm_add_pd(sum, c3);  //sum = sum + c1*c1 + c2*c2 + c3*c3 
			c4 = _mm_mul_pd(c4, c4);    //tmp <- c4*c4
			sum = _mm_add_pd(sum, c4);  //sum = sum + c1*c1 + c2*c2 + c3*c3 +c4*c4
		}
		_mm_store_pd(res, sum);         //unload res <- sum
		dot1 = res[0] + res[1];            //finally: dot1 = res[0]+res[1];
	}
	t_elaps = (double)(GetTickCount() - t_s);

	cout << "SSE2: x4  prefetch x 8   !pipelines         " << t_elaps << " msek" << endl;

	if ((dot - dot1)*(dot - dot1) >= 1.0e-16)
	{
		cout << "Error: dot = " << dot << "  dot1 = " << dot1 << endl;
	}

	//---------------SSE2----------------------//
	//this approach does not support piplines of processor
	__m128d sum1, sum2, sum3;

	t_s = GetTickCount();
	//number of double words in cache line 64B is 8
	for (it = 0; it<ntimes; it++)
	{
		sum3 = sum2 = sum1 = sum = _mm_setzero_pd();

		for (i = 0; i<N; i += 8)
		{
			//_mm_prefetch((const char *)(&X[i+8]), _MM_HINT_NTA);
			_mm_prefetch((const char *)(&X[i + 8]), _MM_HINT_T0);
			c1 = _mm_load_pd(&X[i]);       //load X[i], X[i+1]   to c1
			c2 = _mm_load_pd(&X[i + 2]);   //load X[i+2], X[i+3] to c2
			c3 = _mm_load_pd(&X[i + 4]);   //load X[i+4], X[i+5] to c3
			c4 = _mm_load_pd(&X[i + 6]);   //load X[i+6], X[i+7] to c4
			
			//In this version pipelines should pass from one instruction to another
			//without ani hazards
			c1 = _mm_mul_pd(c1, c1);    //tmp <- c1*c1
			c2 = _mm_mul_pd(c2, c2);    //tmp <- c2*c2
			c3 = _mm_mul_pd(c3, c3);    //tmp <- c3*c3
			c4 = _mm_mul_pd(c4, c4);    //tmp <- c4*c4

			sum = _mm_add_pd(sum, c1);  //sum = sum + c1*c1
			sum1 = _mm_add_pd(sum1, c2);  //sum = sum + c1*c1 + c2*c2 
			sum2 = _mm_add_pd(sum2, c3);  //sum = sum + c1*c1 + c2*c2 + c3*c3 
			sum3 = _mm_add_pd(sum3, c4);  //sum = sum + c1*c1 + c2*c2 + c3*c3 +c4*c4
		}
		sum = _mm_add_pd(sum, sum1);
		sum2 = _mm_add_pd(sum2, sum3);
		sum = _mm_add_pd(sum, sum2);
		_mm_store_pd(res, sum);         //unload res <- sum
		dot1 = res[0] + res[1];            //finally: dot1 = res[0]+res[1];
	}
	t_elaps = (double)(GetTickCount() - t_s);

	cout << "SSE2: x4  prefetch x 8    pipelines         " << t_elaps << " msek" << endl;

	if ((dot - dot1)*(dot - dot1) >= 1.0e-16)
	{
		cout << "Error: dot = " << dot << "  dot1 = " << dot1 << endl;
	}

	_aligned_free(X);
	X = NULL;

#else
	cout << "platform ia32 is not supported\n";
#endif

	system("pause");
	return 0;
}

