package render.shader.nodes;

import util.Vector2f;
import util.Vector3f;
import util.Vector4f;

public class ValueSNV extends ShaderNodeValue {

	String type;
	
	String definition;
	
	public ValueSNV(ShaderNode parent, String name) {
		super(parent, name);
	}
	
	public ValueSNV defineAsFloat(){
		type = "float";
		size = 1;
		return this;
	}
	
	public ValueSNV defineAsFloat(float value){
		defineAsFloat();
		definition = String.format("%f", value);
		return this;
	}
	
	public ValueSNV defineAsVector2f(){
		type = "vec2";
		size = 2;
		return this;
	}
	
	public ValueSNV defineAsVector2f(Vector2f value){
		defineAsVector2f();
		definition = String.format("vec2(%f, %f)", value.x, value.y);
		return this;
	}
	
	public ValueSNV defineAsVector3f(){
		type = "vec3";
		size = 3;
		return this;
	}
	
	public ValueSNV defineAsVector3f(Vector3f value){
		defineAsVector3f();
		definition = String.format("vec3(%f, %f, %f)", value.x, value.y, value.z);
		return this;
	}
	
	public ValueSNV defineAsVector4f(){
		type = "vec4";
		size = 4;
		return this;
	}
	
	public ValueSNV defineAsVector4f(Vector4f value){
		defineAsVector4f();
		definition = String.format("vec4(%f, %f, %f, %f)", value.x, value.y, value.z, value.w);
		return this;
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

	@Override
	public int getSTD140Alignment() {
		switch (size){
		case 1:
			return 1;
		case 2:
			return 2;
		case 3:
			return 4;
		case 4:
			return 4;
		default:
			return 0;
		}
	}
	
}
