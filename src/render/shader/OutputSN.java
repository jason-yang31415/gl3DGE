package render.shader;

public class OutputSN extends ShaderNode {

	public OutputSN() {
		super(0);
		
		init();
	}
	
	public void init(){
		inputs.put("in_color", null);
	}
	
	public void setInColor(ColorSNV color){
		inputs.put("in_color", color);
	}

	@Override
	public String getName(){
		return "output";
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("fragColor = vec4(" + inputs.get("in_color").getName() + ", 1);\n");
		//sb.append("fragColor = vec4(1, 1, 1, 1);\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
