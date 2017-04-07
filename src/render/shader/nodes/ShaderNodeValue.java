package render.shader.nodes;

public abstract class ShaderNodeValue {
	
	public static String INPUT_POSITION = "position";
	public static String INPUT_WORLD_POSITION = "world_position";
	public static String INPUT_NORMAL = "normal";
	public static String INPUT_COLOR = "color";
	public static String INPUT_TEXTURE_COORDINATE = "texture_coordinate";
	
	//public static String UNIFORM_LIGHT_POSITION = "light_position";
	public static String UNIFORM_CAMERA_POSITION = "camera_position";
	
	public static String UNIFORM_LIGHT_UBO = "light_ubo";
	public static String UNIFORM_LIGHT_UBO_STRUCT = "light_ubo_struct";
	public static String UNIFORM_LIGHT_UBO_POSITION = "light_ubo_position";
	public static String UNIFORM_LIGHT_UBO_COLOR = "light_ubo_color";
	public static String UNIFORM_LIGHT_UBO_POWER = "light_ubo_power";
	public static String UNIFORM_LIGHT_UBO_NUMBER = "light_ubo_number";
	
	final ShaderNode parent;
	String name;
	int size;
	
	public ShaderNodeValue(ShaderNode parent, String name){
		this.parent = parent;
		this.name = name;
	}
	
	public String getName(){
		String parentString = null;
		if (parent instanceof InputSN)
			parentString = "input";
		else if (parent == null)
			parentString = "constant";
		else
			parentString = parent.getName();
		return String.format("%s_v%s", parentString, name);
	}
	
	public int getSize(){
		return size;
	}
	
	public abstract String getType();
	
	public abstract String getGLSL();
	
	public abstract String getAttribute();
	
	public abstract String getVarying();
	
	public abstract String getVertexGLSL();
	
}
