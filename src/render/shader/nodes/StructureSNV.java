package render.shader.nodes;

public class StructureSNV extends ShaderNodeValue {

	String name;
	Structure struct;
	
	public StructureSNV(ShaderNode parent, String name, Structure struct) {
		super(parent, name);
		this.struct = struct;
	}
	
	public Structure getStruct(){
		return struct;
	}

	@Override
	public String getType() {
		return struct.getName();
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
