package render.shader;

import render.SamplerMap;

public class SamplerSNV extends ShaderNodeValue {

	SamplerMap sampler;
	
	public SamplerSNV(ShaderNode parent, String name) {
		super(parent, name);
	}
	
	public void setSampler(SamplerMap sampler){
		this.sampler = sampler;
	}
	
	public SamplerMap getSampler(){
		return sampler;
	}

	@Override
	public String getType() {
		return "sampler2D";
	}

	@Override
	public String getGLSL() {
		return null;
	}

	@Override
	public String getAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVarying() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVertexGLSL() {
		// TODO Auto-generated method stub
		return null;
	}

}
