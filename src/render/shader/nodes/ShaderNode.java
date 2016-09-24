package render.shader.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ShaderNode {

	final NodeBasedShader nbs;
	final int id;
	
	String glsl;

	Map<String, ShaderNodeValue> inputs = new HashMap<String, ShaderNodeValue>();
	Map<String, ShaderNodeValue> outputs = new HashMap<String, ShaderNodeValue>();
	
	public ShaderNode(NodeBasedShader nbs, int id){
		this.nbs = nbs;
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