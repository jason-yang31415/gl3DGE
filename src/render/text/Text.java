package render.text;

import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import logic.Transform;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import render.Drawable;
import render.mesh.Mesh;
import render.mesh.Vertex;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;

public class Text extends Transform {

	private String text;
	private int lineCount;
	
	TrueTypeFont ttf;
	
	//NodeBasedShader ts;
	//VertexDataObject vdo;
	
	public Text(String text, TrueTypeFont ttf){
		this.ttf = ttf;
		
		this.text = text.replace("\t", "    ");
		int lc = 0;
		Matcher m = Pattern.compile("^.*$", Pattern.MULTILINE).matcher(text);
		while (m.find())
			lc++;
		
		lineCount = lc;
	}
	
	public String getText(){
		return text;
	}
	
	public void draw(){
		try ( MemoryStack stack = stackPush() ) {
			FloatBuffer x = stack.floats(0.0f);
			FloatBuffer y = stack.floats(0.0f);

			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
			
			for ( int i = 0; i < text.length(); i++ ) {
				char c = text.charAt(i);
				if ( c == '\n' ) {
					y.put(0, y.get(0) + ttf.getFontSize());
					x.put(0, 0.0f);
					continue;
				} else if ( c < 32 || 128 <= c )
					continue;
				
				stbtt_GetBakedQuad(ttf.getCData(), ttf.getBitmapSize(), ttf.getBitmapSize(), c - 32, x, y, q, true);
				
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
				
				if (ttf.getVDO().getVBO() != null)
					ttf.getVDO().getVBO().delete();
				ttf.getVDO().loadVBO(mesh, ttf.getShader());
				if (ttf.getVDO().getEBO() != null)
					ttf.getVDO().getEBO().delete();
				ttf.getVDO().loadEBO(mesh);
				
				float ratio = 1;
				//Matrix4f model = new Matrix4f();
				Matrix4f projection = Matrix4f.orthographic(0, 960, 0, 540, -1, 1);
				Drawable t = new Drawable(ttf.getShader(), ttf.getVDO());
				ttf.getShader().setMVP(getMatrix(), new Matrix4f(), projection);
				t.draw();
			}
		}
	}
	
}
