package render.mesh;

import util.Vector2f;
import util.Vector3f;

public class Vertex {

	Vector3f position, normal;
	Vector3f ambient, diffuse, specular;
	
	Vector2f tex;
	
	public Vertex(float x, float y, float z){
		position = new Vector3f(x, y, z);
	}
	
}
