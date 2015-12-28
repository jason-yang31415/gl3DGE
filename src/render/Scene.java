package render;

public class Scene {

	Camera camera;
	Light light;
	
	public void setCamera(Camera camera){
		this.camera = camera;
	}
	
	public void setLight(Light light){
		this.light = light;
	}
	
	public Camera getCamera(){
		return camera;
	}
	
	public Light getLight(){
		return light;
	}
	
}
