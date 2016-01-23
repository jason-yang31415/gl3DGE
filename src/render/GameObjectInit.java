package render;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;

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
	
	public abstract void load(String path) throws FileNotFoundException, IOException;
	
	public void loadVertices(FloatBuffer vertices){
		this.vertices = vertices;
	}
	
	public void loadVertexShader(Shader vertexShader){
		this.vertexShader = vertexShader;
	}
	
	public void loadFragmentShader(Shader fragmentShader){
		this.fragmentShader = fragmentShader;
	}
	
	public abstract void init();
	
	public abstract void update(Scene scene, Drawable d);
	
	public abstract void draw();
	
}
