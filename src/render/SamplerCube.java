package render;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL21;

import io.ImageLoader;

public class SamplerCube extends SamplerMap {

	public SamplerCube(int width, int height, int location) {
		super(width, height, location);
	}

	@Override
	public void setTextureWrap(int wrap){
		bind();
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, wrap);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, wrap);
		unbind();
	}

	@Override
	public void setTextureFilter(int filter){
		bind();
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, filter);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, filter);
		unbind();
	}

	@Override
	public void bind(){
		bindActiveTexture();
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
		//unbindActiveTexture();
	}

	@Override
	public void unbind(){
		bindActiveTexture();
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		//unbindActiveTexture();
	}

	public static SamplerCube load(String[] paths, int location){
		return load(paths, location, false);
	}

	public static SamplerCube load(String[] paths, int location, boolean srgb){
		ByteBuffer[] buffers = new ByteBuffer[6];
		int width = 0;
		int height = 0;
		if (paths.length == 6){
			for (int i = 0; i < 6; i++){
				String path = paths[i];
				try {
					BufferedImage image = ImageLoader.loadImage(path);
					width = image.getWidth();
					height = image.getHeight();
					buffers[i] = ImageLoader.loadImageBuffer(image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		SamplerCube samplerCube = new SamplerCube(width, height, location);

		for (int i = 0; i < 6; i++){
			ByteBuffer buffer = buffers[i];
			samplerCube.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, srgb ? GL21.GL_SRGB8_ALPHA8 : GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		}

		return samplerCube;
	}

}
