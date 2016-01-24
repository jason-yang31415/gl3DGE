package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import logic.Transform;
import render.Drawable;
import render.GameObjectInit;
import render.mesh.Resource;
import util.Vector3f;

public class GameObject extends Drawable {

	// RENDER
	
	// LOGIC
	Vector3f v;
	BoundingSphere bound;
	
	public static GameObject loadGameObject(String path, GameObjectInit goi) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(GameObject.class.getResourceAsStream(Resource.GAMEOBJECT_DIR + path)));
		String line;
		
		float radius = 1.0f;
		while ((line = reader.readLine()) != null){
			line = line.replace(" ", "");
			String[] s = line.split(":");
			
			String param = s[0];
			String value = s[1];
			switch (param){
			case "radius":
				radius = Float.parseFloat(value);
				break;
			default:
				goi.load(param, value);
				break;
			}
		}
		reader.close();
		
		goi.loadObjectData();
		goi.loadShaders();
		
		goi.check();
		
		return new GameObject(goi);
	}
	
	/*public static GameObject loadFromFloatArray(float[] verts, Shader vertexShader, Shader fragmentShader, Texture texture){
		// GRAPHICS
		
		// number of verts
		FloatBuffer vertices = BufferUtils.createFloatBuffer(verts.length);
		vertices.put(verts).flip();
		int count = (int) Math.floor(verts.length / 9);
		
		return new GameObject(vertices, vertexShader, fragmentShader, count, texture);
	}*/
	
	/*public static GameObject loadFromPath(String path) throws FileNotFoundException, IOException{
		Drawable drawable = OBJLoader.loadGameObject(GameObject.class.getResourceAsStream(path));
		return (GameObject) drawable;
	}*/
	
	/*public GameObject(FloatBuffer vertices, Shader vertexShader, Shader fragmentShader, int[] indices, TextureMap textureMap, SpecularityMap specMap) {
		super(vertices, vertexShader, fragmentShader, indices, textureMap, specMap);
		bound = new BoundingSphere(this, 1);
		v = new Vector3f();
	}*/
	
	public GameObject(GameObjectInit goi){
		super(goi);
		bound = new BoundingSphere(this, 1);
		v = new Vector3f();
	}
	
	public boolean collision(Transform t){
		return bound.collision(t);
	}
	
	public BoundingSphere getBound(){
		return bound;
	}
	
}
