package render.shader.nodes;

public class DepthOfFieldSN extends ShaderNode {

	public DepthOfFieldSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public void init(){
		inputs.put("in_depth", null);
		inputs.put("in_targetdistance", null);
		inputs.put("in_maxradius", null);
		inputs.put("in_factor", null);
		outputs.put("out_radius", new ValueSNV(this, "radius"));
	}
	
	public void setInDepth(ValueSNV depth){
		inputs.put("in_depth", depth);
	}

	public void setInTargetDistance(ValueSNV targetDistance){
		inputs.put("in_targetdistance", targetDistance);
	}
	
	public void setInMaxRadius(ValueSNV maxRadius){
		inputs.put("in_maxradius", maxRadius);
	}
	
	public void setInFactor(ValueSNV factor){
		inputs.put("in_factor", factor);
	}
	
	public ValueSNV getOutRadius(){
		return (ValueSNV) outputs.get("out_radius");
	}
	
	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append("float " + outputs.get("out_radius").getName() + " = min(abs(" + inputs.get("in_depth").getName() + " - " + inputs.get("in_targetdistance").getName() + ") * " + inputs.get("in_factor").getName() + ", " + inputs.get("in_maxradius").getName() + ");\n");
		glsl = sb.toString();
		return glsl;
	}

}
