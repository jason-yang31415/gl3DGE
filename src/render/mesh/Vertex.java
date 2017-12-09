package render.mesh;

import java.io.Serializable;

import util.Vector2f;
import util.Vector3f;

public class Vertex implements Serializable {

	Vector3f position, normal;
	Vector3f ambient, diffuse, specular;

	Vector2f tex;

	public Vertex(float x, float y, float z){
		position = new Vector3f(x, y, z);
	}

	public Vertex(Vector3f position){
		setPosition(position);
	}

	public Vertex(Vector3f position, Vector3f normal, Material mat, Vector2f tex){
		setPosition(position);
		if (normal != null)
			setNormal(normal);
		if (mat != null)
			setMaterial(mat);
		if (tex != null)
			setTextureCoordinate(tex);
	}

	public void setPosition(Vector3f position){
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
