package render.shader.nodes;

import java.util.ArrayList;
import java.util.Collections;

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

	@Override
	public int getSTD140Alignment() {
		ArrayList<Integer> sizes = new ArrayList<Integer>();
		for (ShaderNodeValue snv : getStruct().getValues().values())
			sizes.add(snv.getSTD140Alignment());
		int alignment = Collections.max(sizes);
		int extra = alignment % 4;
		int num = (extra == 0) ? 0 : 4 - extra;
		return alignment + num;
	}
	
	@Override
	public int getSize(){
		size = 0;
		for (ShaderNodeValue snv : getStruct().getValues().values())
			size += snv.getSize();
		return size;
	}
	
}
