package render;

import util.Vector3f;

public class Light {

	Vector3f pos;
	
	public Light(Vector3f pos){
		this.pos = pos;
	}
	
	public void setPos(Vector3f pos){
		this.pos = pos;
	}
	
	public Vector3f getPos(){
		return pos;
	}
	
}
