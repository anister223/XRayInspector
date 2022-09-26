#version 430 core

layout(std430, binding = 0) buffer ColorSSBO {
	int color[];
};

in vec4 gl_FragCoord;

layout(location = 0) out vec4 diffuseColor;

uniform mat4 view;
uniform mat4 projection;

uniform int dataWidth;
uniform int dataHeight;
uniform int dataAmount;

uniform int screenWidth;
uniform int screenHeight;
uniform vec3 aabb1;
uniform vec3 aabb2;
uniform int sampleResolution;

uniform float correction;

uniform float treshold;

uniform bool useInterpolation;

void Swap(inout float a, inout float b){
	float temp = a;
	a = b;
	b = temp;
}

void Swap(inout int a, inout int b){
	int temp = a;
	a = b;
	b = temp;
}

vec3 Grad(vec3 c, vec3 rayDirection){
	vec3 result;
	
	int sizeX, sizeY, sizeZ;
	
	int x0, x1, y0, y1, z0, z1;
	
	float xd = mod(c.x, 1.0);
	float yd = mod(c.y, 1.0);
	float zd = mod(c.z, 1.0);
	
	x0 = int(c.x);
	x1 = int(c.x) + 1;
	y0 = int(c.y);
	y1 = int(c.y) + 1;
	z0 = int(c.z);
	z1 = int(c.z) + 1;
	
	int shift = 0;
	int cor = 0x000000FF;
	
	// Использовать ли другие данные ориентированые на ось X?
	if(abs(rayDirection.x) > abs(rayDirection.z)){
		sizeX = dataAmount; sizeY = dataHeight; sizeZ = dataWidth;
		Swap(x0, z0);
		Swap(x1, z1);
		Swap(xd, zd);
		shift = 8;
		cor = 0x0000FF00;
	} else {
		sizeX = dataWidth; sizeY = dataHeight; sizeZ = dataAmount;
	}
	
	int area = sizeX * sizeY;
	
	int c000, c001, c010, c011, c100, c101, c110, c111;
	
	if ((x0 + y0 * sizeX + z0 * area) % 2 == 0){
		int c00x = color[(x0 + y0 * sizeX + z0 * area) / 2];
		c000 = (c00x  & cor) >> shift;
		c100 = ((c00x >> 16)  & cor) >> shift;
	}else{
		c000 = (color[(x0 + y0 * sizeX + z0 * area) / 2] >> 16 & cor) >> shift;
		c100 = (color[(x1 + y0 * sizeX + z0 * area) / 2] & cor) >> shift;
	}
	if ((x0 + y1 * sizeX + z0 * area) % 2 == 0){
		int c10x = color[(x0 + y1 * sizeX + z0 * area) / 2];
		c010 = (c10x  & cor) >> shift;
		c110 = ((c10x >> 16)  & cor) >> shift;
	}else{
		c010 = (color[(x0 + y1 * sizeX + z0 * area) / 2] >> 16 & cor) >> shift;
		c110 = (color[(x1 + y1 * sizeX + z0 * area) / 2] & cor) >> shift;
	}
	if ((x0 + y0 * sizeX + z1 * area) % 2 == 0){
		int c01x = color[(x0 + y0 * sizeX + z1 * area) / 2];
		c001 = (c01x  & cor) >> shift;
		c101 = ((c01x >> 16)  & cor) >> shift;
	}else{
		c001 = (color[(x0 + y0 * sizeX + z1 * area) / 2] >> 16 & cor) >> shift;
		c101 = (color[(x1 + y0 * sizeX + z1 * area) / 2] & cor) >> shift;
	}
	if ((x0 + y1 * sizeX + z1 * area) % 2 == 0){
		int c11x = color[(x0 + y1 * sizeX + z1 * area) / 2];
		c011 = (c11x  & cor) >> shift;
		c111 = ((c11x >> 16)  & cor) >> shift;
	}else{
		c011 = (color[(x0 + y1 * sizeX + z1 * area) / 2] >> 16 & cor) >> shift;
		c111 = (color[(x1 + y1 * sizeX + z1 * area) / 2] & cor) >> shift;
	}
	
	float dx = (c100 + c110 + c101 + c111) - (c000 + c010 + c001 + c011);
	float dy = (c010 + c110 + c011 + c111) - (c000 + c100 + c001 + c101);
	float dz = (c001 + c011 + c101 + c111) - (c000 + c010 + c100 + c110);
	
	return result = normalize(vec3(dx, dy, dz));
}

