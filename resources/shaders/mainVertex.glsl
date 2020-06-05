#version 460 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 color;
layout(location = 2) in vec2 textureCoord;

out vec3 passColor;
out vec2 passTextureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main(){
	//ortho
    gl_Position = projection * view * model * vec4(position, 1.0);
	//gl_Position = inverse(projection * view * model) * gl_Position;
	//gl_Position = projection * view * model * gl_Position;
	
	//persp
	//gl_Position = projection * view * model * vec4(position, 1.0);
	
	//gl_Position = gl_Position * inverse(model) * inverse(view) * inverse(projection);
	//gl_Position = projection * view * model * gl_Position;
	//gl_Position = gl_Position * model.transpose() * view.transpose() * projection.transpose();
	//gl_Position = projection * view * model * vec4(position, 1.0);
    passColor = color;
	passTextureCoord = textureCoord;
}
