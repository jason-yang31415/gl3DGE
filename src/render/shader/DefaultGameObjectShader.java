package render.shader;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import io.FileLoader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import render.BumpMap;
import render.Drawable;
import render.EmissionMap;
import render.SamplerMap;
import render.Scene;
import render.SpecularityMap;
import render.TextureMap;
import render.VertexArrayObject;
import render.VertexBufferObject;
import render.mesh.Mesh;
import render.mesh.Resource;
import render.mesh.Vertex;

public class DefaultGameObjectShader extends GameObjectShader {

	int[] indices;

	int ebo;

	TextureMap texture;
	SpecularityMap spec;
	float specularity = 0;
	BumpMap bump;
	EmissionMap emission;

//	String obj;
//	boolean smooth;

	public DefaultGameObjectShader() {
		super("shader.vert", "shader.frag");
	}

	@Override
	public void loadMeshAttribute(String param, String value) {
		switch (param) {
		case "vertex":
			if (!value.equals(vertexPath))
				throw new RuntimeException(
						"Shader specified in this file is not compatible with this shader");
			break;
		case "fragment":
			if (!value.equals(fragmentPath))
				throw new RuntimeException(
						"Shader specified in this file is not compatible with this shader");
			break;
		case "texture":
			texture = TextureMap.load(value);
			break;
		case "specularity":
			try {
				specularity = Float.parseFloat(value);
			} catch (NumberFormatException e) {
				spec = SpecularityMap.load(value);
			}
			break;
		case "bump":
			bump = BumpMap.load(value);
			break;
		case "emission":
			emission = EmissionMap.load(value);
			break;
		}
	}

	public FloatBuffer getVertices(Mesh mesh){
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

		/*int[] indexArray = new int[mesh.getIndices().size()];
		for (int i = 0; i < mesh.getIndices().size(); i++) {
			indexArray[i] = mesh.getIndices().get(i);
		}

		loadVertices(vertices);
		loadIndices(indexArray);*/
		return vertices;
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
		/*count = indices.length;

		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();

		// create vao and vbo
		vao = new VertexArrayObject();
		vao.bind();

		vbo = new VertexBufferObject();
		vbo.bind(GL_ARRAY_BUFFER);
		vbo.bufferData(vertices, GL_STATIC_DRAW);

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

		shader.setUniform1i("tex", SamplerMap.TEX_DEFAULT);

		int enableTex = ((texture != null) ? 1 : 0);
		shader.setUniform1i("enableTex", enableTex);

		shader.setUniform1i("spec", SamplerMap.SPEC_DEFAULT);

		int enableSpec = ((spec != null) ? 1 : 0);
		shader.setUniform1i("enableSpec", enableSpec);

		shader.setUniform1f("specularity", specularity);

		shader.setUniform1i("bump", SamplerMap.NORMAL_DEFAULT);

		int enableBump = ((bump != null) ? 1 : 0);
		shader.setUniform1i("enableBump", enableBump);

		shader.setUniform1i("emission", SamplerMap.EMISSION_DEFAULT);

		int enableEmission = ((emission != null) ? 1 : 0);
		shader.setUniform1i("enableEmission", enableEmission);

		shader.unbind();
		/*vbo.unbind(GL_ARRAY_BUFFER);
		vao.unbind();*/
	}

	public void update(Scene scene, Drawable d) {
		shader.bind();
		shader.setUniformMat4f("model", d.getMatrix());
		shader.setUniformMat4f("view", scene.getCamera().getLookAt());
		shader.setUniformVec3f("lightPos", scene.getLights().get(0).getPos());
		shader.setUniformVec3f("lightColor", scene.getLights().get(0).getColor());
		shader.setUniform1f("lightPower", scene.getLights().get(0).getPower());
		shader.unbind();
	}

	public void bindSamplerMaps() {
		if (texture != null)
			texture.bind();
		if (spec != null)
			spec.bind();
		if (bump != null)
			bump.bind();
		if (emission != null)
			emission.bind();
	}

	public void unbindSamplerMaps() {
		if (spec != null)
			spec.unbind();
		if (texture != null)
			texture.unbind();
		if (bump != null)
			bump.unbind();
		if (emission != null)
			emission.unbind();
	}
/*
	public void draw() {
		vao.bind();
		shader.bind();
		bindSamplerMaps();
		glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
		unbindSamplerMaps();
		shader.unbind();
		vao.unbind();
	}
*/
	@Override
	public void setVBOPointers(VertexBufferObject vbo) {
		vbo.bind(GL_ARRAY_BUFFER);
		
		int floatSize = 4;
		int stride = 14;
		int posAttrib = shader.getAttribLocation("position");
		shader.enableVertexAttribArray(posAttrib);
		shader.vertexAttribPointer(posAttrib, 3, stride * floatSize, 0);
		int normalAttrib = shader.getAttribLocation("normal");
		shader.enableVertexAttribArray(normalAttrib);
		shader.vertexAttribPointer(normalAttrib, 3, stride * floatSize,
				3 * floatSize);
		int colorAttrib = shader.getAttribLocation("color");
		shader.enableVertexAttribArray(colorAttrib);
		shader.vertexAttribPointer(colorAttrib, 3, stride * floatSize,
				6 * floatSize);
		int specularColorAttrib = shader.getAttribLocation("specularColor");
		shader.enableVertexAttribArray(specularColorAttrib);
		shader.vertexAttribPointer(specularColorAttrib, 3, stride * floatSize,
				9 * floatSize);
		int texAttrib = shader.getAttribLocation("texcoord");
		shader.enableVertexAttribArray(texAttrib);
		shader.vertexAttribPointer(texAttrib, 2, stride * floatSize,
				12 * floatSize);
		
		vbo.unbind(GL_ARRAY_BUFFER);
	}

	@Override
	public void bind() {
		shader.bind();
		bindSamplerMaps();
	}

	@Override
	public void unbind() {
		unbindSamplerMaps();
		shader.unbind();
	}

}
