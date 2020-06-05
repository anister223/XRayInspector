#version 330 core

//in vec3 passColor;
in vec2 passTextureCoord;

//out vec4 outColor;

uniform sampler2D tex;

uniform int screenWidth;
uniform int screenHeight;

void main() {
	vec4 texColor = texture2D(tex, passTextureCoord);

	if(texColor.a < 0.01f)
	{
		discard;
	} else {
		//float r = gl_FragCoord.x / screenWidth;
		//if (r < 1.0) {
		//gl_FragColor = vec4(r, gl_FragCoord.y / screenHeight, 0.0, 1.0);
		//} else
		//gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
		gl_FragColor = vec4(0.0, 1.0, 0.0, texColor.x);
	}
}
