package render.shader;

import util.Vector2f;

public class TextureCoordinateSNV extends ValueSNV {

	//Vector2f textureCoordinate;
	
	public TextureCoordinateSNV(ShaderNode parent, String name) {
		super(parent, name);
		defineAsVector2f();
	}
	
	public void setTextureCoordinate(Vector2f textureCoordinate){
		defineAsVector2f(textureCoordinate);
	}

	/*public String getType(){
		return "vec2";
	}
	
	public String getGLSL(){
		return "vec2 " + getName() + " = vec2(" + textureCoordinate.x + ", " + textureCoordinate.y + ");\n";
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
