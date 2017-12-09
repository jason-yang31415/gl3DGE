package render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

public class RenderTarget {

	public enum Target {
		COLOR, DEPTH;
	}

	private SamplerMap sampler;
	private Target target;

	public RenderTarget(SamplerMap sampler, Target target){
		this.sampler = sampler;
		this.target = target;
	}

	public void attach(){
		switch (target){
		case COLOR:
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, sampler.getID(), 0);
			break;
		case DEPTH:
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, sampler.getID(), 0);
		}

	}

	public SamplerMap getSampler(){
		return sampler;
	}

}
