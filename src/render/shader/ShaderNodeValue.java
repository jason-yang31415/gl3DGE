package render.shader;

public abstract class ShaderNodeValue {
	
	final ShaderNode parent;
	final String name;
	
	public ShaderNodeValue(ShaderNode parent, String name){
		this.parent = parent;
		this.name = name;
	}
	
	public String getName(){
		return String.format("%s_v%s", (parent == null ? "input" : parent.getName()), name);
	}
	
	public abstract String getGLSL();
	
}
