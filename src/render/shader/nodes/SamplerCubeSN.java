package render.shader.nodes;

public class SamplerCubeSN extends ShaderNode {

	public SamplerCubeSN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());
		
		init();
	}

	public void init(){
		inputs.put("in_samplercube", null);
		inputs.put("in_vector", null);
		outputs.put("out_color", new ColorSNV(this, "color"));
		outputs.put("out_alpha", new ColorSNV(this, "alpha"));
	}
	
	public void setInSamplerCube(SamplerCubeSNV samplerCube){
		inputs.put("in_samplercube", samplerCube);
	}
	
	public void setInVector(ValueSNV vector){
		inputs.put("in_vector", vector);
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
		sb.append("vec4 " + variable("color4") + " = texture(" + inputs.get("in_samplercube").getName() + ", " + inputs.get("in_vector").getName() + ");\n");
		sb.append("vec3 " + outputs.get("out_color").getName() + " = " + variable("color4") + ".xyz;\n");
		sb.append("float " + outputs.get("out_alpha").getName() + " = " + variable("color4") + ".w;\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
