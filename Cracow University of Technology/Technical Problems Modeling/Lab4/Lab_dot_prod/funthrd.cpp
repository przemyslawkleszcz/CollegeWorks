//////////////////////////////////////////////////////////////////////////////////
//   funthrd.cpp
//   thread function implementations

#include "stdafx.h"
#include <iostream>
#include "timethrd.h"
#include<cmath>
#include <intrin.h>
using namespace std;

DWORD WINAPI ThreadFunc1(LPVOID lpParam)
{
	THREAD_DATA *ptrDat = (THREAD_DATA *)(lpParam);

	register double tmp;
	for (int j = 0; j < ptrDat->ntimes; j++) {
		tmp = 0;
		for (int i = ptrDat->threadNo*ptrDat->loc_N; i < (ptrDat->threadNo + 1)*ptrDat->loc_N; ++i) {
			tmp += ptrDat->X[i] * ptrDat->Y[i];
		}
		ptrDat->res = tmp;
	}

	ptrDat->ret = 0;
	return 0;
}

DWORD WINAPI ThreadFunc2(LPVOID lpParam)
{
	THREAD_DATA *ptrDat = (THREAD_DATA *)(lpParam);
	__m128d sum0, sum1, sum2, sum3;
	__m128d x0, x1, x2, x3;
	__m128d y0, y1, y2, y3;
	_declspec(align(16)) double buf0[2];
	_declspec(align(16)) double buf1[2];
	_declspec(align(16)) double buf2[2];
	_declspec(align(16)) double buf3[2];
	double *ptrX = ptrDat->X;
	double *ptrY = ptrDat->Y;
	int start = ptrDat->threadNo*ptrDat->loc_N;
	int stop = (ptrDat->threadNo + 1)*ptrDat->loc_N;
	int lb = 8;
	int rest = ptrDat->loc_N % lb;
	for (int j = 0; j < ptrDat->ntimes; ++j) {
		sum0 = sum1 = sum2 = sum3 = _mm_setzero_pd();
		int i;
		for (i = start; i < stop - rest; i += lb) {
			
			x0 = _mm_loadu_pd(ptrX + i);
			y0 = _mm_loadu_pd(ptrY + i);
			x0 = _mm_mul_pd(x0, y0);
			sum0 = _mm_add_pd(sum0, x0);
			
			x1 = _mm_loadu_pd(ptrX + i + 2);
			y1 = _mm_loadu_pd(ptrY + i + 2);
			x1 = _mm_mul_pd(x1, y1);
			sum1 = _mm_add_pd(sum1, x1);
			
			x2 = _mm_loadu_pd(ptrX + i + 4);
			y2 = _mm_loadu_pd(ptrY + i + 4);
			x2 = _mm_mul_pd(x2, y2);
			sum2 = _mm_add_pd(sum2, x2);
			
			x3 = _mm_loadu_pd(ptrX + i + 6);
			y3 = _mm_loadu_pd(ptrY + i + 6);
			x3 = _mm_mul_pd(x3, y3);
			sum3 = _mm_add_pd(sum3, x3);
		}
		_mm_store_pd(buf0, sum0);
		ptrDat->res = buf0[0] + buf0[1];
		_mm_store_pd(buf1, sum1);
		ptrDat->res += buf1[0] + buf1[1];
		_mm_store_pd(buf2, sum2);
		ptrDat->res += buf2[0] + buf2[1];
		_mm_store_pd(buf3, sum3);
		ptrDat->res += buf3[0] + buf3[1];
		for (i = stop - rest; i < stop; ++i) {
			ptrDat->res += *(ptrX + i) * *(ptrY + i);
		}
	}

	ptrDat->ret = 0;
	return 0;
}

