package render.mesh;

import java.util.ArrayList;
import java.util.Arrays;

import util.Vector2f;
import util.Vector3f;

public class MeshFactory {

	public static Mesh createSquare(Material mat){
		Mesh mesh = new Mesh();
		ArrayList<Vertex> verts = new ArrayList<Vertex>();

		verts.add(new Vertex(new Vector3f(-1, -1, 0), new Vector3f(0, 0, 1), mat, new Vector2f(0, 0)));
		verts.add(new Vertex(new Vector3f(1, -1, 0), new Vector3f(0, 0, 1), mat, new Vector2f(1, 0)));
		verts.add(new Vertex(new Vector3f(1, 1, 0), new Vector3f(0, 0, 1), mat, new Vector2f(1, 1)));
		verts.add(new Vertex(new Vector3f(-1, 1, 0), new Vector3f(0, 0, 1), mat, new Vector2f(0, 1)));
		mesh.loadVertices(verts);
		
		Integer[] index_array = {0, 1, 2, 3, 0, 2};
		ArrayList<Integer> indices = new ArrayList(Arrays.asList(index_array));
		mesh.loadIndices(indices);
		return mesh;
	}
	
}
