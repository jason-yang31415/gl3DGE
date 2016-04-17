package render.shader;

public abstract class ShaderNodeValue {
	
	public static String INPUT_POSITION = "position";
	public static String INPUT_NORMAL = "normal";
	public static String INPUT_COLOR = "color";
	
	final ShaderNode parent;
	final String name;
	int size;
	
	public ShaderNodeValue(ShaderNode parent, String name){
		this.parent = parent;
		this.name = name;
	}
	
	public String getName(){
		return String.format("%s_v%s", (parent == null ? "input" : parent.getName()), name);
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
