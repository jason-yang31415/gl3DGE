package render;

import java.util.ArrayList;

public class Scene {

	Camera camera;
	Light light;
	ArrayList<Light> lights = new ArrayList<Light>();
	
	public void setCamera(Camera camera){
		this.camera = camera;
	}
	
	/*public void setLight(Light light){
		this.light = light;
	}*/
	
	public Camera getCamera(){
		return camera;
	}
	
	/*public Light getLight(){
		return light;
	}*/
	
	public void addLight(Light light){
		lights.add(light);
	}
	
	public ArrayList<Light> getLights(){
		return lights;
	}
	
}
