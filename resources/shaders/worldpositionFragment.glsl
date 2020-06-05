#version 430 core
#define M_PI 3.1415926535897932384626433832795

layout(std430, binding = 0) buffer ColorSSBO {
	float color[];
};

//in vec3 passColor;
in vec2 passTextureCoord;

//out vec4 outColor;

//uniform sampler2D tex;

uniform mat4 view;
uniform mat4 projection;

uniform mat4 viewInv;
uniform mat4 projectionInv;

uniform int screenWidth;
uniform int screenHeight;
//uniform float[512] dataSet;
uniform vec3 aabb1;
uniform vec3 aabb2;

uniform int dcmsAmount;

uniform float fov;

uniform vec3 cameraPos;
//uniform vec3 cameraVec;

uniform int sampleResolution;

void Swap(inout float a, inout float b){
	float temp = a;
	a = b;
	b = temp;
}

vec3 Intersect(vec3 rayOrigin, vec3 rayDirection, vec3[2] axisAlighenBoundingBox, bool returnFirst){
	vec3[] result = vec3[2](vec3(0.0, 0.0, 0.0), vec3(0.0, 0.0, 0.0));
	
	//Box boundaries:
	float B0x = axisAlighenBoundingBox[0].x;
	float B1x = axisAlighenBoundingBox[1].x;
	float B0y = axisAlighenBoundingBox[0].y;
	float B1y = axisAlighenBoundingBox[1].y;
	float B0z = axisAlighenBoundingBox[0].z;
	float B1z = axisAlighenBoundingBox[1].z;
	
	//Ray assignment
	float Ox = rayOrigin.x;
	float Oy = rayOrigin.y;
	float Oz = rayOrigin.z;
	float Dx = rayDirection.x;
	float Dy = rayDirection.y;
	float Dz = rayDirection.z;
	
	//Calculation
	float t0x = (B0x - Ox) / Dx;
	float t1x = (B1x - Ox) / Dx;
	
	if (t0x > t1x) Swap(t0x, t1x);
	
	float t0y = (B0y - Oy) / Dy;
	float t1y = (B1y - Oy) / Dy;
	
	if (t0y > t1y) Swap(t0y, t1y);
	
	if (t0x > t1y || t0y > t1x)
		discard;
	
	if (t0y > t0x)
		t0x = t0y;
		
	if (t1y < t1x)
		t1x = t1y;
	
	float t0z = (B0z - Oz) / Dz;
	float t1z = (B1z - Oz) / Dz;
	
	if (t0z > t1z) Swap(t0z, t1z);
	
	if (t0x > t1z || t0z > t1x)
		discard;
		
	if (t0z > t0x)
		t0x = t0z;
		
	if (t1z < t1x)
		t1x = t1z;
	
	float tMin = t0x;
	float tMax = t1x;
	
	result[0] = vec3(rayDirection.x * tMin + rayOrigin.x, rayDirection.y * tMin + rayOrigin.y, rayDirection.z * tMin + rayOrigin.z);
	result[1] = vec3(rayDirection.x * tMax + rayOrigin.x, rayDirection.y * tMax + rayOrigin.y, rayDirection.z * tMax + rayOrigin.z);
	
	return returnFirst ? result[0] : result[1];
}

vec3 First(vec3 vOrig, vec3 vDir, vec3[2] aabb){
	return Intersect(vOrig, vDir, aabb, true);
}

vec3 Last(vec3 vOrig, vec3 vDir, vec3[2] aabb){
	return Intersect(vOrig, vDir, aabb, false);
}

vec3 Object(vec3 v){
	vec4 result;
	result = inverse(projection * view) * vec4(v, 1.0);
	return result.xyz;
}

vec3 Image(vec3 v){
	vec4 result;
	result = projection * view * vec4(v, 1.0);
	return result.xyz;
}

vec3 Data(vec3 v, int SIZE) {
	//to data space
	vec3 result;
	
	//SIZE -= 1;

	vec3 resolution = vec3(SIZE, SIZE, dcmsAmount);
	vec3 offset = vec3(0.5, 0.5, 0.5);
	
	result = (v + offset) * resolution;
	return result;
}

