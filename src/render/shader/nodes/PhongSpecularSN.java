package render.shader.nodes;

public class PhongSpecularSN extends ShaderNode {

	public PhongSpecularSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());

		init();
	}

	public void init(){
		inputs.put("in_power", null);
		inputs.put("in_normal", nbs.getInputNode().getOutNormal());
		inputs.put("in_position", nbs.getInputNode().getOutWorldPosition());
		outputs.put("out_kspec", new ValueSNV(this, "kspec"));
	}

	public void setInPower(ValueSNV power){
		inputs.put("in_power", power);
	}

	public void setInNormal(ValueSNV normal){
		inputs.put("in_normal", normal);
	}

	public void setInPosition(ValueSNV position){
		inputs.put("in_position", position);
	}

	public ValueSNV getOutKspec(){
		return (ValueSNV) outputs.get("out_kspec");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + variable("L") + " = normalize(" + getLightProperty(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION) + " - " + inputs.get("in_position").getName() + ");\n");
		sb.append("vec3 " + variable("E") + " = normalize(" + nbs.getUniforms().get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName() + " - " + inputs.get("in_position").getName() + ");\n");
		sb.append("vec3 " + variable("R") + " = normalize(reflect(-" + variable("L") + ", " + inputs.get("in_normal").getName() + "));\n");
		sb.append("float " + variable("dot") + " = clamp(dot(" + variable("E") + ", " + variable("R") + "), 0, 1);\n");
		sb.append("float " + outputs.get("out_kspec").getName() + " = pow(" + variable("dot") + ", " + inputs.get("in_power").getName() + ");\n");
		glsl = sb.toString();
		return glsl;
	}

}
