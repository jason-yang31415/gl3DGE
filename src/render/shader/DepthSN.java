package render.shader;

public class DepthSN extends ShaderNode {

	public DepthSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());

		init();
	}
	
	public void init(){
		inputs.put("in_sampler", null);
		inputs.put("in_texturecoordinate", null);
		outputs.put("out_z", new ValueSNV(this, "z"));
		outputs.put("out_depth", new ValueSNV(this, "depth"));
		outputs.put("out_color", new ColorSNV(this, "color"));
	}
	
	public void setInSampler(SamplerSNV sampler){
		inputs.put("in_sampler", sampler);
	}
	
	public void setInTextureCoordinate(TextureCoordinateSNV textureCoordinate){
		inputs.put("in_texturecoordinate", textureCoordinate);
	}
	
	public ValueSNV getOutZ(){
		return (ValueSNV) outputs.get("out_z");
	}
	
	public ValueSNV getOutDepth(){
		return (ValueSNV) outputs.get("out_depth");
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}
	
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("float " + variable("value") + " = texture(" + inputs.get("in_sampler").getName() + ", " + inputs.get("in_texturecoordinate").getName() + ").x;\n");
		sb.append("float " + outputs.get("out_z").getName() + " = " + variable("value") + ";\n");
		sb.append("float " + variable("n") + " = 0.01;\n");
		sb.append("float " + variable("f") + " = 10;\n");
		sb.append("float " + outputs.get("out_depth").getName() + " = (2.0 * " + variable("n") + ") / (" + variable("f") + " + " + variable("n") + " - " + variable("value") + " * (" + variable("f") + " - " + variable("n") + "));\n");
		//sb.append("vec3 " + outputs.get("out_color").getName() + " = vec3(" + variable("value") + ");\n");
		sb.append("vec3 " + outputs.get("out_color").getName() + " = vec3(" + outputs.get("out_depth").getName() + ");\n");
		glsl = sb.toString();
		return glsl;
	}

}
