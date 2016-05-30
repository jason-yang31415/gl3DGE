package render.shader;

public class FloatSNV extends ShaderNodeValue {

	float value;
	
	public FloatSNV(ShaderNode parent, String name) {
		super(parent, name);
		size = 1;
	}
	
	public void setValue(float value){
		this.value = value;
	}

	public String getType() {
		return "float";
	}

	public String getGLSL() {
		return "float " + getName() + " = " + value + ";\n";
	}

	public String getAttribute() {
		return "in_" + getName();
	}
	
	public String getVarying(){
		return getName();
	}
	
	public String getVertexGLSL(){
		return getName() + " = in_" + getName() + ";\n";
	}

}
