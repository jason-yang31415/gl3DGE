package render.shader.nodes;

public class SamplerSN extends ShaderNode {

	public SamplerSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_sampler", null);
		inputs.put("in_texturecoordinate", null);
		outputs.put("out_color4", new ColorSNV(this, "color4"));
		outputs.put("out_color3", new ColorSNV(this, "color3"));
	}
	
	public void setInSampler(SamplerSNV sampler){
		inputs.put("in_sampler", sampler);
	}
	
	public void setInTextureCoordinate(TextureCoordinateSNV textureCoordinate){
		inputs.put("in_texturecoordinate", textureCoordinate);
	}
	
	public ColorSNV getOutColor4f(){
		return (ColorSNV) outputs.get("out_color4");
	}
	
	public ColorSNV getOutColor3f(){
		return (ColorSNV) outputs.get("out_color3");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec4 " + outputs.get("out_color4").getName() + " = texture(" + inputs.get("in_sampler").getName() + ", " + inputs.get("in_texturecoordinate").getName() + ");\n");
		sb.append("vec3 " + outputs.get("out_color3").getName() + " = texture(" + inputs.get("in_sampler").getName() + ", " + inputs.get("in_texturecoordinate").getName() + ").xyz;\n");
		glsl = sb.toString();
		return glsl;
	}

}
