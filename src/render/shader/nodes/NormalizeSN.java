package render.shader.nodes;

public class NormalizeSN extends ShaderNode {

	public NormalizeSN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_value", null);
		outputs.put("out_value", new ValueSNV(this, "value"));
	}
	
	public void setInValue(ValueSNV value){
		inputs.put("in_value", value);
	}
	
	public ValueSNV getOutValue(){
		return (ValueSNV) outputs.get("out_value");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append(((ValueSNV) outputs.get("out_value")).getType() + " " + outputs.get("out_value").getName() + " = normalize(" + inputs.get("in_value").getName() + ");\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
