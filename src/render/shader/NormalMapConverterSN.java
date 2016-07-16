package render.shader;

public class NormalMapConverterSN extends ShaderNode {

	public NormalMapConverterSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_sampler", null);
		inputs.put("in_texturecoordinate", null);
		outputs.put("out_normal", new NormalSNV(this, "normal"));
	}
	
	public void setInSampler(SamplerSNV sampler){
		inputs.put("in_sampler", sampler);
	}
	
	public void setInTextureCoordinate(TextureCoordinateSNV textureCoordinate){
		inputs.put("in_texturecoordinate", textureCoordinate);
	}
	
	public NormalSNV getOutNormal(){
		return (NormalSNV) outputs.get("out_normal");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + outputs.get("out_normal").getName() + " = mat3(model) * (texture(" + inputs.get("in_sampler").getName() + ", " + inputs.get("in_texturecoordinate").getName() + ").xyz * 2 - 1);\n");
		glsl = sb.toString();
		return glsl;
	}

}
