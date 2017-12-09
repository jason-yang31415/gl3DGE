package render;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import render.mesh.Mesh;
import render.shader.ObjectShader;

public class VertexDataObject {

	private VertexBufferObject vbo;
	private VertexArrayObject vao;
	private ElementBufferObject ebo;

	private int count;

	public void loadVertexData(Mesh mesh, ObjectShader shader){
		loadVAO();
		loadVBO(mesh, shader);
		loadEBO(mesh);
	}

	public void loadVAO(){
		vao = new VertexArrayObject();
	}

	public void loadVBO(Mesh mesh, ObjectShader shader){
		FloatBuffer vertices = shader.getVertices(mesh);
		vao.bind();
		vbo = new VertexBufferObject();
		vbo.bind(GL_ARRAY_BUFFER);
		vbo.bufferData(vertices, GL_STATIC_DRAW);
		shader.setVBOPointers(vbo);
		vbo.unbind(GL_ARRAY_BUFFER);
		vao.unbind();
	}

	public void loadEBO(Mesh mesh){
		vao.bind();
		int[] indices = new int[mesh.getIndices().size()];
		for (int i = 0; i < mesh.getIndices().size(); i++) {
			indices[i] = mesh.getIndices().get(i);
		}
		count = indices.length;
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();
		ebo = new ElementBufferObject();
		ebo.bind();
		ebo.bufferData(indexBuffer, GL_STATIC_DRAW);
		ebo.unbind();
		vao.unbind();
	}

	public VertexArrayObject getVAO(){
		return vao;
	}

	public VertexBufferObject getVBO(){
		return vbo;
	}

	public ElementBufferObject getEBO(){
		return ebo;
	}

	public int getCount(){
		return count;
	}

	public void draw(ObjectShader os){
		vao.bind();
		ebo.bind();
		os.bind();
		glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
		os.unbind();
		ebo.unbind();
		vao.unbind();
	}

}
