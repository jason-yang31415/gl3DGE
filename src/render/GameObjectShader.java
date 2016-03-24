package render;



public abstract class GameObjectShader extends ObjectShader {
	
	public GameObjectShader(String vertexPath, String fragmentPath){
		super(vertexPath, fragmentPath);
	}
	
	public abstract void loadMeshAttribute(String param, String value);
	
}
