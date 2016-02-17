package render.mesh;

import java.util.ArrayList;

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
	
}
