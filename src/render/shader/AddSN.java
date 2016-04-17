package render.shader;

public class AddSN extends ShaderNode {

	public AddSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}

	public void init(){
		inputs.put("in_color1", null);
		inputs.put("in_color2", null);
		outputs.put("out_color", new ColorSNV(this, "color"));
	}
	
	public void setInColor1(ColorSNV color){
		inputs.put("in_color1", color);
	}
	
	public void setInColor2(ColorSNV color){
		inputs.put("in_color2", color);
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}
	
	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + outputs.get("out_color").getName() + " = " + inputs.get("in_color1").getName() + " + " + inputs.get("in_color2").getName() + ";\n");
		glsl = sb.toString();
		return glsl;
	}

}
