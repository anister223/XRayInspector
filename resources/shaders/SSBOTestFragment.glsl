#version 430

layout(std430, binding = 0) buffer ColorSSBO {
	float color[];
};

out vec4 FragmentColor;

void main() {
	//FragmentColor = vec4(color[0], color[1], color[2], 1.0);
	gl_FragColor = vec4(color[0], color[1], color[2], 1.0);
}