# compile
METHOD = nvcc
# compilation
compilation: main.cu
	$(METHOD) main.cu -o gauss_gpu `pkg-config --libs opencv`