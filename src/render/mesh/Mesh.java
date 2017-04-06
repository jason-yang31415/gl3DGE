package render.mesh;

import java.util.ArrayList;

import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class Mesh {

	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	ArrayList<Integer> indices = new ArrayList<Integer>();
	
	public void loadVertices(ArrayList<Vertex> vertices){
		this.vertices = vertices;
	}
	
	public void loadIndices(ArrayList<Integer> indices){
		this.indices = indices;
	}
	
	public ArrayList<Vertex> getVertices(){
		return vertices;
	}
	
	public ArrayList<Integer> getIndices(){
		return indices;
	}
	
	public void transform(Matrix4f transform){
		for (Vertex v : vertices){
			Vector4f n = transform.multiply(new Vector4f(v.getPosition(), 1));
			v.setPosition(new Vector3f(n.x, n.y, n.z));
		}
	}
	
}
