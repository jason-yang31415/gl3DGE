package render.shader;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import game.Particle;
import io.FileLoader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import render.Drawable;
import render.Scene;
import render.VertexBufferObject;
import render.mesh.Mesh;
import render.mesh.Resource;
import render.mesh.Vertex;

public class DefaultParticleShader extends ParticleShader {

	public FloatBuffer positionData; // TEMP MAKE GETTERS
	public FloatBuffer colorData; // TEMP MAKE GETTERS
	
	//int[] indices;
	
	//VertexBufferObject position_vbo;
	//VertexBufferObject color_vbo;
	
	//int ebo;
	
	int maxParticles = 8; // TEMP
	Particle[] particles;
	
	public DefaultParticleShader() {
		super("particle.vert", "particle.frag");
	}

	@Override
	public void setMaxParticles(int maxParticles){
		this.maxParticles = maxParticles;
		particles = new Particle[maxParticles];
	}
	
	public void setParticles(Particle[] particles){
		this.particles = particles;
	}

	@Override
	public FloatBuffer getVertices(Mesh mesh) {
		ArrayList<Float> vertex_data = new ArrayList<Float>();
		for (Vertex v : mesh.getVertices()){
			vertex_data.add(v.getPosition().x);
			vertex_data.add(v.getPosition().y);
			vertex_data.add(v.getPosition().z);
		}

		float[] vertArray = new float[vertex_data.size()];
		for (int n = 0; n < vertex_data.size(); n++) {
			vertArray[n] = vertex_data.get(n);
		}

		FloatBuffer vertices = BufferUtils.createFloatBuffer(vertArray.length);
		vertices.put(vertArray).flip();

		/*int[] indexArray = new int[mesh.getIndices().size()];
		for (int i = 0; i < mesh.getIndices().size(); i++) {
			indexArray[i] = mesh.getIndices().get(i);
		}
		
		loadVertices(vertices);
		loadIndices(indexArray);*/
		return vertices;
	}

	/*public void loadIndices(int[] indices) {
		this.indices = indices;
	}*/
	
	public void loadShaders() throws IOException {
		String vertexSource = FileLoader.loadFile(Resource.DEFAULT_SHADER_DIR
				+ vertexPath);
		Shader vertexShader = new Shader(GL_VERTEX_SHADER, vertexSource);
		String fragmentSource = FileLoader.loadFile(Resource.DEFAULT_SHADER_DIR
				+ fragmentPath);
		Shader fragmentShader = new Shader(GL_FRAGMENT_SHADER, fragmentSource);

		loadVertexShader(vertexShader);
		loadFragmentShader(fragmentShader);
	}

	@Override
	public void init(){
		/*count = indices.length;
		
		positionData = FloatBuffer.allocate(maxParticles * 4);
		colorData = FloatBuffer.allocate(maxParticles * 4);

		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();

		/*
		 * for (int i : indices){ System.out.println(i); }
		 

		// create vao and vbo
		vao = new VertexArrayObject();
		vao.bind();

		vbo = new VertexBufferObject();
		vbo.bind(GL_ARRAY_BUFFER);
		vbo.bufferData(vertices, GL_STATIC_DRAW);
		
		int floatSize = 4; // TEMP; MOVE TO STATIC / CONST

		ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);*/

		// create shader program
		shader = new ShaderProgram();
		shader.attachShader(vertexShader);
		shader.attachShader(fragmentShader);
		shader.bindFragDataLocation(0, "fragColor");
		shader.link();
		shader.bind();

		shader.unbind();
	//	vbo.unbind(GL_ARRAY_BUFFER);
	//	vao.unbind();
	}

	@Override
	public void update(Scene scene, Drawable d) {
		shader.bind();
		shader.setUniformMat4f("model", d.getMatrix());
		shader.setUniformMat4f("view", scene.getCamera().getLookAt());
		shader.unbind();
	}

	@Override
	public void setVBOPointers(VertexBufferObject vbo) {
		shader.bind();
		int floatSize = 4;
		int vertex_position_particle = shader.getAttribLocation("vertex_position_particle");
		shader.enableVertexAttribArray(vertex_position_particle);
		vbo.bind(GL_ARRAY_BUFFER);
		shader.vertexAttribPointer(vertex_position_particle, 3, 3 * floatSize, 0);
		shader.unbind();
	}

	@Override
	public void bind() {
		shader.bind();
	}

	@Override
	public void unbind() {
		shader.unbind();
	}

	/*@Override
	public void draw() {
		vao.bind();
		vbo.bind(GL_ARRAY_BUFFER);
		shader.bind();
		
		//glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		
		for (Particle p : particles){
			if (p != null){
				p.uniform(shader);
				glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
			}
		}
		
		glDepthMask(true);
		//glEnable(GL_DEPTH_TEST);
		
		shader.unbind();
		vbo.unbind(GL_ARRAY_BUFFER);
		vao.unbind();
	}*/
	
}
