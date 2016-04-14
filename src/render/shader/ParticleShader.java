package render.shader;

import game.Particle;


public abstract class ParticleShader extends ObjectShader {

	public ParticleShader(String vertexPath, String fragmentPath) {
		super(vertexPath, fragmentPath);
	}
	
	public abstract void setMaxParticles(int maxParticles);
	
	public abstract void setParticles(Particle[] particles);

}
