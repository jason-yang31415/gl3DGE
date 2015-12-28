package render.mesh;

public class Resource {

	public static String GAMEOBJECT_DIR = "/";
	public static String OBJ_DIR = "/";
	public static String TEX_DIR = "/";
	public static String SHADER_DIR = "/";
	
	public static void setGameObjectDir(String path){
		GAMEOBJECT_DIR = path;
	}
	
	public static void setOBJDir(String path){
		OBJ_DIR = path;
	}
	
	public static void setTexDir(String path){
		TEX_DIR = path;
	}
	
	public static void setShaderDir(String path){
		SHADER_DIR = path;
	}
	
}
