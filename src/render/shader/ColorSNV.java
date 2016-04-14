package render.shader;

import util.Vector3f;

public class ColorSNV extends ShaderNodeValue {

	Vector3f color;
	
	public ColorSNV(ShaderNode parent, String name) {
		super(parent, name);
	}
	
	public void setColor(Vector3f color){
		this.color = color;
	}
	
	public String getGLSL(){
		return "vec3 " + getName() + " = vec3(" + color.x + ", " + color.y + ", " + color.z + ");\n";
	}

}
