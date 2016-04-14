package game;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import render.Drawable;
import render.mesh.Material;
import render.mesh.Mesh;
import render.mesh.Vertex;
import render.shader.GameObjectShader;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;

public class HUD extends Drawable {

	public static HUD loadHUD(GameObjectShader goi, float width, float height, String texture_path) throws IOException {
		Mesh mesh = new Mesh();
		
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		Material material = new Material();
		material.setDiffuse(new Vector3f(1, 1, 1));
		material.setSpecular(new Vector3f(0, 0, 0));
		
		verts.add(new Vertex(new Vector3f(width, height, 0), new Vector3f(0, 0, -1), material, new Vector2f(1, 1)));
		verts.add(new Vertex(new Vector3f(0, height, 0), new Vector3f(0, 0, -1), material, new Vector2f(0, 1)));
		verts.add(new Vertex(new Vector3f(0, 0, 0), new Vector3f(0, 0, -1), material, new Vector2f(0, 0)));
		verts.add(new Vertex(new Vector3f(width, 0, 0), new Vector3f(0, 0, -1), material, new Vector2f(1, 0)));
		mesh.loadVertices(verts);
		
		Integer[] index_array = {3, 1, 0, 3, 2, 1};
		ArrayList<Integer> indices = new ArrayList(Arrays.asList(index_array));
		mesh.loadIndices(indices);
		
		goi.loadMeshAttribute("texture", texture_path);
		goi.loadMeshAttribute("emission", texture_path);
		
		goi.loadObjectData(mesh);
		goi.loadShaders();
		
		goi.check();
		
		return new HUD(goi, width, height);
	}
	
	public HUD(GameObjectShader goi, float width, float height) {
		super(goi);
		
		// MVP
		float ratio = 1;
		Matrix4f projection = Matrix4f.orthographic(0, width, 0, height, -1f, 1f);
		goi.setMVP(new Matrix4f(), new Matrix4f(), projection);
	}
	
	public void draw(){
		glDisable(GL_DEPTH_TEST);
		super.draw();
		glEnable(GL_DEPTH_TEST);
	}
	
}
