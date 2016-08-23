package audio;

import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;

import java.nio.ByteBuffer;

public class AudioBuffer {

	private final int id;
	
	public AudioBuffer(){
		id = alGenBuffers();
	}
	
	public void bufferData(int format, ByteBuffer data, int frequency){
		alBufferData(id, format, data, frequency);
	}
	
	public int getID(){
		return id;
	}
	
	public void delete(){
		alDeleteBuffers(id);
	}
	
}
