package render.text;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import io.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;

import render.SamplerMap;

public class TrueTypeFont {
	
	private int BITMAP_W = 512;
	private int BITMAP_H = 512;
	
	private STBTTBakedChar.Buffer cdata;
	
	private SamplerMap tex;

	public TrueTypeFont(String path){
		int id = glGenTextures();
		cdata = STBTTBakedChar.malloc(96);
		
		try {
			ByteBuffer ttf = IOUtil.ioResourceToByteBuffer(path, 160 * 1024);
			
			ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
			stbtt_BakeFontBitmap(ttf, 24, bitmap, BITMAP_W, BITMAP_H, 32, cdata); //change values, 24 = font height
			
			tex = new SamplerMap(BITMAP_W, BITMAP_H, SamplerMap.TEX_DEFAULT);
			tex.texImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public STBTTBakedChar.Buffer getCData(){
		return cdata;
	}
	
	public int getBitmapWidth(){
		return BITMAP_W;
	}
	
	public int getBitmapHeight(){
		return BITMAP_H;
	}
	
	public SamplerMap getTexture(){
		return tex;
	}
	
	public ByteArrayOutputStream readInputStream(InputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] byteArray = new byte[1024];
		int read = 0;
		while ((read = is.read(byteArray, 0, byteArray.length)) != -1) {
			baos.write(byteArray, 0, read);
		}
		baos.flush();
		return baos;
	}
	
}
