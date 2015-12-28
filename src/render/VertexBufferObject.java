package render;

import static org.lwjgl.opengl.GL15.*;

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
	
	public void bufferData(int target, FloatBuffer data, int usage){
		glBufferData(target, data, usage);
	}
	
	public void delete(){
		glDeleteBuffers(id);
	}
	
	public int getID(){
		return id;
	}
	
}