vec3 BlinnPhong(vec3 normal) {
	vec3 result;
	vec3 lightSource = normalize(vec3(-1.0,-1.0,-1.0));
	float temp = abs(-dot(normal, lightSource));
	result = vec3(temp,temp,temp);
	return result;
}

vec3[2] Intersect(vec3 rayOrigin, vec3 rayDirection){
	vec3[] result = vec3[2](vec3(0.0, 0.0, 0.0), vec3(0.0, 0.0, 0.0));
	
	float t0x, t1x, t0y, t1y, t0z, t1z;
	
	vec3 invdir = 1 / rayDirection;
	
	if(invdir.x >= 0){
		t0x = (aabb1.x - rayOrigin.x) * invdir.x;
		t1x = (aabb2.x - rayOrigin.x) * invdir.x;
	} else {
		t0x = (aabb2.x - rayOrigin.x) * invdir.x;
		t1x = (aabb1.x - rayOrigin.x) * invdir.x;
	}
	
	if(invdir.y >= 0){
		t0y = (aabb1.y - rayOrigin.y) / rayDirection.y;
		t1y = (aabb2.y - rayOrigin.y) / rayDirection.y;
	} else {
		t0y = (aabb2.y - rayOrigin.y) / rayDirection.y;
		t1y = (aabb1.y - rayOrigin.y) / rayDirection.y;
	}
	
	if (t0x > t1y || t0y > t1x)
		discard;
	
	if (t0y > t0x)
		t0x = t0y;
		
	if (t1y < t1x)
		t1x = t1y;
	
	
	if(invdir.z >= 0){
		t0z = (aabb1.z - rayOrigin.z) / rayDirection.z;
		t1z = (aabb2.z - rayOrigin.z) / rayDirection.z;
	} else {
		t0z = (aabb2.z - rayOrigin.z) / rayDirection.z;
		t1z = (aabb1.z - rayOrigin.z) / rayDirection.z;
	}
	
	if (t0x > t1z || t0z > t1x)
		discard;
		
	if (t0z > t0x)
		t0x = t0z;
		
	if (t1z < t1x)
		t1x = t1z;
	
	result[0] = vec3(rayDirection.x * t0x + rayOrigin.x, rayDirection.y * t0x + rayOrigin.y, rayDirection.z * t0x + rayOrigin.z);
	result[1] = vec3(rayDirection.x * t1x + rayOrigin.x, rayDirection.y * t1x + rayOrigin.y, rayDirection.z * t1x + rayOrigin.z);
	
	return result;
}

vec3 Object(vec3 v){
	//To object(world) space
	vec4 result;
	result = inverse(projection * view) * vec4(v, 1.0);
	return result.xyz;
}

vec3 Image(vec3 v){
	//To image space
	vec4 result;
	result = projection * view * vec4(v, 1.0);
	return result.xyz;
}

vec3 Data(vec3 v) {
	//To data space
	vec3 result;

	vec3 resolution = vec3(dataWidth, dataHeight, dataAmount);
	vec3 offset = vec3(abs(aabb1.x), abs(aabb1.y), abs(aabb1.z));
	result = v + offset;
	result.z = result.z / correction; // aabb2.z / 2;	//correction
	result = result * resolution;
	return result;
}

