package render.shader;

import util.Vector3f;

public class NormalSNV extends ValueSNV {

	//Vector3f normal;
	
	public NormalSNV(ShaderNode parent, String name) {
		super(parent, name);
		defineAsVector3f();
	}
	
	public void setNormal(Vector3f normal){
		defineAsVector3f(normal);
	}
	
	/*public String getType(){
		return "vec3";
	}
	
	public String getGLSL(){
		return "vec3 " + getName() + " = vec3(" + normal.x + ", " + normal.y + ", " + normal.z + ");\n";
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
