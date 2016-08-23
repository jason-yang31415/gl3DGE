package audio;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_PITCH;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;
import logic.Transform;
import util.Vector3f;

public class Source {

	private final int id;
	private Transform transform;
	
	private AudioBuffer buffer;
	
	public Source(AudioBuffer buffer, Transform transform){
		id = alGenSources();
		this.transform = transform;
		
		setBuffer(buffer);
		setPitch(1.0f);
		setGain(1.0f);
		updateSource();
	}
	
	public int getID(){
		return id;
	}
	
	public void delete(){
		alDeleteSources(id);
	}
	
	public void setBuffer(AudioBuffer buffer){
		this.buffer = buffer;
		alSourcei(id, AL_BUFFER, buffer.getID());
	}
	
	public AudioBuffer getBuffer(){
		return buffer;
	}
	
	public void setPitch(float pitch){
		alSourcef(id, AL_PITCH, pitch);
	}
	
	public void setGain(float gain){
		alSourcef(id, AL_GAIN, gain);
	}
	
	public void updateSource(){
		Vector3f position = transform.getPos();
		Vector3f velocity = transform.getVelocity();
		alSourcei(id, AL_BUFFER, buffer.getID());
		alSource3f(id, AL_POSITION, position.x, position.y, position.z);
		alSource3f(id, AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}
	
}
