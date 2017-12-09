package render.shader.nodes;

public class StructureSN extends ShaderNode {

	String key;

	public StructureSN(NodeBasedShader nbs, String key) {
		super(nbs, nbs.genNodes());
		this.key = key;

		init();
	}

	public void init(){
		inputs.put("in_structure", null);
		outputs.put("out_value", null);
	}

	public void setInStructure(StructureSNV struct){
		inputs.put("in_structure", struct);
		outputs.put("out_value", ShaderNodeValue.getNewInstance(struct.getStruct().getValues().get(key), this, "value"));
	}

	public ShaderNodeValue getOutValue(){
		return outputs.get("out_value");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append(outputs.get("out_value").getType() + " " + outputs.get("out_value").getName() + " = " + inputs.get("in_structure").getName() + "." + ((StructureSNV) inputs.get("in_structure")).getStruct().getValues().get(key).getName() + ";\n");
		glsl = sb.toString();
		return glsl;
	}

}
