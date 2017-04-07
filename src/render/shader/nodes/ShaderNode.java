package render.shader.nodes;

import java.util.HashMap;
import java.util.Map;

public abstract class ShaderNode {

	protected final NodeBasedShader nbs;
	protected final int id;
	
	protected String glsl;

	protected Map<String, ShaderNodeValue> inputs = new HashMap<String, ShaderNodeValue>();
	protected Map<String, ShaderNodeValue> outputs = new HashMap<String, ShaderNodeValue>();
	
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
	
	public static String getLightProperty(String property){
		StructureSNV struct = (StructureSNV) NodeBasedShader.getUBO().getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_UBO_STRUCT);
		return struct.getName() + "." + struct.getStruct().getValues().get(property).getName();
	}
	
}
