package render.shader;

public class FastGaussianSN extends ShaderNode {

	public FastGaussianSN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_sampler", null);
		inputs.put("in_texturecoordinate", null);
		inputs.put("in_radius", null);
		inputs.put("in_resolution", null);
		inputs.put("in_direction", null);
		outputs.put("out_color", new ColorSNV(this, "color"));
	}
	
	public void setInSampler(SamplerSNV sampler){
		inputs.put("in_sampler", sampler);
	}
	
	public void setInTextureCoordinate(TextureCoordinateSNV textureCoordinate){
		inputs.put("in_texturecoordinate", textureCoordinate);
	}
	
	public void setInDirection(ValueSNV direction){
		inputs.put("in_direction", direction);
	}
	
	public void setInRadius(ValueSNV radius){
		inputs.put("in_radius", radius);
	}
	
	public void setInResolution(ValueSNV resolution){
		inputs.put("in_resolution", resolution);
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + variable("sum") + " = vec3(0);\n");
		sb.append("float " + variable("blur") + " = " + inputs.get("in_radius").getName() + " / " + inputs.get("in_resolution").getName() + ";\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x - 4 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y - 4 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".y)).xyz * 0.0162162162;\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x - 3 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y - 3 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".y)).xyz * 0.0540540541;\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x - 2 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y - 2 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".y)).xyz * 0.1216216216;\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x - 1 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y - 1 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".y)).xyz * 0.1945945946;\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y)).xyz * 0.2270270270;\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x + 1 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y + 1 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".y)).xyz * 0.1945945946;\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x + 2 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y + 2 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".y)).xyz * 0.1216216216;\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x + 3 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y + 3 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".y)).xyz * 0.0540540541;\n");
		sb.append(variable("sum") + " += texture(" + inputs.get("in_sampler").getName() + ", vec2(" + inputs.get("in_texturecoordinate").getName() + ".x + 4 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".x, " + inputs.get("in_texturecoordinate").getName() + ".y + 4 * " + variable("blur") + " * " + inputs.get("in_direction").getName() + ".y)).xyz * 0.0162162162;\n");
		sb.append("vec3 " + outputs.get("out_color").getName() + " = " + variable("sum") + ";\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
