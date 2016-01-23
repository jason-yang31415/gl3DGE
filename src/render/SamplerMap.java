package render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import io.ImageLoader;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public abstract class SamplerMap {

	public static int TEX_DEFAULT = 0;
	public static int SPEC_DEFAULT = 1;
	public static int BUMP_DEFAULT = 2;
	public static int EMISSION_DEFAULT = 3;
	
	public final int id;
	
	private int width, height;
	
	public SamplerMap(int width, int height, ByteBuffer image){
		id = glGenTextures();
		
		this.width = width;
		this.height = height;
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
	
	public abstract void bindActiveTexture();
	
	public void unbindActiveTexture(){
		glActiveTexture(GL_TEXTURE0);
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void delete(){
		glDeleteTextures(id);
	}
	
	public static ByteBuffer loadImage(String path){
		BufferedImage image = null;
		try {
			image = ImageLoader.loadImage(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (image == null)
			throw new RuntimeException("Texture screwed up :(");
		
		AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
		transform.translate(0, -image.getHeight());
		AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = operation.filter(image, null);
		
		int width = image.getWidth();
		int height = image.getHeight();

		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y * width + x];
				
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();
		
		return buffer;
	}
	
	public static SamplerMap load(String path){
		return null;
	}
	
}