float Sample(vec3 c, int SIZE){
	float result;
	
	//SIZE -= 1;
	
	int x0, x1, y0, y1, z0, z1;
	/*
	float xd, yd, zd;
	float c000, c001, c010, c011, c100, c101, c110, c111;
	float c00, c01, c10, c11;
	float c0, c1;
	*/
	float xd = mod(c.x, 1.0);
	float yd = mod(c.y, 1.0);
	float zd = mod(c.z, 1.0);
	
	/*
	if(c.x == SIZE){
		x0 = SIZE-1;
		x1 = SIZE;
		xd = 1.0;
	} else {
		x0 = int(c.x);
		x1 = int(c.x) + 1;
		xd = mod(c.x, 1);
	}
	
	if(c.y == SIZE){
		y0 = SIZE-1;
		y1 = SIZE;
		yd = 1.0;
	} else {
		y0 = int(c.y);
		y1 = int(c.y) + 1;
		yd = mod(c.y, 1);
	}
	
	if(c.z == SIZE){
		z0 = SIZE-1;
		z1 = SIZE;
		zd = 1.0;
	} else {
		z0 = int(c.z);
		z1 = int(c.z) + 1;
		zd = mod(c.z, 1);
	}
	*/
	
	if(xd == 0.0){
		x0 = int(c.x);
		x1 = x0;
	} else {
		x0 = int(c.x);
		x1 = int(c.x) + 1;
	}
	
	if(yd == 0.0){
		y0 = int(c.y);
		y1 = y0;
	} else {
		y0 = int(c.y);
		y1 = int(c.y) + 1;
	}
	
	if(zd == 0.0){
		z0 = int(c.z);
		z1 = z0;
	} else {
		z0 = int(c.z);
		z1 = int(c.z) + 1;
	}
	
	z0 = 0;
	z1 = 0;
	
	float c000 = color[x0 + y0 * SIZE + z0 * SIZE * SIZE];
	float c001 = color[x0 + y0 * SIZE + z1 * SIZE * SIZE];
	float c010 = color[x0 + y1 * SIZE + z0 * SIZE * SIZE];
	float c011 = color[x0 + y1 * SIZE + z1 * SIZE * SIZE];
	float c100 = color[x1 + y0 * SIZE + z0 * SIZE * SIZE];
	float c101 = color[x1 + y0 * SIZE + z1 * SIZE * SIZE];
	float c110 = color[x1 + y1 * SIZE + z0 * SIZE * SIZE];
	float c111 = color[x1 + y1 * SIZE + z1 * SIZE * SIZE];
	
	float c00 = c000 * (1 - xd) + c100 * xd;
	float c01 = c001 * (1 - xd) + c101 * xd;
	float c10 = c010 * (1 - xd) + c110 * xd;
	float c11 = c011 * (1 - xd) + c111 * xd;
	
	float c0 = c00 * (1 - yd) + c10 * yd;
	float c1 = c01 * (1 - yd) + c11 * yd;
	
	result = (c0 * (1 - zd) + c1 * zd) / sampleResolution /10000;
	
	return result;
}

void main() {
	int SIZE = 384;
	vec3[] aabb = vec3[2](aabb1, aabb2);
	
	//float[] dataSet;

	//float aspect = screenWidth/screenHeight;
	
	//float right = 1.0f * aspect, left = -1.0f * aspect, top = 1.0f, bottom = -1.0f;
	//float range = 2.0;

	float tx = (float(gl_FragCoord.x) / float(screenWidth)) * 2.0 - 1.0;
	float ty = (float(gl_FragCoord.y) / float(screenHeight)) * 2.0 - 1.0;
	vec3 rayOrigin = Object(vec3(tx, ty, 0.0));		//in object-space
	vec3 rayDirection = normalize(Object(vec3(tx, ty, -1.0)) - rayOrigin);
	
	vec3 modulation = rayDirection / sampleResolution;
	
	//vec3 c = vec3(0, 0, 0);
	vec3 C = vec3(1, 1, 1);
	float alpha = 0;
	float Alpha = 0;
	
	vec3 x1 = First(rayOrigin, rayDirection, aabb);// * vec3(0.999, 0.999, 0.999);
	vec3 x2 = Last(rayOrigin, rayDirection, aabb);// * vec3(0.999, 0.999, 0.999);
	
	//float a = SIZE * sqrt(3);
	
	//float U1 = Image(x1).z;
	//float U2 = Image(x2).z;
	//int range = int(a * (U1 - U2));
	
	//vec3 x = x1;//vec3((x1.x + 0.5) * (SIZE - 1), (x1.y + 0.5) * (SIZE - 1), (x1.z + 0.5) * (SIZE - 1));	//нормализация, "- 1" потомучто в алгоритме Sample координата находится на "+ 1"
	vec3 x = Data(x1, SIZE);
	
	//vec3 offset = vec3(0.5, 0.5, 0.5);
	
	//x1 = x1 + offset;
	//x2 = x2 + offset;
	// Loop through all samples falling within data
	//for (int U = U1; U < U2; U++){
	//for (int i = 0; i < range; i++){
	float temp = (Image(x1).z + 0.5) / 2;
	
	gl_FragColor = vec4(temp,temp,temp, 255); //Alpha; //vec4(c, Alpha);
	//gl_FragColor = vec4(x, 1.0);
}
