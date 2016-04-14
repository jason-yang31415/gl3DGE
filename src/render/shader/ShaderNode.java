package render.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ShaderNode {

	final int id;
	
	String glsl;

	Map<String, ShaderNodeValue> inputs = new HashMap<String, ShaderNodeValue>();
	Map<String, ShaderNodeValue> outputs = new HashMap<String, ShaderNodeValue>();
	
	public ShaderNode(int id){
		this.id = id;
	}
	
	public String getName(){
		return String.format("n%d", id);
	}
	
	public String variable(String var){
		return String.format("%s_%s", getName(), var);
	}
	
	public abstract String getGLSL();
	
}
