package game;

import java.io.IOException;

import logic.Transform;
import render.Drawable;
import render.GameObjectInit;
import util.Vector3f;

public class GameObject extends Drawable {

	// RENDER
	
	// LOGIC
	Vector3f v;
	BoundingSphere bound;
	
	public static GameObject loadGameObject(String path, GameObjectInit goi) throws IOException {
		/*BufferedReader reader = new BufferedReader(new InputStreamReader(GameObject.class.getResourceAsStream("/mesh/" + path)));
		String line;
		
		String vertexPath = "vertex";
		String fragmentPath = "fragment";
		String obj = "";
		TextureMap tex = null;
		float radius = 1.0f;
		while ((line = reader.readLine()) != null){
			line = line.replace(" ", "");
			String[] s = line.split(":");
			if (s[0].equals("vertex"))
				vertexPath = s[1];
			else if (s[0].equals("fragment"))
				fragmentPath = s[1];
			else if (s[0].equals("obj"))
				obj = s[1];
			else if (s[0].equals("texture"))
				tex = TextureMap.load(s[1]);
			else if (s[0].equals("radius"))
				radius = Float.parseFloat(s[1]);
		}
		reader.close();
		
		// TEMP
		SpecularityMap spec = SpecularityMap.load("/tex/brick_spec.bmp");
		
		if (obj != ""){
			GameObject go = OBJLoader.loadGameObject(obj, vertexPath, fragmentPath, tex, spec);
			go.getBound().setSize(radius);
			return go;
		}
		else {
			throw new FileNotFoundException("Could not find obj file");
		}*/
		goi.load(path);
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
