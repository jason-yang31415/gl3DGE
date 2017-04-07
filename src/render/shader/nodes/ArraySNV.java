package render.shader.nodes;

public class ArraySNV extends ShaderNodeValue {

	ShaderNodeValue value;
	
	public ArraySNV(ShaderNode parent, ShaderNodeValue value) {
		super(parent, value.getName());
		this.value = value;
	}
	
	public ShaderNodeValue getValue(){
		return value;
	}

	@Override
	public String getType() {
		return value.getType() + "[16]";
	}

	@Override
	public String getGLSL() {
		return null;
	}

	@Override
	public String getAttribute() {
		return null;
	}

	@Override
	public String getVarying() {
		return null;
	}

	@Override
	public String getVertexGLSL() {
		return null;
	}

}
