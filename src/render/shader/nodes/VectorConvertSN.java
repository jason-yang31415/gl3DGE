package render.shader.nodes;

public class VectorConvertSN extends ShaderNode {

	public VectorConvertSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_value", null);
		outputs.put("out_value", new ColorSNV(this, "value"));
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
		sb.append("vec4 " + outputs.get("out_value").getName() + " = vec4(" + inputs.get("in_value").getName() + ", 1);\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
