package render.shader;

public class MultiplySN extends ShaderNode {

	public MultiplySN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}

	public void init(){
		inputs.put("in_value", null);
		inputs.put("in_color", null);
		outputs.put("out_color", new ColorSNV(this, "color"));
	}
	
	public void setInValue(FloatSNV color){
		inputs.put("in_value", color);
	}
	
	public void setInColor(ColorSNV color){
		inputs.put("in_color", color);
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}
	
	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + outputs.get("out_color").getName() + " = " + inputs.get("in_value").getName() + " * " + inputs.get("in_color").getName() + ";\n");
		glsl = sb.toString();
		return glsl;
	}

}
