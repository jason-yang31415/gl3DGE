package render;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import org.lwjgl.opengl.GL31;;

public class UniformBufferObject {

	private final int id;
	private int binding;
	
	public UniformBufferObject(){
		id = glGenBuffers();
		binding = 0;
	}
	
	public UniformBufferObject(int size){
		this();
		bind();
		bufferData(size);
		unbind();
	}
	
	public void bufferData(int size){
		glBufferData(GL_UNIFORM_BUFFER, size, GL_DYNAMIC_DRAW);
	}
	
	public void bindBufferBase(int binding){
		this.binding = binding;
		glBindBufferBase(GL31.GL_UNIFORM_BUFFER, binding, id);
	}
	
	public int getBinding(){
		return binding;
	}
	
	public void bind(){
		glBindBuffer(GL_UNIFORM_BUFFER, id);
	}
	
	public void unbind(){
		glBindBuffer(GL_UNIFORM_BUFFER, 0);
	}
	
	public void delete(){
		glDeleteBuffers(id);
	}
	
	public int getID(){
		return id;
	}
	
}
