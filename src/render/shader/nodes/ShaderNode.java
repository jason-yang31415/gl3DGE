package render.shader.nodes;

import java.util.HashMap;
import java.util.Map;

public abstract class ShaderNode {
	
	public static String INPUT_LIGHT_INDEX = "in_light_index";

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
	
	public Map<String, ShaderNodeValue> getInputs(){
		return inputs;
	}
	
	public Map<String, ShaderNodeValue> getOutputs(){
		return outputs;
	}
	
	public String variable(String var){
		return String.format("%s_%s", getName(), var);
	}
	
	public void setInLightIndex(ValueSNV index){
		inputs.put(ShaderNode.INPUT_LIGHT_INDEX, index);
	}
	
	public abstract String getGLSL();
	
	public String getLightProperty(String property){
		ArraySNV array = (ArraySNV) NodeBasedShader.getUBO().getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_UBO_STRUCT);
		StructureSNV struct = (StructureSNV) array.getValue();
		if (getInputs().get(INPUT_LIGHT_INDEX) != null)
			return array.getName() + "[int(" + getInputs().get(INPUT_LIGHT_INDEX).getName() + ")]." + struct.getStruct().getValues().get(property).getName();
		else
			return array.getName() + "[0]." + struct.getStruct().getValues().get(property).getName();
	}
	
}
