package render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import render.RenderTarget.Target;

public class FramebufferObject {

	public static final String FBO_TARGET_TEXTURE = "texture";
	public static final String FBO_TARGET_DEPTH = "depth";

	private final int id;
	private int rbo;

	private int WIDTH, HEIGHT;
	private int location;

	private SamplerMap texture;
	private SamplerMap depth;

	private LinkedHashMap<String, RenderTarget> targets;

	public static FramebufferObject createFramebuffer(int WIDTH, int HEIGHT, int location){
		SamplerMap texture = new SamplerMap(WIDTH, HEIGHT, location);
		texture.texImage2D(GL_TEXTURE_2D, 0, GL_RGB, WIDTH, HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, null);
		texture.setTextureWrap(GL_CLAMP_TO_EDGE);
		SamplerMap depth = new SamplerMap(WIDTH, HEIGHT, location + 1);
		depth.texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, WIDTH, HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
		depth.setTextureWrap(GL_CLAMP_TO_EDGE);
		LinkedHashMap<String, RenderTarget> targets = new LinkedHashMap<String, RenderTarget>();
		targets.put(FBO_TARGET_TEXTURE, new RenderTarget(texture, Target.COLOR));
		targets.put(FBO_TARGET_DEPTH, new RenderTarget(depth, Target.DEPTH));
		return new FramebufferObject(WIDTH, HEIGHT, targets);
	}

	public FramebufferObject(int WIDTH, int HEIGHT, int location){
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.location = location;

		id = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, id);

		//rbo = glGenRenderbuffers();
		//glBindRenderbuffer(GL_RENDERBUFFER, rbo);
		//glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, WIDTH, HEIGHT);

		texture = new SamplerMap(WIDTH, HEIGHT, location);
		texture.texImage2D(GL_TEXTURE_2D, 0, GL_RGB, WIDTH, HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, ByteBuffer.allocate(1024));
		depth = new SamplerMap(WIDTH, HEIGHT, location + 1);
		depth.texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, WIDTH, HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);

		//glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getID(), 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth.getID(), 0);

		GL20.glDrawBuffers(GL_COLOR_ATTACHMENT0);

		int error = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (error != GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException(String.format("%d", error));

		//glBindRenderbuffer(GL_RENDERBUFFER, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public FramebufferObject(int WIDTH, int HEIGHT, LinkedHashMap<String, RenderTarget> targets){
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.targets = targets;

		id = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, id);

		IntBuffer buffer = BufferUtils.createIntBuffer(targets.size());
		Iterator<RenderTarget> iterator = targets.values().iterator();
		int i = 0;
		while (iterator.hasNext()){
			RenderTarget target = iterator.next();
			target.attach(i, buffer);
			i++;
		}

		buffer.flip();
		// FIX
		GL20.glDrawBuffers(buffer);
		//GL20.glDrawBuffers(GL_COLOR_ATTACHMENT0);

		int error = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (error != GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException(String.format("%d", error));

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public LinkedHashMap<String, RenderTarget> getTargets(){
		return targets;
	}

	public void update(int WIDTH, int HEIGHT){
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;

		bind();
		glBindRenderbuffer(GL_RENDERBUFFER, rbo);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, WIDTH, HEIGHT);

		texture.setWidth(WIDTH);
		texture.setHeight(HEIGHT);
		texture.texImage2D(GL_TEXTURE_2D, 0, GL_RGB, WIDTH, HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, ByteBuffer.allocate(1024));
		depth.setWidth(WIDTH);
		depth.setHeight(HEIGHT);
		depth.texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, WIDTH, HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);

		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getID(), 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth.getID(), 0);

		int error = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (error != GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException(String.format("%d", error));

		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		unbind();
	}

	public void bind(){
		glBindFramebuffer(GL_FRAMEBUFFER, id);
	}

	public void unbind(){
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void clear(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void delete(){
		glDeleteRenderbuffers(rbo);
		glDeleteFramebuffers(id);
	}

	public SamplerMap getTexture(){
		return texture;
	}

	public SamplerMap getDepth(){
		return depth;
	}

	public RenderTarget getTarget(String key){
		return targets.get(key);
	}

}
