package game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import logic.Transform;
import render.Drawable;
import render.mesh.Mesh;
import render.mesh.OBJLoader;
import render.mesh.Resource;
import render.shader.GameObjectShader;
import util.Matrix4f;
import util.Vector3f;

public class GameObject extends Drawable {
	
	// RENDER
	
	// LOGIC
	BoundingSphere bound;
	
	public static GameObject loadGameObject(String path, GameObjectShader gos) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(GameObject.class.getResourceAsStream(Resource.GAMEOBJECT_DIR + path)));
		String line;
		
		float radius = 1.0f;
		String obj = "";
		boolean smooth = false;
		while ((line = reader.readLine()) != null){
			line = line.replace(" ", "");
			String[] s = line.split(":");
			
			String param = s[0];
			String value = s[1];
			switch (param){
			case "radius":
				radius = Float.parseFloat(value);
				break;
			case "obj":
				obj = value;
				break;
			case "smooth":
				smooth = Boolean.parseBoolean(value);
				break;
			default:
				gos.loadMeshAttribute(param, value);
				break;
			}
		}
		reader.close();
		
		if (!obj.equals("")) {
			// OBJLoader.loadGameObjectData(this, obj, smooth);
			Mesh mesh = new Mesh();
			OBJLoader.loadGameObjectData(mesh, obj, smooth);
			
			gos.loadObjectData(mesh);
		} else
			throw new FileNotFoundException("Could not find obj file");
		
		gos.loadShaders();
		
		gos.check();
		
		return new GameObject(gos, radius);
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
	
	public GameObject(GameObjectShader goi, float radius){
		super(goi);
		
		// MVP
		float ratio = 1;
		Matrix4f projection = Matrix4f.perspective(90, ratio, 0.01f, 100);
		//Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
		goi.setMVP(new Matrix4f(), new Matrix4f(), projection);
		
		bound = new BoundingSphere(this, 1);
	}
	
	public boolean collision(Transform t){
		return bound.collision(t);
	}
	
	public BoundingSphere getBound(){
		return bound;
	}
	
}
