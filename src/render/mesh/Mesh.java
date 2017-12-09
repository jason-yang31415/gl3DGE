package render.mesh;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class Mesh implements Serializable {

	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	ArrayList<Integer> indices = new ArrayList<Integer>();

	public void loadVertices(ArrayList<Vertex> vertices){
		this.vertices = vertices;
	}

	public void loadIndices(ArrayList<Integer> indices){
		this.indices = indices;
	}

	public void setMaterial(Material mat){
		for (Vertex v : vertices)
			v.setMaterial(mat);
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

	public void serialize(String path) throws IOException{
		FileOutputStream fos = new FileOutputStream(path);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(this);
		out.close();
		fos.close();
	}

	public static Mesh deserialize(String path) throws IOException, ClassNotFoundException{
		InputStream i = Mesh.class.getResourceAsStream(path);
		ObjectInputStream in = new ObjectInputStream(i);
		Mesh mesh = (Mesh) in.readObject();
		in.close();
		i.close();
		return mesh;
	}

}
