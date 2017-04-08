package render.shader.nodes;

import render.SamplerCube;

public class SamplerCubeSNV extends ShaderNodeValue {

	SamplerCube samplerCube;
	
	public SamplerCubeSNV(ShaderNode parent, String name) {
		super(parent, name);
	}
	
	public SamplerCubeSNV(ShaderNode parent, String name, SamplerCube samplerCube){
		super(parent, name);
		setSamplerCube(samplerCube);
	}
	
	public void setSamplerCube(SamplerCube samplerCube){
		this.samplerCube = samplerCube;
	}
	
	public SamplerCube getSamplerCube(){
		return samplerCube;
	}

	@Override
	public String getType() {
		return "samplerCube";
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
		return 0;
	}
	
}