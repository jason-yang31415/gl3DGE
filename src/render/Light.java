package render;

import util.Vector3f;

public class Light {

	Vector3f pos, color;
	float power;
	
	public Light(){
		this(0, 0, 0);
	}
	
	public Light(float x, float y, float z){
		this(x, y, z, 1, 1, 1, 10);
	}
	
	public Light(float x, float y, float z, float r, float g, float b, float power){
		this(new Vector3f(x, y, z), new Vector3f(r, g, b), power);
	}
	
	public Light(Vector3f pos, Vector3f color, float power){
		this.pos = pos;
		this.color = color;
		this.power = power;
	}
	
	public void setPos(Vector3f pos){
		this.pos = pos;
	}
	
	public Vector3f getPos(){
		return pos;
	}
	
	public void setColor(Vector3f color){
		this.color = color;
	}
	
	public Vector3f getColor(){
		return color;
	}
	
	public void setPower(float power){
		this.power = power;
	}
	
	public float getPower(){
		return power;
	}
	
}
