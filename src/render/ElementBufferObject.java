package render;

import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.IntBuffer;

public class ElementBufferObject {

	private final int id;
	
	public ElementBufferObject(){
		id = glGenBuffers();
	}
	
	public void bind(){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
	}
	
	public void unbind(){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void delete(){
		glDeleteBuffers(id);
	}
	
	public void bufferData(IntBuffer data, int usage){
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage);
	}
	
	public int getID(){
		return id;
	}
	
}
