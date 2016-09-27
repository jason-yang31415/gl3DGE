package render.text;

import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import render.Drawable;
import render.VertexDataObject;
import render.mesh.Mesh;
import render.mesh.Vertex;
import render.shader.nodes.AlphaTestSN;
import render.shader.nodes.NodeBasedShader;
import render.shader.nodes.SamplerSN;
import render.shader.nodes.SamplerSNV;
import render.shader.nodes.ShaderNodeValue;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;

public class Text {

	private String text;
	private int lineCount;
	
	TrueTypeFont ttf;
	
	NodeBasedShader ts;
	VertexDataObject vdo;
	
	public Text(String text, TrueTypeFont ttf){
		this.ttf = ttf;
		
		this.text = text.replace("\t", "    ");
		int lc = 0;
		Matcher m = Pattern.compile("^.*$", Pattern.MULTILINE).matcher(text);
		while (m.find())
			lc++;
		
		lineCount = lc;
		
		loadShader();
	}
	
	public String getText(){
		return text;
	}
	
	public void loadShader(){
		ts = new NodeBasedShader();
		ts.addInput(ShaderNodeValue.INPUT_POSITION, ts.getInputNode()
				.getOutPosition());
		ts.addInput(ShaderNodeValue.INPUT_TEXTURE_COORDINATE, ts
				.getInputNode().getOutTextureCoordinate());
		
		ts.addSampler(ttf.getTexture());
		
		SamplerSN texSampler2 = new SamplerSN(ts);
		SamplerSNV texSNV2 = new SamplerSNV(null, "texture");
		texSNV2.setSampler(ttf.getTexture());
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
	
	public void draw(){
		try ( MemoryStack stack = stackPush() ) {
			FloatBuffer x = stack.floats(0.0f);
			FloatBuffer y = stack.floats(0.0f);

			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
			
			for ( int i = 0; i < text.length(); i++ ) {
				char c = text.charAt(i);
				if ( c == '\n' ) {
					y.put(0, y.get(0) + 24); //24 = font height
					x.put(0, 0.0f);
					continue;
				} else if ( c < 32 || 128 <= c )
					continue;
				
				stbtt_GetBakedQuad(ttf.getCData(), ttf.getBitmapWidth(), ttf.getBitmapHeight(), c - 32, x, y, q, true);
				
				Mesh mesh = new Mesh();
				ArrayList<Vertex> verts2 = new ArrayList<Vertex>();
				
				verts2.add(new Vertex(new Vector3f(q.x0(), -q.y0(), 0), null, null, new Vector2f(
						q.s0(), q.t0())));
				verts2.add(new Vertex(new Vector3f(q.x1(), -q.y0(), 0), null, null, new Vector2f(
						q.s1(), q.t0())));
				verts2.add(new Vertex(new Vector3f(q.x1(), -q.y1(), 0), null, null, new Vector2f(
						q.s1(), q.t1())));
				verts2.add(new Vertex(new Vector3f(q.x0(), -q.y1(), 0), null, null, new Vector2f(
						q.s0(), q.t1())));
				mesh.loadVertices(verts2);
				
				Integer[] index_array2 = { 3, 1, 0, 3, 2, 1 };
				ArrayList<Integer> indices2 = new ArrayList(Arrays.asList(index_array2));
				mesh.loadIndices(indices2);
				
				if (vdo.getVBO() != null)
					vdo.getVBO().delete();
				vdo.loadVBO(mesh, ts);
				if (vdo.getEBO() != null)
					vdo.getEBO().delete();
				vdo.loadEBO(mesh);
				
				float ratio = 1;
				Matrix4f model = Matrix4f.scale(1 / 960f, 1 / 540f, 1).multiply(Matrix4f.translate(-0.5f, -0.5f, 0));
				Drawable t = new Drawable(ts, vdo);
				ts.setMVP(model, new Matrix4f(), new Matrix4f());
				t.draw();
			}
		}
	}
	
}
