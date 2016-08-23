package audio;

import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alListener3f;
import static org.lwjgl.openal.AL10.alListenerfv;
import logic.Transform;
import util.Matrix4f;
import util.Vector3f;

public class Listener {
	
	private Transform transform;
	
	public Listener(Transform transform){
		setTransform(transform);
		
		updateListener();
	}
	
	public void setTransform(Transform transform){
		this.transform = transform;
	}
	
	public Transform getTransform(){
		return transform;
	}
	
	public void updateListener(){
		Vector3f position = transform.getPos();
		Vector3f velocity = transform.getVelocity();
		Matrix4f matrix = transform.getMatrix();
		alListener3f(AL_POSITION, -position.x, position.y, position.z);
		alListener3f(AL_VELOCITY, -velocity.x, velocity.y, velocity.z);
		alListenerfv(AL_ORIENTATION, new float[]{-matrix.m02, matrix.m12, matrix.m22, -matrix.m01, matrix.m11, matrix.m21});
	}
	
}
