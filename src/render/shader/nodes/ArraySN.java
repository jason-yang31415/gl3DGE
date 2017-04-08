package render.shader.nodes;

public class ArraySN extends ShaderNode {

	public ArraySN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_array", null);
		inputs.put("in_index", null);
		outputs.put("out_value", null);
	}
	
	public void setInArray(ArraySNV array){
		inputs.put("in_array", array);
		outputs.put("out_value", ShaderNodeValue.getNewInstance(array.getValue(), this, "value"));
	}
	
	public void setInIndex(ValueSNV index){
		inputs.put("in_index", index);
	}
	
	public ShaderNodeValue getOutValue(){
		return outputs.get("out_value");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append(outputs.get("out_value").getType() + " " + outputs.get("out_value").getName() + " = " + inputs.get("in_array").getName() + "[int(" + inputs.get("in_index").getName() + ")];\n");
		glsl = sb.toString();
		return glsl;
	}

}