DWORD WINAPI ThreadFunc3(LPVOID lpParam)
{
	THREAD_DATA *ptrDat = (THREAD_DATA *)(lpParam);

	__m256d sum0, sum1, sum2, sum3, rx0, rx1, rx2, rx3, ry0, ry1, ry2, ry3;
	double *ptr_x, *ptr_y;

	_declspec(align(32)) double buf0[4];
	_declspec(align(32)) double buf1[4];
	_declspec(align(32)) double buf2[4];
	_declspec(align(32)) double buf3[4];

	int buf_size = 4;

	int nr = 16;
	int rest = ptrDat->loc_N % nr;

	int it = 0;
	int i = 0;
	int k = 0;

	register double dot = 0;

	for (it = 0; it < ptrDat->ntimes; it++) {

		sum0 = _mm256_setzero_pd();
		sum1 = _mm256_setzero_pd();;
		sum2 = _mm256_setzero_pd();;
		sum3 = _mm256_setzero_pd();

		ptr_x = ptrDat->X + (ptrDat->threadNo * ptrDat->loc_N);
		ptr_y = ptrDat->Y + (ptrDat->threadNo * ptrDat->loc_N);
		dot = 0;

		for (i = 0; i < ptrDat->loc_N - rest; i += nr) {

			_mm_prefetch((const char *)(ptr_x + nr), _MM_HINT_T0);
			_mm_prefetch((const char *)(ptr_y + nr), _MM_HINT_T0);
			_mm_prefetch((const char *)(ptr_x + 2* nr), _MM_HINT_T0);
			_mm_prefetch((const char *)(ptr_y + 2* nr), _MM_HINT_T0);

			rx0 = _mm256_load_pd(ptr_x);
			rx1 = _mm256_load_pd(ptr_x + 4);
			rx2 = _mm256_load_pd(ptr_x + 8);
			rx3 = _mm256_load_pd(ptr_x + 12);

			ry0 = _mm256_load_pd(ptr_y);
			ry1 = _mm256_load_pd(ptr_y + 4);
			ry2 = _mm256_load_pd(ptr_y + 8);
			ry3 = _mm256_load_pd(ptr_y + 12);

			rx0 = _mm256_mul_pd(rx0, ry0);
			rx1 = _mm256_mul_pd(rx1, ry1);
			rx2 = _mm256_mul_pd(rx2, ry2);
			rx3 = _mm256_mul_pd(rx3, ry3);

			sum0 = _mm256_add_pd(sum0, rx0);
			sum1 = _mm256_add_pd(sum1, rx1);
			sum2 = _mm256_add_pd(sum2, rx2);
			sum3 = _mm256_add_pd(sum3, rx3);

			ptr_x += nr;
			ptr_y += nr;
		}

		_mm256_store_pd(buf0, sum0);
		_mm256_store_pd(buf1, sum1);
		_mm256_store_pd(buf2, sum2);
		_mm256_store_pd(buf3, sum3);

		dot += buf0[0] + buf0[1] + buf0[2] + buf0[3]
			+ buf1[0] + buf1[1] + buf1[2] + buf1[3]
			+ buf2[0] + buf2[1] + buf2[2] + buf2[3]
			+ buf3[0] + buf3[1] + buf3[2] + buf3[3];

		for (i = 0; i < rest; i++) {
			dot += ptr_x[i] * ptr_y[i];
		}
	}

	ptrDat->res = dot;
	ptrDat->ret = 0;
	return 0;

	ptrDat->ret = 0;
	return 0;
}

DWORD WINAPI ThreadFunc4(LPVOID lpParam)
{
	THREAD_DATA *ptrDat = (THREAD_DATA *)(lpParam);

	for (int j = 0; j < ptrDat->ntimes; ++j)
	{
		ptrDat->res = 0;
		for (int i = ptrDat->threadNo*ptrDat->loc_N; i < (ptrDat->threadNo + 1)*ptrDat->loc_N; ++i) {
			ptrDat->res += ptrDat->X[i] * ptrDat->Y[i];
		}
	}

	ptrDat->ret = 0;
	return 0;
}