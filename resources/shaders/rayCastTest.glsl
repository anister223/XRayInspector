#version 430 core

layout(std430, binding = 0) buffer ColorSSBO {
	float color[];
};

out vec4 outColor;
/*
vec3 unpackColor(float f) 
{
    vec3 color;
    color.r = floor(f / 65536);
    color.g = floor((f - color.r * 65536) / 256.0);
    color.b = floor(f - color.r * 65536 - color.g * 256.0);
    return color / 256.0;
}
*/
void main() {
	int index = int(gl_FragCoord.x) + int(gl_FragCoord.y) * 1280;
	float c = color[index]; //unpackColor(bf[index]);
	outColor = vec4(c, c, c, 1.0);
}
