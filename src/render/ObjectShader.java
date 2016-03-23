package render;

import java.io.IOException;
import java.nio.FloatBuffer;

import render.mesh.Mesh;
import util.Matrix4f;

public abstract class ObjectShader {

	FloatBuffer vertices;
	
	VertexArrayObject vao;
	VertexBufferObject vbo;
	
	Shader vertexShader;
	Shader fragmentShader;
	ShaderProgram shader;
	
	String vertexPath;
	String fragmentPath;
	
	public ObjectShader(String vertexPath, String fragmentPath){
		this.vertexPath = vertexPath;
		this.fragmentPath = fragmentPath;
	}

	public abstract void loadObjectData(Mesh mesh);
	
	public abstract void loadShaders() throws IOException;
	
	public void loadVertices(FloatBuffer vertices){
		this.vertices = vertices;
	}
	
	public void loadVertexShader(Shader vertexShader){
		this.vertexShader = vertexShader;
	}
	
	public void loadFragmentShader(Shader fragmentShader){
		this.fragmentShader = fragmentShader;
	}
	
	public boolean check(){
		if (vertices == null || vertexShader == null || fragmentShader == null)
			throw new RuntimeException("Failed to load Game Object: "
					+ "\nVertices: " + vertices
					+ "\nVertex shader: " + vertexShader
					+ "\nFragment shader" + fragmentShader );
		return true;
	}
	
	public abstract void init();
	
	public abstract void update(Scene scene, Drawable d);
	
	public abstract void draw();

	public void setMVP(Matrix4f model, Matrix4f view, Matrix4f projection) {
		shader.bind();
		shader.setUniformMat4f("model", model);
		shader.setUniformMat4f("view", view);
		shader.setUniformMat4f("projection", projection);
		shader.unbind();
	}
	
}
