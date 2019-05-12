package render;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL21;

import io.ImageLoader;

public class SamplerMap {

	public static int TEX_DEFAULT = 0;
	public static int SPEC_DEFAULT = 1;
	public static int NORMAL_DEFAULT = 2;
	public static int EMISSION_DEFAULT = 3;

	public final int id;
	public int location;

	private int width, height;

	public SamplerMap(int width, int height, int location){
		id = glGenTextures();
		this.location = location;

		this.width = width;
		this.height = height;

		setTextureWrap(GL_REPEAT);
		setTextureFilter(GL_LINEAR);
	}

	public void setTextureWrap(int wrap){
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);
		unbind();
	}

	public void setTextureFilter(int filter){
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
		unbind();
	}

	public void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels){
		bind();
		glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
		unbind();
	}

	public void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, long pixels){
		bind();
		glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
		unbind();
	}

	public void bind(){
		bindActiveTexture();
		glBindTexture(GL_TEXTURE_2D, id);
		//unbindActiveTexture();
	}

	public void unbind(){
		bindActiveTexture();
		glBindTexture(GL_TEXTURE_2D, 0);
		//unbindActiveTexture();
	}

	public void bindActiveTexture(){
		glActiveTexture(GL_TEXTURE0 + location);
	}

	public void unbindActiveTexture(){
		glActiveTexture(GL_TEXTURE0);
	}

	public void setWidth(int width){
		this.width = width;
	}

	public int getWidth(){
		return width;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public int getLocation(){
		return location;
	}

	public void delete(){
		glDeleteTextures(id);
	}

	public int getID(){
		return id;
	}

	public static SamplerMap load(String path, int location) {
		return load(path, location, false);
	}

	public static SamplerMap load(String path, int location, boolean srgb){
		BufferedImage image = null;
		try {
			image = ImageLoader.loadImage(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (image == null)
			throw new RuntimeException("Texture screwed up :(");

		int width = image.getWidth();
		int height = image.getHeight();

		ByteBuffer buffer = ImageLoader.loadImageBuffer(image);

		SamplerMap samplerMap = new SamplerMap(width, height, location);

		samplerMap.texImage2D(GL_TEXTURE_2D, 0, srgb ? GL21.GL_SRGB8_ALPHA8 : GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		return samplerMap;
	}

}
