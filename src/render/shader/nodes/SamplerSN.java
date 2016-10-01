package render.shader.nodes;

public class SamplerSN extends ShaderNode {

	public SamplerSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_sampler", null);
		inputs.put("in_texturecoordinate", null);
		outputs.put("out_color", new ColorSNV(this, "color"));
		outputs.put("out_alpha", new ColorSNV(this, "alpha"));
	}
	
	public void setInSampler(SamplerSNV sampler){
		inputs.put("in_sampler", sampler);
	}
	
	public void setInTextureCoordinate(TextureCoordinateSNV textureCoordinate){
		inputs.put("in_texturecoordinate", textureCoordinate);
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}
	
	public ValueSNV getOutAlpha(){
		return (ValueSNV) outputs.get("out_alpha");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec4 " + variable("color4") + " = texture(" + inputs.get("in_sampler").getName() + ", " + inputs.get("in_texturecoordinate").getName() + ");\n");
		sb.append("vec3 " + outputs.get("out_color").getName() + " = " + variable("color4") + ".xyz;\n");
		sb.append("float " + outputs.get("out_alpha").getName() + " = " + variable("color4") + ".w;\n");
		glsl = sb.toString();
		return glsl;
	}

}
