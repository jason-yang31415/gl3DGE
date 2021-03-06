package render.shader.nodes;

public class DiffuseSN extends ShaderNode {

	public DiffuseSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());

		init();
	}

	public void init(){
		inputs.put("in_color", null);
		inputs.put("in_normal", nbs.getInputNode().getOutNormal());
		inputs.put("in_position", nbs.getInputNode().getOutWorldPosition());
		outputs.put("out_kdiff", new ValueSNV(this, "kdiff").defineAsFloat());
		outputs.put("out_color", new ColorSNV(this, "color"));
	}

	public void setInColor(ColorSNV color){
		inputs.put("in_color", color);
	}

	public void setInNormal(ValueSNV normal){
		inputs.put("in_normal", normal);
	}

	public void setInPosition(ValueSNV position){
		inputs.put("in_position", position);
	}

	public ValueSNV getOutKdiff(){
		return (ValueSNV) outputs.get("out_kdiff");
	}

	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}

	@Override
	public String getGLSL(){
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + variable("L") + " = normalize("
				+ getLightProperty(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION)
				+ " - " + inputs.get("in_position").getName() + ");\n");
		sb.append("float " + getOutKdiff().getName() + " = clamp(dot(" + inputs.get("in_normal").getName() + ", " + variable("L") + "), 0, 1);\n");
		if (inputs.get("in_color") != null)
			sb.append("vec3 " + getOutColor().getName() + " = " + inputs.get("in_color").getName() + " * " + getOutKdiff().getName() + ";\n");
		//sb.append(outputs.get("out_color").getName() + " = clamp(" + outputs.get("out_color").getName() + ", 0, 1);\n");
		glsl = sb.toString();
		return glsl;
	}

}
