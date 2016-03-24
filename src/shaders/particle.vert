#version 150 core

in vec3 vertex_position_particle;

out vec4 particle_color;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec4 particle_position_size;
uniform vec4 color;

void main() {
	float size = particle_position_size.w;
	size = 1;
	vec3 particle_center = particle_position_size.xyz;
	
	vec3 vertex_position = particle_center + vertex_position_particle * size;
	
	mat4 mvp = projection * view * model;
	gl_Position = mvp * vec4(vertex_position, 1.0);
	//gl_Position = mvp * vec4(vertex_position_particle, 1.0);
	
	particle_color = color;
}
