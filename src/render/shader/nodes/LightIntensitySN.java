package render.shader.nodes;

public class LightIntensitySN extends ShaderNode {

	public LightIntensitySN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		outputs.put("out_intensity", new ValueSNV(this, "intensity"));
	}
	
	public ValueSNV getOutIntensity(){
		return (ValueSNV) outputs.get("out_intensity");
	}
	
	@Override
	public String getGLSL(){
		StringBuilder sb = new StringBuilder();
		sb.append("float " + outputs.get("out_intensity").getName() + " = ");
		sb.append("1 / pow(length(" + nbs.getInputNode().getOutWorldPosition().getName() + " - ");
		sb.append(NodeBasedShader.getUBO().getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION).getName() + "), 2) * ");
		sb.append(NodeBasedShader.getUBO().getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_UBO_POWER).getName() + ";\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
