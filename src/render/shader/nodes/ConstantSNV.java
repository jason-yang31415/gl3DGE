package render.shader.nodes;

public class ConstantSNV extends ValueSNV {

	private String value;

	public ConstantSNV(float value){
		super(null, "");
		this.value = String.format("%f", value);
	}

	public ConstantSNV(String value){
		super(null, "");
		this.value = value;
	}

	@Override
	public String getName(){
		return value;
	}

}
