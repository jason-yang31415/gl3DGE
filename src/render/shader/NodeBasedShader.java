package render.shader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

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

import org.lwjgl.BufferUtils;

import render.Drawable;
import render.SamplerMap;
import render.Scene;
import render.VertexArrayObject;
import render.VertexBufferObject;
import render.mesh.Mesh;
import render.mesh.Vertex;
import util.Vector3f;

public class NodeBasedShader extends ObjectShader {
	
	int[] indices;

	int ebo;
	
	ArrayList<ShaderNodeValue> inputs = new ArrayList<ShaderNodeValue>();
	ArrayList<ShaderNode> nodes = new ArrayList<ShaderNode>();
	OutputSN out = new OutputSN();
	
	int currentId = 0;
	
	public int genNodes(){
		int id = currentId;
		currentId++;
		return id;
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

	@Override
	public void loadShaders() throws IOException {
		String vertexSource = 				"#version 150 core"
								+ "\n" +	"in vec3 position;"
								+ "\n" +	"in vec3 normal;"
								+ "\n" +	"in vec3 in_color;"
								+ "\n" +	"out vec3 N;"
								+ "\n" +	"out vec3 v;"
								+ "\n" +	"out vec3 color;"
								+ "\n" +	"uniform mat4 model;"
								+ "\n" +	"uniform mat4 view;"
								+ "\n" +	"uniform mat4 projection;"
								+ "\n" +	"uniform vec3 lightPos;"
								+ "\n" +	"void main() {"
								+ "\n" +	"	color = in_color;"
								+ "\n" +	"	v = (model * vec4(position, 1)).xyz;"
								+ "\n" +	"	N = normalize(normal);"
								+ "\n" +	"	mat4 mvp = projection * view * model;"
								+ "\n" +	"	gl_Position = mvp * vec4(position, 1.0);"
								+ "\n" +	"}"
								+ "\n" +	"";
		Shader vertexShader = new Shader(GL_VERTEX_SHADER, vertexSource);
		String fragmentSource = getFragmentSource();
		Shader fragmentShader = new Shader(GL_FRAGMENT_SHADER, fragmentSource);
		loadVertexShader(vertexShader);
		loadFragmentShader(fragmentShader);
	}
	
	public String getFragmentSource(){
		StringBuilder sb = new StringBuilder();
		sb.append("#version 150 core\n");
		sb.append("in vec3 N;\n");
		sb.append("in vec3 v;\n");
		sb.append("in vec3 color;\n");
		sb.append("out vec4 fragColor;\n");
		sb.append("uniform vec3 lightPos;\n");
		sb.append("void main() {\n");
		sb.append("	vec3 global_position = v;\n");
		sb.append("	vec3 global_normal = N;\n");
		sb.append("	vec3 global_lightPos = lightPos;\n");
		for (ShaderNodeValue snv : inputs){
			sb.append(snv.getGLSL());
		}
		for (ShaderNode n : nodes){
			sb.append(n.getGLSL());
		}
		sb.append(out.getGLSL());
		sb.append("}\n");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public void addNode(ShaderNode node){
		nodes.add(node);
	}
	
	public void addInput(ShaderNodeValue value){
		inputs.add(value);
	}
	
	public OutputSN getOutputNode(){
		return out;
	}

	@Override
	public void init() {
		count = indices.length;

		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();

		/*
		 * for (int i : indices){ System.out.println(i); }
		 */

		// create vao and vbo
		vao = new VertexArrayObject();
		vao.bind();

		vbo = new VertexBufferObject();
		vbo.bind(GL_ARRAY_BUFFER);
		vbo.bufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

		// create shader program
		shader = new ShaderProgram();
		shader.attachShader(vertexShader);
		shader.attachShader(fragmentShader);
		shader.bindFragDataLocation(0, "fragColor");
		shader.link();
		shader.bind();

		int floatSize = 4;
		int stride = 9;
		int posAttrib = shader.getAttribLocation("position");
		shader.enableVertexAttribArray(posAttrib);
		shader.vertexAttribPointer(posAttrib, 3, stride * floatSize, 0);
		int normalAttrib = shader.getAttribLocation("normal");
		shader.enableVertexAttribArray(normalAttrib);
		shader.vertexAttribPointer(normalAttrib, 3, stride * floatSize,
				3 * floatSize);
		int colorAttrib = shader.getAttribLocation("in_color");
		shader.enableVertexAttribArray(colorAttrib);
		shader.vertexAttribPointer(colorAttrib, 3, stride * floatSize,
				6 * floatSize);

		shader.unbind();
		vbo.unbind(GL_ARRAY_BUFFER);
		vao.unbind();
	}

	@Override
	public void update(Scene scene, Drawable d) {
		shader.bind();
		shader.setUniformMat4f("model", d.getMatrix());
		shader.setUniformMat4f("view", scene.getCamera().getLookAt());
		shader.setUniformVec3f("lightPos", scene.getLight().getPos());
		shader.unbind();
	}

	@Override
	public void draw() {
		vao.bind();
		shader.bind();
		glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
		shader.unbind();
		vao.unbind();
	}

}
