package render;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import io.FileLoader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import render.mesh.Mesh;
import render.mesh.Resource;
import render.mesh.Vertex;

public class DefaultParticleShader extends ParticleShader {

	int[] indices;
	
	int ebo;
	
	public DefaultParticleShader() {
		super("shader.vert", "shader.frag"); // TEMP
		// TODO Auto-generated constructor stub
	}

	@Override
	public void loadObjectData(Mesh mesh) {
		ArrayList<Float> vertex_data = new ArrayList<Float>();
		for (Vertex v : mesh.getVertices()){
			vertex_data.add(v.getPosition().x);
			vertex_data.add(v.getPosition().y);
			vertex_data.add(v.getPosition().z);
			vertex_data.add(v.getNormal().x);
			vertex_data.add(v.getNormal().y);
			vertex_data.add(v.getNormal().z);
			vertex_data.add(v.getDiffuseColor().x);
			vertex_data.add(v.getDiffuseColor().y);
			vertex_data.add(v.getDiffuseColor().z);
			vertex_data.add(v.getSpecularColor().x);
			vertex_data.add(v.getSpecularColor().y);
			vertex_data.add(v.getSpecularColor().z);
			vertex_data.add(v.getTextureCoordinate().x);
			vertex_data.add(v.getTextureCoordinate().y);
		}

		float[] vertArray = new float[vertex_data.size()];
		for (int n = 0; n < vertex_data.size(); n++) {
			vertArray[n] = vertex_data.get(n);
		}

		FloatBuffer vertices = BufferUtils.createFloatBuffer(vertArray.length);
		vertices.put(vertArray).flip();

		int[] indexArray = new int[mesh.getIndices().size()];
		for (int i = 0; i < mesh.getIndices().size(); i++) {
			indexArray[i] = mesh.getIndices().get(i);
		}

		loadVertices(vertices);
		loadIndices(indexArray);
	}

	public void loadIndices(int[] indices) {
		this.indices = indices;
	}
	
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
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Scene scene, Drawable d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}
	
}
