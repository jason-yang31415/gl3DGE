package render.shader;

import util.Vector3f;

public class PositionSNV extends ShaderNodeValue {
	
	Vector3f position;
	
	public PositionSNV(ShaderNode parent, String name) {
		super(parent, name);
		size = 3;
	}
	
	public void setPosition(Vector3f position){
		this.position = position;
	}
	
	public String getType(){
		return "vec3";
	}
	
	public String getGLSL(){
		return "vec3 " + getName() + " = vec3(" + position.x + ", " + position.y + ", " + position.z + ");\n";
	}

	public String getAttribute() {
		return "in_" + getName();
	}
	
	public String getVarying(){
		return getName();
	}
	
	public String getVertexGLSL(){
		return getName() + " = (model * vec4(in_" + getName() + ", 1)).xyz;\n";
	}
}
