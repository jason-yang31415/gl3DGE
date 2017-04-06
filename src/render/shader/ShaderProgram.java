package render.shader;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

import java.util.HashMap;
import java.util.Map;

import render.UniformBufferObject;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class ShaderProgram {

	private final int id;
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	public ShaderProgram(){
		id = glCreateProgram();
	}
	
	public void attachShader(Shader s){
		glAttachShader(id, s.getID());
	}
	
	public void bindFragDataLocation(int colorNumber, CharSequence name){
		glBindFragDataLocation(id, colorNumber, name);
	}
	
	public void link(){
		glLinkProgram(id);
		
		check();
	}
	
	public void check(){
		int status = glGetProgrami(id, GL_LINK_STATUS);
		if (status != GL_TRUE)
			throw new RuntimeException(glGetProgramInfoLog(id));
	}
	
	public void bind(){
		glUseProgram(id);
	}
	
	public void unbind(){
		glUseProgram(0);
	}
	
	public int getAttribLocation(CharSequence name){
		return glGetAttribLocation(id, name);
	}
	
	public void enableVertexAttribArray(int index){
		glEnableVertexAttribArray(index);
	}
	
	public void vertexAttribPointer(int location, int size, int stride, int offset){
		glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
	}
	
	public int getUniform(String name){
		if (locationCache.containsKey(name))
			return locationCache.get(name);
		int result = glGetUniformLocation(id, name);
		if (result == -1)
			System.err.println("could not find uniform variable: " + name);
		else
			locationCache.put(name, result);
		
		return glGetUniformLocation(id, name);
	}
	
	public void setUniform1i(String name, int value){
		glUniform1i(getUniform(name), value);
	}

	public void setUniform1f(String name, float value){
		glUniform1f(getUniform(name), value);
	}
	
	public void setUniform2f(String name, float x, float y){
		glUniform2f(getUniform(name), x, y);
	}
	
	public void setUniform3f(String name, float x, float y, float z){
		glUniform3f(getUniform(name), x, y, z);
	}
	
	public void setUniformVec3f(String name, Vector3f vec){
		glUniform3f(getUniform(name), vec.x, vec.y, vec.z);
	}
	
	public void setUniformVec4f(String name, Vector4f vec){
		glUniform4f(getUniform(name), vec.x, vec.y, vec.z, vec.w);
	}
	
	public void setUniformMat4f(String name, Matrix4f matrix) {
        glUniformMatrix4fv(getUniform(name), false, matrix.getBuffer());
    }
	
	public int getUniformBlock(String name){
		return glGetUniformBlockIndex(id, name);
	}
	
	public void uniformBlockBinding(String name, UniformBufferObject ubo){
		glUniformBlockBinding(id, getUniformBlock(name), ubo.getBinding());
	}
	
	public void delete(){
		glDeleteProgram(id);
	}
	
	public int getID(){
		return id;
	}
	
}
