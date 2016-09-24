package render.shader.nodes;

import java.util.HashMap;
import java.util.Map;

public class DiffuseSN extends ShaderNode {
	
	public DiffuseSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_color", null);
		inputs.put("in_normal", nbs.getInputNode().getOutNormal());
		outputs.put("out_color", new ColorSNV(this, "color"));
	}
	
	public void setInColor(ColorSNV color){
		inputs.put("in_color", color);
	}
	
	public void setInNormal(NormalSNV normal){
		inputs.put("in_normal", normal);
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}
	
	public String getGLSL(){
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + variable("L") + " = normalize(" + nbs.getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_POSITION).getName() + " - " + nbs.getInputNode().getOutPosition().getName() + ");\n");
		sb.append("float " + variable("cosTheta") + " = clamp(dot(" + inputs.get("in_normal").getName() + ", " + variable("L") + "), 0, 1);\n");
		sb.append("vec3 " + getOutColor().getName() + " = " + inputs.get("in_color").getName() + " * " + variable("cosTheta") + ";\n");
		sb.append(outputs.get("out_color").getName() + " = clamp(" + outputs.get("out_color").getName() + ", 0, 1);\n");
		glsl = sb.toString();
		return glsl;
	}

}
