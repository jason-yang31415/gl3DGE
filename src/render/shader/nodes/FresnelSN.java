package render.shader.nodes;

public class FresnelSN extends ShaderNode {

	public FresnelSN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_ior", null);
		inputs.put("in_normal", nbs.getInputNode().getOutNormal());
		outputs.put("out_R", new ValueSNV(this, "R"));
	}
	
	public void setInIOR(ValueSNV ior){
		inputs.put("in_ior", ior);
	}
	
	public void setInNormal(NormalSNV normal){
		inputs.put("in_normal", normal);
	}
	
	public ValueSNV getOutR(){
		return (ValueSNV) outputs.get("out_R");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("vec3 " + variable("E") + " = normalize(" + nbs.getUniforms().get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName() + " - " + nbs.getInputNode().getOutPosition().getName() + ");\n");
		sb.append("float " + variable("cos") + " = dot(normalize(" + inputs.get("in_normal").getName() + "), " + variable("E") + ");\n");
		sb.append("float " + variable("r0") + " = pow((1 - " + inputs.get("in_ior").getName() + ") / (1 + " + inputs.get("in_ior").getName() + "), 2);\n");
		sb.append("float " + outputs.get("out_R").getName() + " = " + variable("r0") + " + (1 - " + variable("r0") + ") * pow(1 - " + variable("cos") + ", 5);\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
