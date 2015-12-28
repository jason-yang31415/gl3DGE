#version 150 core

in vec3 position;
in vec3 normal;
in vec3 color;
in vec3 specularColor;
in vec2 texcoord;

out Material {
	vec3 diffuse;
	vec3 specular;
	vec2 texCoord;
	float specularity;
} mat_out;

out vec3 N;
out vec3 v;

out vec3 position_worldspace;
out vec3 light_cameraspace;
out vec3 eyeDirection_cameraspace;

uniform vec3 light;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float specularity;

void main() {
	mat_out.diffuse = color;
	mat_out.specular = specularColor;
	mat_out.texCoord = texcoord;
	mat_out.specularity = specularity;
	
	position_worldspace = (model * vec4(position, 1)).xyz;
	v = vec3(view * model * vec4(position, 1)).xyz;
	N = normalize((view * model * vec4(normal, 0)).xyz);
	
	vec3 position_cameraspace = (view * model * vec4(position, 1)).xyz;
	eyeDirection_cameraspace = vec3(0, 0, 0) - position_cameraspace;
	
	vec4 lightPositionVec4 = vec4(light, 1);
	
	vec3 lightPosition_cameraspace = (view * vec4(light, 1)).xyz;
	light_cameraspace = lightPosition_cameraspace + eyeDirection_cameraspace;
	
	mat4 mvp = projection * view * model;
	gl_Position = mvp * vec4(position, 1.0);
}