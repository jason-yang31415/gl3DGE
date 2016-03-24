#version 150 core

in vec4 particle_color;

out vec4 color;

void main() {
	color = particle_color;
	//color = vec4(0.1, 0.1, 0.1, 1);
}
