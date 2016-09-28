package render.text;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import io.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;

import render.SamplerMap;
import render.VertexDataObject;
import render.shader.nodes.AlphaTestSN;
import render.shader.nodes.NodeBasedShader;
import render.shader.nodes.SamplerSN;
import render.shader.nodes.SamplerSNV;
import render.shader.nodes.ShaderNodeValue;

public class TrueTypeFont {
	
	private int BITMAP_SIZE = 512;
	
	private STBTTBakedChar.Buffer cdata;
	
	private SamplerMap tex;
	private int fontSize;
	
	private NodeBasedShader ts;
	private VertexDataObject vdo;

	public TrueTypeFont(String path, int fontSize){
		this.fontSize = fontSize;
		
		cdata = STBTTBakedChar.malloc(96);
		try {
			ByteBuffer ttf = IOUtil.ioResourceToByteBuffer(path, 160 * 1024);
			
			ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_SIZE * BITMAP_SIZE);
			stbtt_BakeFontBitmap(ttf, fontSize, bitmap, BITMAP_SIZE, BITMAP_SIZE, 32, cdata);
			
			tex = new SamplerMap(BITMAP_SIZE, BITMAP_SIZE, SamplerMap.TEX_DEFAULT);
			tex.texImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_SIZE, BITMAP_SIZE, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		loadShader();
	}
	
	public void loadShader(){
		ts = new NodeBasedShader();
		ts.addInput(ShaderNodeValue.INPUT_POSITION, ts.getInputNode()
				.getOutPosition());
		ts.addInput(ShaderNodeValue.INPUT_TEXTURE_COORDINATE, ts
				.getInputNode().getOutTextureCoordinate());
		
		ts.addSampler(getTexture());
		
		SamplerSN texSampler2 = new SamplerSN(ts);
		SamplerSNV texSNV2 = new SamplerSNV(null, "texture");
		texSNV2.setSampler(getTexture());
		ts.addUniform("texture", texSNV2);
		texSampler2.setInSampler(texSNV2);
		texSampler2.setInTextureCoordinate(ts.getInputNode()
				.getOutTextureCoordinate());
		ts.addNode(texSampler2);
		
		AlphaTestSN at = new AlphaTestSN(ts);
		at.setInValue(texSampler2.getOutColor4f());
		ts.addNode(at);
		
		/*ValueSNV color = new ValueSNV(null, "color");
		color.defineAsVector3f(new Vector3f(1, 1, 1));
		ts.addConstant(color);*/
		ts.getOutputNode().setInValue(texSampler2.getOutColor4f());
		
		try {
			ts.loadShaders();
			ts.check();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ts.init();
		vdo = new VertexDataObject();
		vdo.loadVAO();
	}
	
	public VertexDataObject getVDO(){
		return vdo;
	}
	
	public NodeBasedShader getShader(){
		return ts;
	}
	
	public STBTTBakedChar.Buffer getCData(){
		return cdata;
	}
	
	public int getBitmapSize(){
		return BITMAP_SIZE;
	}
	
	public int getFontSize(){
		return fontSize;
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
