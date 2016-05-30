package render.shader;

public class InputSN extends ShaderNode{

	public InputSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		outputs.put(ShaderNodeValue.INPUT_POSITION, new PositionSNV(this, ShaderNodeValue.INPUT_POSITION));
		outputs.put(ShaderNodeValue.INPUT_NORMAL, new NormalSNV(this, ShaderNodeValue.INPUT_NORMAL));
		outputs.put(ShaderNodeValue.INPUT_COLOR, new ColorSNV(this, ShaderNodeValue.INPUT_COLOR));
	}
	
	public PositionSNV getOutPosition(){
		return (PositionSNV) outputs.get(ShaderNodeValue.INPUT_POSITION);
	}
	
	public NormalSNV getOutNormal(){
		return (NormalSNV) outputs.get(ShaderNodeValue.INPUT_NORMAL);
	}
	
	public ColorSNV getOutColor(){
		return (ColorSNV) outputs.get(ShaderNodeValue.INPUT_COLOR);
	}

	@Override
	public String getGLSL() {
		return "";
	}
	
}
