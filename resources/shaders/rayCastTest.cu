extern "C" __global__ void render(float *fb) {
    int o = threadIdx.x + blockIdx.x * blockDim.x;
    int s = threadIdx.y + blockIdx.y * blockDim.y;
	int i = o % 1280;
    if(o > 921600) return;
    fb[o] = float(i) / 1280;
}
/*
extern "C" __global__ void matSum(int *a, int *b, int *c) {
    int i = threadIdx.x + blockIdx.x * blockDim.x;
    int j = threadIdx.y + blockIdx.y * blockDim.y;
	i = i % 1280;
	j = i / 1280;
    if((i >= 1280) || (j >= 720)) return;
    int pixel_index = j*1280*3 + i*3;
    c[pixel_index + 0] = i;
    c[pixel_index + 1] = j;
    c[pixel_index + 2] = 1;
}*/
/*
__global__ void render(float *fb, int max_x, int max_y) {
    int i = threadIdx.x + blockIdx.x * blockDim.x;
    int j = threadIdx.y + blockIdx.y * blockDim.y;
    if((i >= max_x) || (j >= max_y)) return;
    int pixel_index = j*max_x*3 + i*3;
    fb[pixel_index + 0] = float(i) / max_x;
    fb[pixel_index + 1] = float(j) / max_y;
    fb[pixel_index + 2] = 0.2;
}
*/