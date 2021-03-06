package render.shader.nodes;

public class BeckmannSpecularSN extends ShaderNode {

	public BeckmannSpecularSN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());

		init();
	}

	public void init(){
		inputs.put("in_roughness", null);
		inputs.put("in_normal", nbs.getInputNode().getOutNormal());
		inputs.put("in_position", nbs.getInputNode().getOutWorldPosition());
		outputs.put("out_kspec", new ValueSNV(this, "kspec"));
	}

	public void setInRoughness(ValueSNV roughness){
		inputs.put("in_roughness", roughness);
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
		sb.append("vec3 " + variable("L") + " = normalize(");
		sb.append(getLightProperty(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION));
		sb.append(" - " + inputs.get("in_position").getName() + ");\n");
		sb.append("vec3 " + variable("E") + " = normalize(" + nbs.getUniforms().get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName() + " - " + inputs.get("in_position").getName() + ");\n");
		sb.append("vec3 " + variable("H") + " = normalize(" + variable("L") + " + " + variable("E") + ");\n");
		sb.append("float " + variable("cos") + " = dot(normalize(" + inputs.get("in_normal").getName() + "), " + variable("H") + ");\n");
		sb.append("float " + variable("e") + " = 2.71828183;\n");
		sb.append("float " + variable("num") + " = pow(" + variable("e") + ", (pow(" + variable("cos") + ", 2) - 1)/(pow(" + variable("cos") + ", 2) * pow(" + inputs.get("in_roughness").getName() + ", 2)));\n");
		sb.append("float " + variable("den") + " = 3.1415927 * pow(" + inputs.get("in_roughness").getName() + ", 2) * pow(" + variable("cos") + ", 4);\n");
		sb.append("float " + outputs.get("out_kspec").getName() + " = " + variable("num") + " / " + variable("den") + ";\n");
		glsl = sb.toString();
		return glsl;
	}

}
