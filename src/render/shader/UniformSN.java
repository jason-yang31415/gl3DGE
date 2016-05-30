package render.shader;

public class UniformSN extends ShaderNode{

	public UniformSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		outputs.put(ShaderNodeValue.UNIFORM_LIGHT_POSITION, new PositionSNV(this, ShaderNodeValue.UNIFORM_LIGHT_POSITION));
	}
	
	public PositionSNV getOutLightPosition(){
		return (PositionSNV) outputs.get(ShaderNodeValue.UNIFORM_LIGHT_POSITION);
	}

	@Override
	public String getGLSL() {
		return "";
	}
	
}
