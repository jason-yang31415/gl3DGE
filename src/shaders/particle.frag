#version 150 core

in vec4 particle_color;

out vec4 fragColor;

void main() {
	fragColor = particle_color;
	//fragColor = vec4(0.1, 0.1, 0.1, 1);
}
