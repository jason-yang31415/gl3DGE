package render.mesh;

import util.Vector3f;

public class Material {

	Vector3f ambient, diffuse, specular;
	
	public void setAmbient(Vector3f ambient){
		this.ambient = ambient;
	}
	
	public void setDiffuse(Vector3f diffuse){
		this.diffuse = diffuse;
	}
	
	public void setSpecular(Vector3f specular){
		this.specular = specular;
	}
	
}
