package render;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;

public class BlendFunction {

	int sfactor;
	int dfactor;
	int equation;

	public BlendFunction(){
		sfactor = GL_SRC_ALPHA;
		dfactor = GL_ONE_MINUS_SRC_ALPHA;
	}

	public BlendFunction(int sfactor, int dfactor){
		this.sfactor = sfactor;
		this.dfactor = dfactor;
	}

	public void setBlendFunc(int sfactor, int dfactor){
		this.sfactor = sfactor;
		this.dfactor = dfactor;
		blendFunc();
	}

	public void blendFunc(){
		glBlendFunc(sfactor, dfactor);
	}

}
