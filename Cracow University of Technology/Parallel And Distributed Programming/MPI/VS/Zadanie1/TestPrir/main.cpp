#include "stdafx.h"
#include <stdio.h>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include "mpi.h"

using namespace std;
using namespace cv;

MPI_Comm communicator;
double sstime;

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

Mat cut(Mat image, int x, int y, int width, int height) {
	return image(Rect(x, y, width, height)).clone();
}

void send(void * dane, int rozmiar, MPI_Datatype typ = MPI_LONG, int destination = 0, int tag = 0) {
	MPI_Send(dane, rozmiar, typ, destination, tag, communicator);
}

void receive(void * data, int size, MPI_Datatype type = MPI_LONG, int destination = 0, int tag = 0) {
	MPI_Recv(data, size, type, destination, tag, communicator, MPI_STATUS_IGNORE);
}

Mat gauss(Mat picture, Mat pictureNew, int kernel[][5], int weight) {
	int y, x;
	int r, g, b;
	int i_x, i_y;

	for (y = 2; y < picture.rows - 2; y++)
	{
		for (x = 2; x < picture.cols - 2; x++)
		{
			r = 0;
			g = 0;
			b = 0;

			for (i_y = 0; i_y < 5; i_y++) {
				for (i_x = 0; i_x < 5; i_x++) {
					Vec3b intensity = picture.at <Vec3b>(i_y + y - 2, i_x + x - 2);
					r += intensity.val[2] * kernel[i_x][i_y];
					g += intensity.val[1] * kernel[i_x][i_y];
					b += intensity.val[0] * kernel[i_x][i_y];
				}
			}

			Vec3b intensity2 = Vec3b();
			intensity2.val[2] = r / weight;
			intensity2.val[1] = g / weight;
			intensity2.val[0] = b / weight;
			pictureNew.at<Vec3b>(y - 2, x - 2) = intensity2;
		}
	}

	return pictureNew;
}

void InitMpi(int* argc, char*** argv, int* numberOfProcess, int* processes)
{
	MPI_Init(argc, argv);
	communicator = MPI_COMM_WORLD;
	MPI_Comm_rank(communicator, numberOfProcess);
	MPI_Comm_size(communicator, processes);
}

Mat sendImage(Mat& picture, int processes)
{
	copyMakeBorder(picture, picture, 2, 2, 2, 2, BORDER_REPLICATE);
	sstime = MPI_Wtime();
	Mat cutImg;

	for (int i = processes - 1; i > -1; i--)
	{
		int xStart = i * (picture.cols - 4) / processes;
		int xWid = (i + 1)*(picture.cols - 4) / processes - xStart + 4;

		cutImg = cut(picture, xStart, 0, xWid, picture.rows);

		int x = cutImg.cols;
		int y = cutImg.rows;

		if (i != 0)
		{
			send(&x, sizeof(int), MPI_LONG, i, 0);
			send(&y, sizeof(int), MPI_LONG, i, 1);
			send(cutImg.data, x*y * 3, MPI_BYTE, i, 2);
		}
	}

	return cutImg;
}

Mat receiveImage()
{
	int x, y;
	receive(&x, sizeof(int), MPI_LONG, 0, 0);
	receive(&y, sizeof(int), MPI_LONG, 0, 1);

	Mat cutImg = Mat(y, x, CV_8UC3);
	receive(cutImg.data, x*y * 3, MPI_BYTE, 0, 2);
	return cutImg;
}

void sendResultsBack(Mat& pictureNew)
{
	int ox = pictureNew.cols;
	int oy = pictureNew.rows;

	send(&ox, sizeof(int), MPI_LONG, 0, 0);
	send(&oy, sizeof(int), MPI_LONG, 0, 1);
	send(pictureNew.data, ox*oy * 3, MPI_BYTE, 0, 2);
}

Mat receiveResults(Mat& pictureNew, int processes)
{
	for (int i = 1; i < processes; i++)
	{
		int tx, ty;
		receive(&tx, sizeof(int), MPI_LONG, i, 0);
		receive(&ty, sizeof(int), MPI_LONG, i, 1);

		Mat tempImg = Mat(ty, tx, CV_8UC3);
		receive(tempImg.data, tx*ty * 3, MPI_BYTE, i, 2);
		hconcat(pictureNew, tempImg, pictureNew);
	}

	return pictureNew;
}

void produceOutput(Mat& pictureNew, char** argv)
{
	double endTime = 1000 * (MPI_Wtime() - sstime);
	cout << "Czas: " << endTime << "ms\n";
	imwrite(argv[2], pictureNew);
}

int main(int argc, char** argv)
{
	if (!checkArgumentsNumber(argc))
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

	int processes, numberOfProcess;
	InitMpi(&argc, &argv, &numberOfProcess, &processes);
	
	Mat obrazcut;
	if (numberOfProcess == 0)
		obrazcut = sendImage(picture, processes);
	else
		obrazcut = receiveImage();

	Mat pictureNew = obrazcut.clone();
	int weight = calculateWeight(kernel);
	pictureNew = cut(pictureNew, 2, 2, pictureNew.cols - 4, pictureNew.rows - 4);
	pictureNew = gauss(obrazcut, pictureNew, kernel, weight);

	if (numberOfProcess != 0)
		sendResultsBack(pictureNew);
	else 
	{
		pictureNew = receiveResults(pictureNew, processes);
		produceOutput(pictureNew, argv);
	}

	MPI_Finalize();
	return 0;
}



