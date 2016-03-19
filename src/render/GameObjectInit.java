package render;

import java.io.IOException;
import java.nio.FloatBuffer;

import render.mesh.Mesh;
import util.Matrix4f;

public abstract class GameObjectInit {

	int count;
	
	FloatBuffer vertices;
	
	VertexArrayObject vao;
	VertexBufferObject vbo;
	
	Shader vertexShader;
	Shader fragmentShader;
	ShaderProgram shader;
	
	String vertexPath;
	String fragmentPath;
	
	public GameObjectInit(String vertexPath, String fragmentPath){
		this.vertexPath = vertexPath;
		this.fragmentPath = fragmentPath;
	}
	
	public abstract void load(String param, String value);
	
	public abstract void loadObjectData(Mesh mesh);
	
	public abstract void loadShaders() throws IOException;
	
	public boolean check(){
		if (vertices == null || vertexShader == null || fragmentShader == null)
			throw new RuntimeException("Failed to load Game Object: "
					+ "\nVertices: " + vertices
					+ "\nVertex shader: " + vertexShader
					+ "\nFragment shader" + fragmentShader );
		return true;
	}
	
	public void loadVertices(FloatBuffer vertices){
		this.vertices = vertices;
	}
	
	public void loadVertexShader(Shader vertexShader){
		this.vertexShader = vertexShader;
	}
	
	public void loadFragmentShader(Shader fragmentShader){
		this.fragmentShader = fragmentShader;
	}
	
	public abstract void setMVP(Matrix4f model, Matrix4f view, Matrix4f projection);
	
	public abstract void init();
	
	public abstract void update(Scene scene, Drawable d);
	
	public abstract void draw();
	
}
