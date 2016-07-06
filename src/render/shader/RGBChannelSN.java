package render.shader;

public class RGBChannelSN extends ShaderNode {

	public static enum Channel {
		RED,
		GREEN,
		BLUE
	}
	
	public Channel channel;
	
	public RGBChannelSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_color", null);
		outputs.put("out_value", new ValueSNV(this, "value"));
	}
	
	public void setInColor(ColorSNV color){
		inputs.put("in_color", color);
	}
	
	public void setChannel(Channel channel){
		this.channel = channel;
	}
	
	public ValueSNV getOutValue(){
		return (ValueSNV) outputs.get("out_value");
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("float " + outputs.get("out_value").getName() + " = " + inputs.get("in_color").getName());
		switch (channel){
		case RED:
			sb.append(".x");
			break;
		case BLUE:
			sb.append(".y");
			break;
		case GREEN:
			sb.append(".z");
			break;
		}
		sb.append(";\n");
		glsl = sb.toString();
		return glsl;
	}

}
