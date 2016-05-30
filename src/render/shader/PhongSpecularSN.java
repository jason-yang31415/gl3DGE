package render.shader;

public class PhongSpecularSN extends ShaderNode {

	public PhongSpecularSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_power", null);
		outputs.put("out_value", new FloatSNV(this, "kspec"));
	}
	
	public void setInPower(FloatSNV power){
		inputs.put("in_power", power);
	}
	
	public FloatSNV getOutValue(){
		return (FloatSNV) outputs.get("out_value");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + variable("L") + " = normalize(" + nbs.getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_POSITION).getName() + " - " + nbs.getInputNode().getOutPosition().getName() + ");\n");
		sb.append("vec3 " + variable("E") + " = normalize(" + nbs.getUniforms().get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName() + " - " + nbs.getInputNode().getOutPosition().getName() + ");\n");
		sb.append("vec3 " + variable("R") + " = normalize(reflect(-" + variable("L") + ", " + nbs.getInputNode().getOutNormal().getName() + "));\n");
		sb.append("float " + variable("dot") + " = clamp(dot(" + variable("E") + ", " + variable("R") + "), 0, 1);\n");
		sb.append("float " + outputs.get("out_value").getName() + " = pow(" + variable("dot") + ", " + inputs.get("in_power").getName() + ");\n");
		glsl = sb.toString();
		return glsl;
	}

}