float Sample(vec3 c, vec3 rayDirection){
	float result;
	
	int sizeX, sizeY, sizeZ;
	
	int x0, x1, y0, y1, z0, z1;
	
	float xd = mod(c.x, 1.0);
	float yd = mod(c.y, 1.0);
	float zd = mod(c.z, 1.0);
	
	x0 = int(c.x);
	x1 = int(c.x) + 1;
	y0 = int(c.y);
	y1 = int(c.y) + 1;
	z0 = int(c.z);
	z1 = int(c.z) + 1;
	
	int shift = 0;
	int cor = 0x000000FF;
	
	// Использовать ли другие данные ориентированые на ось X?
	if(abs(rayDirection.x) > abs(rayDirection.z)){
		sizeX = dataAmount; sizeY = dataHeight; sizeZ = dataWidth;
		Swap(x0, z0);
		Swap(x1, z1);
		Swap(xd, zd);
		shift = 8;
		cor = 0x0000FF00;
	} else {
		sizeX = dataWidth; sizeY = dataHeight; sizeZ = dataAmount;
	}
	
	int area = sizeX * sizeY;
	
	if(useInterpolation){
		int c000, c001, c010, c011, c100, c101, c110, c111;
		
		if ((x0 + y0 * sizeX + z0 * area) % 2 == 0){
			int c00x = color[(x0 + y0 * sizeX + z0 * area) / 2];
			c000 = (c00x  & cor) >> shift;
			c100 = ((c00x >> 16)  & cor) >> shift;
		}else{
			c000 = (color[(x0 + y0 * sizeX + z0 * area) / 2] >> 16 & cor) >> shift;
			c100 = (color[(x1 + y0 * sizeX + z0 * area) / 2] & cor) >> shift;
		}
		if ((x0 + y1 * sizeX + z0 * area) % 2 == 0){
			int c10x = color[(x0 + y1 * sizeX + z0 * area) / 2];
			c010 = (c10x  & cor) >> shift;
			c110 = ((c10x >> 16)  & cor) >> shift;
		}else{
			c010 = (color[(x0 + y1 * sizeX + z0 * area) / 2] >> 16 & cor) >> shift;
			c110 = (color[(x1 + y1 * sizeX + z0 * area) / 2] & cor) >> shift;
		}
		if ((x0 + y0 * sizeX + z1 * area) % 2 == 0){
			int c01x = color[(x0 + y0 * sizeX + z1 * area) / 2];
			c001 = (c01x  & cor) >> shift;
			c101 = ((c01x >> 16)  & cor) >> shift;
		}else{
			c001 = (color[(x0 + y0 * sizeX + z1 * area) / 2] >> 16 & cor) >> shift;
			c101 = (color[(x1 + y0 * sizeX + z1 * area) / 2] & cor) >> shift;
		}
		if ((x0 + y1 * sizeX + z1 * area) % 2 == 0){
			int c11x = color[(x0 + y1 * sizeX + z1 * area) / 2];
			c011 = (c11x  & cor) >> shift;
			c111 = ((c11x >> 16)  & cor) >> shift;
		}else{
			c011 = (color[(x0 + y1 * sizeX + z1 * area) / 2] >> 16 & cor) >> shift;
			c111 = (color[(x1 + y1 * sizeX + z1 * area) / 2] & cor) >> shift;
		}
		
		float c00 = c000 * (1 - xd) + c100 * xd;
		float c01 = c001 * (1 - xd) + c101 * xd;
		float c10 = c010 * (1 - xd) + c110 * xd;
		float c11 = c011 * (1 - xd) + c111 * xd;
		
		float c0 = c00 * (1 - yd) + c10 * yd;
		float c1 = c01 * (1 - yd) + c11 * yd;
		
		result = (c0 * (1 - zd) + c1 * zd);
	}
	if(useInterpolation){
		return result;
	} else {
		return (color[(x0 + y0 * sizeX + z0 * area) / 2] & cor) >> shift;
	}
	
}

float lerp(float b, float a, float x){
	return a * x + b * (1 - x);
}

vec3 lerp(vec3 b, vec3 a, float x){
	return vec3(a.x * x + b.x * (1 - x), a.y * x + b.y * (1 - x), a.z * x + b.z * (1 - x));
}

