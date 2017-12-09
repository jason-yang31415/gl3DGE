package render.shader.nodes;

public class GenericSN extends ShaderNode {	

	public GenericSN(NodeBasedShader nbs) {
		super(nbs, nbs.genNodes());
	}

	public GenericSN(NodeBasedShader nbs, String glsl){
		this(nbs);
		this.glsl = glsl;
	}

	public void setGLSL(String glsl){
		this.glsl = glsl;
	}

	@Override
	public String getGLSL() {
		return glsl;
	}

}
