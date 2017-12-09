package render.shader.nodes;

import java.util.ArrayList;

public class ForLoopSN extends ShaderNode {


	ArrayList<ShaderNode> nodes = new ArrayList<ShaderNode>();

	public ForLoopSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());

		init();
	}

	public void init(){
		inputs.put("in_iterator", null);
		inputs.put("in_max", null);
		inputs.put("in_value", null);
		outputs.put("out_value", new ValueSNV(this, "value"));
	}

	public void setInIterator(ValueSNV iterator){
		inputs.put("in_iterator", iterator);
	}

	public void setInMax(ValueSNV max){
		inputs.put("in_max", max);
	}

	public void setInValue(ValueSNV value){
		inputs.put("in_value", value);
	}

	public ValueSNV getOutValue(){
		return (ValueSNV) outputs.get("out_value");
	}

	public void addNode(ShaderNode node){
		nodes.add(node);
	}

	@Override
	public String getGLSL() {
		StringBuilder sb = new StringBuilder();
		sb.append(outputs.get("out_value").getType() + " " + outputs.get("out_value").getName() + " = " + outputs.get("out_value").getType() + "(0);\n");
		sb.append("for (" + inputs.get("in_iterator").getName() + " = 0; " + inputs.get("in_iterator").getName() + " < " + inputs.get("in_max").getName() + "; " + inputs.get("in_iterator").getName() + "++){\n");
		for (ShaderNode node : nodes){
			sb.append(node.getGLSL());
		}
		sb.append(outputs.get("out_value").getName() + " += " + inputs.get("in_value").getName() + ";\n");
		sb.append("}\n");
		glsl = sb.toString();
		return glsl;
	}

}
