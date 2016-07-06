package render.shader;

import util.Vector2f;
import util.Vector3f;
import util.Vector4f;

public class ValueSNV extends ShaderNodeValue {

	String type;
	
	String definition;
	
	public ValueSNV(ShaderNode parent, String name) {
		super(parent, name);
	}
	
	public void defineAsFloat(){
		type = "float";
		size = 1;
	}
	
	public void defineAsFloat(float value){
		defineAsFloat();
		definition = String.format("%f", value);
	}
	
	public void defineAsVector2f(){
		type = "vec2";
		size = 2;
	}
	
	public void defineAsVector2f(Vector2f value){
		defineAsVector2f();
		definition = String.format("vec2(%f, %f)", value.x, value.y);
	}
	
	public void defineAsVector3f(){
		type = "vec3";
		size = 3;
	}
	
	public void defineAsVector3f(Vector3f value){
		defineAsVector3f();
		definition = String.format("vec3(%f, %f, %f)", value.x, value.y, value.z);
	}
	
	public void defineAsVector4f(){
		type = "vec4";
		size = 4;
	}
	
	public void defineAsVector4f(Vector4f value){
		defineAsVector4f();
		definition = String.format("vec4(%f, %f, %f, %f)", value.x, value.y, value.z, value.w);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getGLSL() {
		return type + " " + getName() + " = " + definition + ";\n";
	}

	@Override
	public String getAttribute() {
		return "in_" + getName();
	}

	@Override
	public String getVarying() {
		return getName();
	}

	@Override
	public String getVertexGLSL() {
		return getName() + " = in_" + getName() + ";\n";
	}
	
	
	
}
