package render.shader.nodes;

import render.SamplerCube;

public class SamplerCubeSNV extends ShaderNodeValue {

	SamplerCube samplerCube;
	
	public SamplerCubeSNV(ShaderNode parent, String name) {
		super(parent, name);
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
	
}