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
	
	public Vertex(Vector3f position){
		this.position = position;
	}
	
	public void setNormal(Vector3f normal){
		this.normal = normal;
	}
	
	public void setTextureCoordinate(Vector2f tex){
		this.tex = tex;
	}
	
	public void setMaterial(Material mat){
		ambient = mat.ambient;
		diffuse = mat.diffuse;
		specular = mat.specular;
	}
	
	public Vector3f getPosition(){
		return position;
	}
	
	public Vector3f getNormal(){
		return normal;
	}
	
	public Vector3f getAmbientColor(){
		return ambient;
	}
	
	public Vector3f getDiffuseColor(){
		return diffuse;
	}
	
	public Vector3f getSpecularColor(){
		return specular;
	}
	
	public Vector2f getTextureCoordinate(){
		return tex;
	}
	
}
