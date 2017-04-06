package render.shader;

import java.io.IOException;
import java.nio.FloatBuffer;

import render.Drawable;
import render.Scene;
import render.VertexArrayObject;
import render.VertexBufferObject;
import render.mesh.Mesh;
import util.Matrix4f;

public abstract class ObjectShader {
	
	protected Shader vertexShader;
	protected Shader fragmentShader;
	protected ShaderProgram shader;
	
	protected String vertexPath;
	protected String fragmentPath;
	
	public ObjectShader(String vertexPath, String fragmentPath){
		this.vertexPath = vertexPath;
		this.fragmentPath = fragmentPath;
	}
	
	public ObjectShader(Shader vertex, Shader fragment){
		loadVertexShader(vertex);
		loadFragmentShader(fragment);
	}
	
	public ObjectShader(){
		
	}

	public abstract FloatBuffer getVertices(Mesh mesh);
	
	public abstract void loadShaders() throws IOException;
	
	public abstract void setVBOPointers(VertexBufferObject vbo);
	
	public void loadVertexShader(Shader vertexShader){
		this.vertexShader = vertexShader;
	}
	
	public void loadFragmentShader(Shader fragmentShader){
		this.fragmentShader = fragmentShader;
	}
	
	public boolean check(){
		if (vertexShader == null || fragmentShader == null)
			throw new RuntimeException("Failed to load Game Object: "
					+ "\nVertex shader: " + vertexShader
					+ "\nFragment shader: " + fragmentShader );
		return true;
	}
	
	public abstract void init();
	
	public abstract void update(Scene scene, Drawable d);
	
	public abstract void bind();
	
	public abstract void unbind();

	public void setMVP(Matrix4f model, Matrix4f view, Matrix4f projection) {
		shader.bind();
		shader.setUniformMat4f("model", model);
		shader.setUniformMat4f("view", view);
		shader.setUniformMat4f("projection", projection);
		shader.unbind();
	}
	
	public ShaderProgram getShader(){
		return shader;
	}
	
}
