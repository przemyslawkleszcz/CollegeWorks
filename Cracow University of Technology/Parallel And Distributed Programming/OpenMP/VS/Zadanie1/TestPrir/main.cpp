#include "stdafx.h"
#include <stdio.h>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <omp.h>

using namespace std;
using namespace cv;

int calculateWeight(int kernel[][5]) {
	int weight = 0;
	for (int i = 0; i < 5; i++) {
		for (int j = 0; j < 5; j++) {
			weight += kernel[i][j];
		}
	}

	return weight;
}

int checkArgumentsNumber(int argc)
{
	int maxArgumentCount = 4;
	if (argc != maxArgumentCount)
	{
		std::cout << "Program wymaga trzech argumentow.\n";
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

Mat getPicture(char** argv)
{
	Mat picture = imread(argv[2], CV_LOAD_IMAGE_COLOR);
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

void gaussian(int weight, Mat picture, Mat pictureNew, int kernel[][5], int threadsNumber)
{
	int y, x;
	int r, g, b;

#pragma omp parallel shared(picture,pictureNew) num_threads(threadsNumber) private(r,g,b,y,x)
	{
#pragma omp for schedule(guided,1)
		for (y = 0; y < picture.rows; y++)
		{
			for (x = 0; x < picture.cols; x++)
			{
				if (x > 1 && y > 1 && x < picture.cols - 2 && y < picture.rows - 2)
				{
					r = 0;
					g = 0;
					b = 0;

					for (int kernelX = 0; kernelX < 5; kernelX++) {
						for (int kernelY = 0; kernelY < 5; kernelY++) {

							//y = 3, x = 5
							Vec3b intensity = picture.at<Vec3b>(y, x);

							//kernelX = 0, kernelY = 0
							/*Vec3b intensity = picture.at<Vec3b>(kernelY + y - 2, kernelX + x - 2);*/
							//kernelY + y - 2 -> 1
							//kernelX + x - 2 -> 3

							r += intensity.val[2] * kernel[kernelX][kernelY];
							g += intensity.val[1] * kernel[kernelX][kernelY];
							b += intensity.val[0] * kernel[kernelX][kernelY];
						}
					}

					Vec3b intensity2 = Vec3b();
					intensity2.val[0] = b / weight;
					intensity2.val[1] = g / weight;
					intensity2.val[2] = r / weight;
					pictureNew.at<Vec3b>(y, x) = intensity2;
				}
				else {
					Vec3b intensity = picture.at<Vec3b>(y, x);
					pictureNew.at<Vec3b>(y, x) = intensity;
				}
			}
		}
	}
}

int main(int argc, char** argv)
{
	if (!checkArgumentsNumber(argc))
		return -1;

	int threadsNumber = getThreadsNumber(argv);
	if (!checkThreadsNumber(threadsNumber))
		return -1;

	Mat picture = getPicture(argv);
	if (!isImageExist(picture))
		return -1;

	int kernel[5][5] =
	{
		{ 1,4,7,4,1 },
		{ 4,16,26,16,4 },
		{ 7,26,41,26,7 },
		{ 4,16,26,16,4 },
		{ 1,4,7,4,1 }
	};

	Mat pictureNew = picture.clone();
	int weight = calculateWeight(kernel);
	double start = omp_get_wtime();
	gaussian(weight, picture, pictureNew, kernel, threadsNumber);

	double end = omp_get_wtime();
	imwrite(argv[3], pictureNew);
	double time = 1000 * (end - start);
	std::cout << "Czas: " << time << "ms\n";
	return 0;
}

