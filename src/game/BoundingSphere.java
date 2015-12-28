package game;

import logic.Transform;
import util.Vector3f;

public class BoundingSphere {

	Transform transform;
	float size;
	
	public BoundingSphere(Transform transform, float size){
		this.transform = transform;
		this.size = size;
	}
	
	public boolean collision(GameObject go){
		float collide = go.getBound().getSize() + size;
		return collision(go.getPos(), collide);
	}
	
	public boolean collision(Transform t){
		return collision(t.getPos(), size);
	}
	
	public boolean collision(Vector3f pos, float collide){
		Vector3f center = transform.getPos();
		float d = (float) Math.sqrt(Math.pow(center.x - pos.x, 2) + Math.pow(center.y - pos.y, 2) + Math.pow(center.z - pos.z, 2));
		if (d <= size)
			return true;
		else
			return false;
	}
	
	public float getSize(){
		return size;
	}
	
	public void setSize(float size){
		this.size = size;
	}
	
}
