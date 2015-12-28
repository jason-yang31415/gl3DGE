package render;

import game.GameObject;
import logic.Transform;
import util.Vector3f;

public class Camera extends Transform {
	
	public static enum Mode {
		FIRST_PERSON,
		CHASE
	}
	Mode mode = Mode.FIRST_PERSON;
	
	GameObject target;
	
	public Camera(){
		super(Transform.Type.T2D);
	}
	
	public Camera(GameObject target){
		super(Transform.Type.T2D);
		this.target = target;
	}
	
	public void update(){
		switch (mode){
		case FIRST_PERSON:
			/*translate = target.translate;
			rotate = target.rotate;
			matrix = target.getMatrix();*/
			break;
		case CHASE:
			break;
		}
	}
	
	public void setMode(Mode mode){
		this.mode = mode;
	}
	
	public void setTarget(GameObject target){
		this.target = target;
	}
	
	/*@Override
	public Vector3f getPos(){
		float x = -matrix.m03;
		float y = -matrix.m13;
		float z = -matrix.m23;
		return new Vector3f(x, y, z);
	}*/
	
}
