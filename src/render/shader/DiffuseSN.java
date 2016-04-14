package render.shader;

import java.util.HashMap;
import java.util.Map;

public class DiffuseSN extends ShaderNode {
	
	public DiffuseSN(int id) {
		super(id);
		
		init();
	}
	
	public void init(){
		inputs.put("in_color", null);
		outputs.put("out_color", new ColorSNV(this, "color"));
	}
	
	public void setInColor(ColorSNV color){
		inputs.put("in_color", color);
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get("out_color");
	}
	
	public String getGLSL(){
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + variable("L") + " = normalize(global_lightPos - global_position);\n");
		sb.append("float " + variable("cosTheta") + " = clamp(dot(global_normal, " + variable("L") + "), 0, 1);\n");
		sb.append("vec3 " + getOutColor().getName() + " = " + inputs.get("in_color").getName() + " * " + variable("cosTheta") + ";\n");
		sb.append(outputs.get("out_color").getName() + " = clamp(" + outputs.get("out_color").getName() + ", 0, 1);\n");
		glsl = sb.toString();
		return glsl;
	}

}
