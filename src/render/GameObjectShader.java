package render;



public abstract class GameObjectShader extends ObjectShader {

	int count;
	
	public GameObjectShader(String vertexPath, String fragmentPath){
		super(vertexPath, fragmentPath);
	}
	
	public abstract void loadMeshAttribute(String param, String value);
	
}