float alphaRamp(float x){
	float s1 = 0.0;
	float s2 = 0.223;
	float s3 = 1.0;
	
	float color1 = 0.05;
	float color2 = 0.386;
	float color3 = 1.0;
	
	if(x < s2){
		x = x / s2;
		return lerp(color1, color2, x);
	} else{
		x = x - s2;
		float t = s3 - s2;
		x = x / t;
		return lerp(color2, color3, x);
	}
}

vec3 colorRamp(float x){
	float s1 = 0.0;
	float s2 = 0.4;
	float s3 = 1.0;
	
	vec3 color1 = vec3(1.0, 0.786, 0.386);
	vec3 color2 = vec3(0.922, 0.0, 0.004);
	vec3 color3 = vec3(0.844, 0.782, 0.745);
	
	if(x < s2){
		x = x / s2;
		return lerp(color1, color2, x);
	} else{
		x = x - s2;
		float t = s3 - s2;
		x = x / t;
		return lerp(color2, color3, x);
	}
}

void main() {
	float tx = (float(gl_FragCoord.x) / float(screenWidth)) * 2.0 - 1.0;
	float ty = (float(gl_FragCoord.y) / float(screenHeight)) * 2.0 - 1.0;
	vec3 rayOrigin = Object(vec3(tx, ty, 0.0));		///In object-space
	vec3 rayDirection = normalize(Object(vec3(tx, ty, -1.0)) - rayOrigin);
	
	vec3 modulation = rayDirection / max(dataWidth, max(dataHeight, dataAmount));// sampleResolution;
	//int correction = int(1.0 / screenWidth / modulation);	//voxel size divided by modulation = correction value
	//if(correction == 0) correction = 1;
	
	vec3 C = vec3(1.0, 0.786, 0.386);
	
	vec3 target = vec3(1, 0, 0);
	float Alpha = 0;
	
	vec3[] inters = Intersect(rayOrigin, rayDirection);
	
	vec3 p = inters[0];
	
	float U1 = Image(p).z;
	float U2 = Image(inters[1]).z;
	
	// Opaque version:
	// Loop through all samples falling within data
	while (U1 > U2) {
		// If sample opacity > 0,
		// then resample color and composite into ray
		float a = Sample(Data(p), rayDirection);// Проба
		a = a / 256.0; // / correction;
		
		if(a >= treshold){
			vec3 lit = BlinnPhong(Grad(Data(p),rayDirection));
			vec3 sampleColor = colorRamp(a);
			diffuseColor = vec4(sampleColor * lit, 1.0);
			break;
		}
		p += modulation; // Прирощение координаты пробы
		U1 = Image(p).z;
	}
	/*
	// Translucent version:
	// Loop through all samples falling within data
	C = colorRamp(Sample(Data(p), rayDirection) / 256.0);// - первая проба
	while (U1 > U2) {
		// If sample opacity > 0,
		// then resample color and composite into ray
		float a = Sample(Data(p), rayDirection);// Проба
		a = a / 256.0; // / correction;
		
		vec3 sampleColor = colorRamp(a);
		//a = alphaRamp(a);
		
		if(a >= treshold){
			vec3 lit = BlinnPhong(Grad(Data(p),rayDirection));
			C = lerp(C, sampleColor * lit, a);
			//C = lerp(C, target, a);
			//a = 1.0;
			
		}else{
			a =0.0;
		}
		Alpha += a;// * brightness;
		if(Alpha >= 1){
			break;
		}
		
		p += modulation; // Прирощение координаты пробы
		U1 = Image(p).z;
		
		//if (Alpha > 0){
		//	C = Sample(dataSet, p);
		//	c = c + C(1 - alpha);
		//	alpha = alpha + Alpha(U)(1 - alpha);
		//}
	}
	if (Alpha == 0) discard;
	diffuseColor = vec4(C, Alpha);
	*/
}
