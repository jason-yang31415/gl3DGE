package render.shader;

public class SamplerSN extends ShaderNode {

	public SamplerSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_sampler", null);
		inputs.put("in_texturecoordinate", null);
		outputs.put("out_color", new ColorSNV(this, "color"));
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

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + outputs.get("out_color").getName() + " = texture(" + inputs.get("in_sampler").getName() + ", " + inputs.get("in_texturecoordinate").getName() + ").xyz;\n");
		glsl = sb.toString();
		return glsl;
	}

}
