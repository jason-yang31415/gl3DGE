package render.shader.nodes;

public class AlphaTestSN extends ShaderNode {

	public AlphaTestSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());

		init();
	}
	
	public void init(){
		inputs.put("in_value", null);
	}
	
	public void setInValue(ValueSNV value){
		inputs.put("in_value", value);
	}
	
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("if (" + inputs.get("in_value").getName() + " <= 0.0){\n");
		sb.append("  discard;\n");
		sb.append("}\n");
		glsl = sb.toString();
		return glsl;
	}

}
