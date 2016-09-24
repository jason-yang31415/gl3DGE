package render.shader.nodes;

public class OutputSN extends ShaderNode {

	public OutputSN(NodeBasedShader nbs) {
		super(nbs, 0);
		
		init();
	}
	
	public void init(){
		inputs.put("in_value", null);
	}
	
	public void setInValue(ValueSNV value){
		inputs.put("in_value", value);
	}

	@Override
	public String getName(){
		return "output";
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("fragColor = " + inputs.get("in_value").getName() + ";\n");
		//sb.append("fragColor = vec4(1, 1, 1, 1);\n");
		glsl = sb.toString();
		return glsl;
	}
	
}