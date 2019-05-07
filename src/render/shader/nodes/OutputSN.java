package render.shader.nodes;

import java.util.LinkedHashMap;
import java.util.Map;

import render.FramebufferObject;
import render.RenderTarget;
import render.RenderTarget.Target;

public class OutputSN extends ShaderNode {

	public static String DEFAULT_OUTPUT = "fragColor";

	protected Map<String, ShaderNodeValue> outputs = new LinkedHashMap<String, ShaderNodeValue>();

	public OutputSN(NodeBasedShader nbs) {
		super(nbs, 0);

		outputs.put(DEFAULT_OUTPUT, null);

		//init();
	}

	public void init(){
		inputs.put("in_color", null);
		inputs.put("in_alpha", null);
	}

	public void initOutputs(FramebufferObject fbo){
		outputs.clear();
		LinkedHashMap<String, RenderTarget> targets = fbo.getTargets();
		for (String s : targets.keySet()){
			if (targets.get(s).getTarget() == Target.COLOR)
				outputs.put(s, null);
		}
	}

	public void setInColor(ValueSNV color){
		setInColor(color, DEFAULT_OUTPUT);
		// inputs.put("in_color", color);
	}

	public void setInAlpha(ValueSNV alpha){
		inputs.put("in_alpha", alpha);
	}

	public void setInColor(ValueSNV color, String output){
		if (outputs.containsKey(output))
			outputs.put(output, color);
		else
			System.err.println("No such output");
	}

	@Override
	public Map<String, ShaderNodeValue> getOutputs(){
		return outputs;
	}

	@Override
	public String getName(){
		return "output";
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		/*if (inputs.get("in_alpha") != null)
			sb.append("fragColor = vec4(" + inputs.get("in_color").getName() + ", " + inputs.get("in_alpha").getName() + ");\n");
		else
			sb.append("fragColor = vec4(" + inputs.get("in_color").getName() + ", 1);\n");*/
		for (String s : outputs.keySet()){
			sb.append(s + " = vec4(" + outputs.get(s).getName() + ", 1);\n");
		}
		//sb.append("fragColor = vec4(1, 1, 1, 1);\n");
		glsl = sb.toString();
		return glsl;
	}

}
