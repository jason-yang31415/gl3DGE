package render;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

public class VertexBufferObject {

	private final int id;
	
	public VertexBufferObject(){
		id = glGenBuffers();
	}
	
	public void bind(int target){
		glBindBuffer(target, id);
	}
	
	public void unbind(int target){
		glBindBuffer(target, 0);
	}
	
	public void bufferData(FloatBuffer data, int usage){
		glBufferData(GL_ARRAY_BUFFER, data, usage);
	}
	
	public void delete(){
		glDeleteBuffers(id);
	}
	
	public int getID(){
		return id;
	}
	
}
