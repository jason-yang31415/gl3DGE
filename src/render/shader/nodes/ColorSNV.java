package render.shader.nodes;

import util.Vector3f;

public class ColorSNV extends ValueSNV {

	//Vector3f color;
	
	public ColorSNV(ShaderNode parent, String name) {
		super(parent, name);
		defineAsVector3f();
	}
	
	public void setColor(Vector3f color){
		defineAsVector3f(color);
	}
	
	/*public String getType(){
		return "vec3";
	}
	
	public String getGLSL(){
		return "vec3 " + getName() + " = vec3(" + color.x + ", " + color.y + ", " + color.z + ");\n";
	}

	public String getAttribute() {
		return "in_" + getName();
	}
	
	public String getVarying(){
		return getName();
	}
	
	public String getVertexGLSL(){
		return getName() + " = in_" + getName() + ";\n";
	}*/

}
