package render.shader;

public class MathSN extends ShaderNode {
	
	public static enum Operation {
		ADD,
		SUBTRACT,
		MULTIPLY,
		DIVIDE
	}
	
	public Operation op;
	
	public MathSN(NodeBasedShader nbs){
		super(nbs, nbs.genNodes());
		
		init();
	}
	
	public MathSN(NodeBasedShader nbs, Operation op){
		super(nbs, nbs.genNodes());
		
		init();
		setOperation(op);
	}
	
	public void init(){
		inputs.put("in_value1", null);
		inputs.put("in_value2", null);
		outputs.put("out_value", new ValueSNV(this, "out"));
	}
	
	public void setOperation(Operation op){
		this.op = op;
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
		sb.append(((ValueSNV) outputs.get("out_value")).getType() + " " + outputs.get("out_value").getName() + " = ");
		sb.append(inputs.get("in_value1").getName());
		switch (op){
		case ADD:
			sb.append(" + ");
			break;
		case SUBTRACT:
			sb.append(" - ");
			break;
		case MULTIPLY:
			sb.append(" * ");
			break;
		case DIVIDE:
			sb.append(" / ");
			break;
		default:
			throw new RuntimeException("Math node operation is undefined");
		}
		sb.append(inputs.get("in_value2").getName() + ";\n");
		glsl = sb.toString();
		return glsl;
	}
	
}
