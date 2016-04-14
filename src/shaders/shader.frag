#version 150 core

in Material {
	//vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	vec2 texCoord;
	float specularity;
} mat_in;

in Light {
	vec3 light_cameraspace;
	vec3 color;
	float power;
} light_in;

in vec3 N;
in vec3 v;
in vec3 position_worldspace;
//in vec3 normal_cameraspace;
//in vec3 light_cameraspace;
in vec3 eyeDirection_cameraspace;

out vec4 fragColor;

uniform vec3 lightPos;

uniform int enableTex;
uniform int enableSpec;
uniform int enableBump;
uniform int enableEmission;
uniform sampler2D tex;
uniform sampler2D spec;
uniform sampler2D bump;
uniform sampler2D emission;

void main() {
	vec3 lightColor = vec3(1, 1, 1);
	
	vec3 color;
	float alpha = 1;
	if (clamp(enableTex, 0, 1) == 0)
		color = mat_in.diffuse;
	else {
		color = texture(tex, mat_in.texCoord).xyz;
		alpha = texture(tex, mat_in.texCoord).w;
	}
	
	vec3 Iamb = vec3(0.1, 0.1, 0.1) * color.xyz;
	//vec3 Iamb = mat_in.ambient;
	
	float distance = length(lightPos - position_worldspace);
	
	vec3 V;
	if (clamp(enableBump, 0, 1) == 0)
		V = v;
	else
		V = v + texture(bump, mat_in.texCoord).x * N;
	
	vec3 L = normalize(light_in.light_cameraspace - V);
	float cosTheta = clamp(dot(N, L), 0, 1);
	
	float roughness;
	if (clamp(enableSpec, 0, 1) == 0)
		roughness = mat_in.specularity;
	else {
		roughness = texture(spec, mat_in.texCoord).x;
		roughness = clamp(roughness, 0, 1);
	}
	
	// Lambert's cosine law
	vec3 Idiff = color * lightColor * cosTheta;
	Idiff = clamp(Idiff, 0, 1);
	
	vec3 E = normalize(-v);
	vec3 R = reflect(-L, N);
	
	
	roughness = clamp(roughness, 0, 1);
	float cosAlpha = clamp(dot(E, R), 0, 1);
	
	// custom specularity model
	float specfunc = pow((cosAlpha * roughness + 1) / 2, 1);
	vec3 Ispec = mat_in.specular * light_in.color * light_in.power * pow(cosAlpha, 5) / (distance * distance);
	Ispec = clamp(Ispec, 0, 1);
	
	// emission
	vec3 Iemission = vec3(0, 0, 0);
	if (clamp(enableEmission, 0, 1) == 1)
		Iemission = texture(emission, mat_in.texCoord).xyz;
	
	vec3 outColor = Iamb + Idiff * (1 - roughness) + Ispec * roughness + Iemission;
	//fragColor = vec4(outColor, 1.0);
	fragColor = vec4(outColor.xyz, alpha);
}