package game;

import render.shader.ObjectShader;
import render.shader.ParticleShader;
import render.shader.ShaderProgram;
import util.Vector3f;
import util.Vector4f;

public class Particle {

	ObjectShader ps;
	
	Vector3f position;
	Vector3f velocity;
	Vector4f color;
	float size, angle, weight;
	float t, lifespan;
	
	public Particle(ObjectShader ps, Vector3f position, Vector3f velocity, Vector4f color, float size, float lifespan){
		this.ps = ps;
		
		this.position = position;
		this.velocity = velocity;
		this.color = color;
		this.size = size;
		this.lifespan = lifespan;
		t = 0;
	}
	
	public void update(){
		t++;
		position = position.add(velocity);
		color = color.scale(1 - t / lifespan);
	}
	
	public boolean isDead(){
		if (t > lifespan)
			return true;
		return false;
	}
	
	public void uniform(ShaderProgram shader){
		shader.setUniformVec4f("particle_position_size", new Vector4f(position.x, position.y, position.z, size));
		shader.setUniformVec4f("color", color);
	}
	
	public Vector3f getPosition(){
		return position;
	}
	
	public Vector3f getVelocity(){
		return velocity;
	}
	
	public Vector4f getColor(){
		return color;
	}
	
	public float getSize(){
		return size;
	}
	
}
