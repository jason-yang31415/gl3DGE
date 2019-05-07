package render.shader.nodes;

public class LightIntensitySN extends ShaderNode {

	public LightIntensitySN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());

		init();
	}

	public void init(){
		inputs.put("in_position", nbs.getInputNode().getOutWorldPosition());
		outputs.put("out_intensity", new ValueSNV(this, "intensity"));
	}

	public void setInPosition(ValueSNV position){
		inputs.put("in_position", position);
	}

	public ValueSNV getOutIntensity(){
		return (ValueSNV) outputs.get("out_intensity");
	}

	@Override
	public String getGLSL(){
		StringBuilder sb = new StringBuilder();
		sb.append("float " + outputs.get("out_intensity").getName() + " = ");
		sb.append("1 / pow(length(" + inputs.get("in_position").getName() + " - ");
		sb.append(getLightProperty(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION) + "), 2) * ");
		sb.append(getLightProperty(ShaderNodeValue.UNIFORM_LIGHT_UBO_POWER) + ";\n");
		glsl = sb.toString();
		return glsl;
	}

}
