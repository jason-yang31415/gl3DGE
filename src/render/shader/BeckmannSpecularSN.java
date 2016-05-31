package render.shader;

public class BeckmannSpecularSN extends ShaderNode {

	public BeckmannSpecularSN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_roughness", null);
		outputs.put("out_kspec", new FloatSNV(this, "kspec"));
	}
	
	public void setInRoughness(FloatSNV roughness){
		inputs.put("in_roughness", roughness);
	}
	
	public FloatSNV getOutKspec(){
		return (FloatSNV) outputs.get("out_kspec");
	}

	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + variable("L") + " = normalize(" + nbs.getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_POSITION).getName() + " - " + nbs.getInputNode().getOutPosition().getName() + ");\n");
		sb.append("vec3 " + variable("E") + " = normalize(" + nbs.getUniforms().get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName() + " - " + nbs.getInputNode().getOutPosition().getName() + ");\n");
		sb.append("vec3 " + variable("H") + " = normalize(" + variable("L") + " + " + variable("E") + ");\n");
		sb.append("float " + variable("cos") + " = dot(" + nbs.getInputNode().getOutNormal().getName() + ", " + variable("H") + ");\n");
		sb.append("float " + variable("e") + " = 2.71828183;\n");
		sb.append("float " + variable("num") + " = pow(" + variable("e") + ", (pow(" + variable("cos") + ", 2) - 1)/(pow(" + variable("cos") + ", 2) * pow(" + inputs.get("in_roughness").getName() + ", 2)));\n");
		sb.append("float " + variable("den") + " = 3.1415927 * pow(" + inputs.get("in_roughness").getName() + ", 2) * pow(" + variable("cos") + ", 4);\n");
		sb.append("float " + outputs.get("out_kspec").getName() + " = " + variable("num") + " / " + variable("den") + ";\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
