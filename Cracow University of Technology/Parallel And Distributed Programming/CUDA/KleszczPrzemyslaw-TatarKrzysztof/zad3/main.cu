#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

#define BLOCK_SIZE 16

int checkArgumentsNumber(int argc)
{
	int maxArgumentCount = 3;
	if (argc != maxArgumentCount)
	{
		std::cout << "Program wymaga dwóch argumentow.\n";
		return 0;
	}

	return 1;
}

Mat getPicture(char** argv)
{
	Mat picture = imread(argv[1], CV_LOAD_IMAGE_COLOR);
	return picture;
}

int isImageExist(Mat image)
{
	if (!image.data)
	{
		std::cout << "Obraz nie istnieje.\n";
		return 0;
	}

	return 1;
}

__global__
void gaussian(uchar * picture, uchar * pictureNew, long sizeX, long sizeY)
{
	int mask[5][5] = 
	{
		{ 1,4 ,7 ,4 ,1 },
		{ 4,16,26,16,4 },
		{ 7,26,41,26,7 },
		{ 4,16,26,16,4 },
		{ 1,4 ,7 ,4 ,1 }
	};

	int weight = 273;
	long x = blockIdx.x * blockDim.x + threadIdx.x;
	long y = blockIdx.y * blockDim.y + threadIdx.y;

	if (x < sizeX - 2 && y < sizeY - 2 && x>1 && y>1)
	{
		long r = 0, g = 0, b = 0;
		long wInput, wOutput;

		for (int i_y = 0; i_y < 5; i_y++) 
		{
			for (int i_x = 0; i_x < 5; i_x++) 
			{
				wInput = sizeX * (y + i_y - 2) * 3 + (x + i_x - 2) * 3;
				r += picture[wInput + 2] * mask[i_x][i_y];
				g += picture[wInput + 1] * mask[i_x][i_y];
				b += picture[wInput] * mask[i_x][i_y];
			}
		}

		wOutput = (sizeX - 4)*(y - 2) * 3 + (x - 2) * 3;
		pictureNew[wOutput + 2] = r / weight;
		pictureNew[wOutput + 1] = g / weight;
		pictureNew[wOutput] = b / weight;
	}
}


dim3 getGridDim(Mat picture)
{
	int gridX, gridY;
	gridX = picture.cols / BLOCK_SIZE + 1;
	gridY = picture.rows / BLOCK_SIZE + 1;
	dim3 gridy(gridX, gridY);
	return gridy;
}

int main(int argc, char** argv)
{
	if (!checkArgumentsNumber(argc))
		return -1;

	Mat picture = getPicture(argv);
	if (!isImageExist(picture))
		return -1;

	Mat pictureNew = Mat(picture.rows, picture.cols, CV_8UC3);
	copyMakeBorder(picture, picture, 2, 2, 2, 2, BORDER_REPLICATE);

	dim3 blocks(BLOCK_SIZE, BLOCK_SIZE);
	dim3 grids = getGridDim(picture);

	long sizeIn = sizeof(uchar) * picture.rows* picture.cols * 3;
	long sizeOut = sizeof(uchar) * pictureNew.rows* pictureNew.cols * 3;

	uchar * devPicture;
	uchar * devPictureNew;

	cudaMalloc((void**)& devPicture, sizeIn);
	cudaMalloc((void**)& devPictureNew, sizeOut);

	if (cudaMemcpy(devPicture, picture.data, sizeIn, cudaMemcpyHostToDevice) != cudaSuccess) 
		cout << "Wystapil blad podczas kopiowania CPU -> GPU\n";

	cudaEvent_t timeStart, timeEnd;
	float time;
	cudaEventCreate(&timeStart);
	cudaEventCreate(&timeEnd);
	cudaEventRecord(timeStart, 0);

	gaussian<<<grids, blocks>>>(devPicture, devPictureNew, picture.cols, picture.rows);
	cudaDeviceSynchronize();

	cudaEventRecord(timeEnd, 0);
	cudaEventSynchronize(timeEnd);
	cudaEventElapsedTime(&time, timeStart, timeEnd);

	if (cudaMemcpy(pictureNew.data, devPictureNew, sizeOut, cudaMemcpyDeviceToHost) != cudaSuccess) 
		cout << "Wystapil blad podczas kopiowania GPU -> CPU\n";
	
	imwrite(argv[2], pictureNew);
	cout << "Czas: " << time << "ms\n";

	cudaFree(devPicture);
	cudaFree(devPictureNew);
	return 0;
}

