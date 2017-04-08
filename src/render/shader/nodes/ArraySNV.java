package render.shader.nodes;

public class ArraySNV extends ShaderNodeValue {

	ShaderNodeValue value;
	final int length;
	
	public ArraySNV(ShaderNode parent, ShaderNodeValue value, int length) {
		super(parent, value.getName());
		this.value = value;
		this.length = length;
	}
	
	public ShaderNodeValue getValue(){
		return value;
	}
	
	public int getLength(){
		return length;
	}

	@Override
	public String getType() {
		return value.getType() + "[" + length + "]";
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

	@Override
	public int getSTD140Alignment() {
		return value.getSTD140Alignment() * length;
	}
	
	@Override
	public int getSize(){
		size = value.getSize() * length;
		return size;
	}

}
