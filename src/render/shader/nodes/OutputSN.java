package render.shader.nodes;

public class OutputSN extends ShaderNode {

	public OutputSN(NodeBasedShader nbs) {
		super(nbs, 0);
		
		init();
	}
	
	public void init(){
		inputs.put("in_color", null);
		inputs.put("in_alpha", null);
	}
	
	public void setInColor(ValueSNV color){
		inputs.put("in_color", color);
	}
	
	public void setInAlpha(ValueSNV alpha){
		inputs.put("in_alpha", alpha);
	}

	@Override
	public String getName(){
		return "output";
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		if (inputs.get("in_alpha") != null)
			sb.append("fragColor = vec4(" + inputs.get("in_color").getName() + ", " + inputs.get("in_alpha").getName() + ");\n");
		else
			sb.append("fragColor = vec4(" + inputs.get("in_color").getName() + ", 1);\n");
		//sb.append("fragColor = vec4(1, 1, 1, 1);\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
