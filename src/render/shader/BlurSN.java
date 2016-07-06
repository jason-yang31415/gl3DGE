package render.shader;

public class BlurSN extends ShaderNode {

	public BlurSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());

		init();
	}
	
	public void init(){
		inputs.put("in_color", null);
		inputs.put("in_sampler", null);
		inputs.put("in_texturecoordinate", null);
		inputs.put("in_radius", null);
		outputs.put("out_color", new ColorSNV(this, "color"));
	}
	
	public void setInColor(ColorSNV color){
		inputs.put("in_color", color);
	}
	
	public void setInSampler(SamplerSNV sampler){
		inputs.put("in_sampler", sampler);
	}
	
	public void setInTextureCoordinate(TextureCoordinateSNV textureCoordinate){
		inputs.put("in_texturecoordinate", textureCoordinate);
	}
	
	public void setInRadius(ValueSNV radius){
		inputs.put("in_radius", radius);
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}
	
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("int " + variable("radius") + " = int(" + inputs.get("in_radius").getName() + ");\n");
		sb.append("float " + variable("blurSizeX") + " = 1 / 960.0;");
		sb.append("float " + variable("blurSizeY") + " = 1 / 540.0;");
		sb.append("vec3 " + variable("color") + " = vec3(0, 0, 0);\n");
		sb.append("for (int " + variable("x") + " = -" + variable("radius") + "; " + variable("x") + " <= " + variable("radius") + "; " + variable("x") + "++){\n");
		sb.append("  for (int " + variable("y") + " = -" + variable("radius") + "; " + variable("y") + " <= " + variable("radius") + "; " + variable("y") + "++){\n");
		sb.append("    vec2 " + variable("coord") + " = vec2(" + inputs.get("in_texturecoordinate").getName() + ".x + " + variable("x") + " * " + variable("blurSizeX") + ", " + inputs.get("in_texturecoordinate").getName() + ".y + " + variable("y") + " * " + variable("blurSizeY") + ");\n");
		sb.append("    vec3 " + variable("ptcolor") + " = texture(" + inputs.get("in_sampler").getName() + ", " + variable("coord") + ").xyz;\n");
		sb.append("    " + variable("color") + " += " + variable("ptcolor") + " / pow(" + variable("radius") + " * 2 + 1, 2);\n");
		sb.append("  }\n");
		sb.append("}\n");
		/*sb.append("vec3 " + variable("color") + " = vec3(0, 0, 0);\n");
		sb.append("int " + variable("y") + ";\n");
		sb.append("for (" + variable("y") + " = -" + variable("radius") + "; " + variable("y") + " <= " + variable("radius") + "; " + variable("y") + "++){\n");
		sb.append("  vec2 " + variable("coord") + " = vec2(" + inputs.get("in_texturecoordinate").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y + " + variable("y") + " * " + variable("blurSizeY") + ");\n");
		sb.append("  vec3 " + variable("ptcolor") + " = texture(" + inputs.get("in_sampler").getName() + ", " + variable("coord") + ").xyz;\n");
		sb.append("  if (" + variable("y") + " == 0)\n");
		sb.append("    " + variable("color") + " += " + variable("xcolor") + " / (" + variable("radius") + " * 2 + 1);\n");
		sb.append("  else\n");
		sb.append("    " + variable("color") + " += " + variable("ptcolor") + " / (" + variable("radius") + " * 2 + 1);\n");
		sb.append("}\n");*/
		sb.append("vec3 " + outputs.get("out_color").getName() + " = " + variable("color") + ";\n");
		//sb.append("vec3 " + outputs.get("out_color").getName() + " = texture(" + inputs.get("in_sampler").getName() + ", " + inputs.get("in_texturecoordinate").getName() + ").xyz;\n");
		glsl = sb.toString();
		return glsl;
	}

}
