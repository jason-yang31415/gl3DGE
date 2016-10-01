package render.shader.nodes;

public class MixSN extends ShaderNode {
	
	public static enum Blend {
		MIX,
		SCREEN
	}
	public Blend blend;
	
	public MixSN(NodeBasedShader nbs){
		this(nbs, Blend.MIX);
	}
	
	public MixSN(NodeBasedShader nbs, Blend blend){
		super(nbs, nbs.genNodes());
		
		init();
		setBlend(blend);
	}
	
	public void init(){
		inputs.put("in_factor", null);
		inputs.put("in_value1", null);
		inputs.put("in_value2", null);
		outputs.put("out_value", new ValueSNV(this, "out"));
	}
	
	public void setBlend(Blend blend){
		this.blend = blend;
	}
	
	public void setInFactor(ValueSNV factor){
		inputs.put("in_factor", factor);
	}
	
	public void setInValue1(ValueSNV value1){
		inputs.put("in_value1", value1);
	}
	
	public void setInValue2(ValueSNV value2){
		inputs.put("in_value2", value2);
	}
	
	public ValueSNV getOutValue(){
		return (ValueSNV) outputs.get("out_value");
	}
	
	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		switch (blend){
		case MIX:
			sb.append(((ValueSNV) outputs.get("out_value")).getType() + " " + outputs.get("out_value").getName() + " = (1 - " + inputs.get("in_factor").getName() + ") * clamp(" + inputs.get("in_value1").getName() + ", 0, 1)");
			sb.append(" + ");
			sb.append(inputs.get("in_factor").getName() + " * clamp(" + inputs.get("in_value2").getName() + ", 0, 1);\n");
			break;
		case SCREEN:
			sb.append(((ValueSNV) outputs.get("out_value")).getType() + " " + outputs.get("out_value").getName() + " = 1 - (1 - " + inputs.get("in_value1").getName() + ") * (1 - " + inputs.get("in_value2").getName() + ");\n");
			break;
		}
		
		glsl = sb.toString();
		return glsl;
	}
	
}
