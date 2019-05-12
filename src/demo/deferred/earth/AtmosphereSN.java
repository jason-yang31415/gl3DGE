package demo.deferred.earth;

import render.shader.nodes.ColorSNV;
import render.shader.nodes.NodeBasedShader;
import render.shader.nodes.ShaderNode;
import render.shader.nodes.ShaderNodeValue;
import render.shader.nodes.ValueSNV;

public class AtmosphereSN extends ShaderNode {

	public AtmosphereSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());

		init();
	}

	public void init(){
		inputs.put("in_position", nbs.getInputNode().getOutWorldPosition());
		outputs.put("out_color", new ColorSNV(this, "color"));
	}

	public void setInPosition(ValueSNV position){
		inputs.put("in_position", position);
	}

	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + outputs.get("out_color").getName() + " = atmosphere(normalize(" + inputs.get("in_position").getName() + " - "
				+ nbs.getUniforms().get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName()+ "), "
				+ nbs.getUniforms().get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName() + ", "
				+ getLightProperty(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION) + ", "
				+ "10.0, 2, 4, vec3(5.5e-1, 13.0e-1, 22.4e-1), 21e-1, 0.01, 0.002, 0.758);\n");
		sb.append(outputs.get("out_color").getName() + " = 1.0 - exp(-1.0 * max(" + outputs.get("out_color").getName() + ", 0));\n");
		glsl = sb.toString();
		return glsl;
	}

}
