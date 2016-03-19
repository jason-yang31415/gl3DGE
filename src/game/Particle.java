package game;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import render.Camera;
import render.Drawable;
import render.GameObjectInit;
import render.Scene;
import render.mesh.Material;
import render.mesh.Mesh;
import render.mesh.Vertex;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;
import util.Vector4f;

public class Particle extends Drawable {

	public static Particle loadParticle(GameObjectInit goi, Vector3f color) throws IOException{
		Mesh mesh = new Mesh();
		
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		Material material = new Material();
		material.setDiffuse(color);
		material.setSpecular(new Vector3f(0, 0, 0));
		
		verts.add(new Vertex(new Vector3f(1, 1, 0), new Vector3f(0, 0, -1), material, new Vector2f(1, 1)));
		verts.add(new Vertex(new Vector3f(-1, 1, 0), new Vector3f(0, 0, -1), material, new Vector2f(-1, 1)));
		verts.add(new Vertex(new Vector3f(-1, -1, 0), new Vector3f(0, 0, -1), material, new Vector2f(-1, -1)));
		verts.add(new Vertex(new Vector3f(1, -1, 0), new Vector3f(0, 0, -1), material, new Vector2f(1, -1)));
		mesh.loadVertices(verts);
		
		Integer[] index_array = {3, 1, 0, 3, 2, 1};
		ArrayList<Integer> indices = new ArrayList(Arrays.asList(index_array));
		mesh.loadIndices(indices);
		
		goi.loadObjectData(mesh);
		goi.loadShaders();
		
		goi.check();
		
		return new Particle(goi);
	}
	
	public Particle(GameObjectInit goi){
		super(goi);
		
		// MVP
		float ratio = 1;
		Matrix4f projection = Matrix4f.perspective(90, ratio, 0.01f, 100);
		goi.setMVP(new Matrix4f(), new Matrix4f(), projection);
	}
	
	@Override
	public void setBlendFunc(){
		glBlendFunc(GL_ONE, GL_ONE);
	}
	
	@Override
	public void update(Scene scene){
		super.update(scene);
		Camera cam = scene.getCamera();
		Matrix4f view = cam.getMatrix();
		
		//Vector3f camera_right = new Vector3f(view.m00, view.m10, view.m20);
		Vector3f camera_up = new Vector3f(view.m01, view.m11, view.m21);
		Vector3f camera_forward = getPos().subtract(cam.getPos()).normalize();
		Vector3f camera_right = camera_up.cross(camera_forward);
		rotate = new Matrix4f(
			new Vector4f(camera_right.x, camera_right.y, camera_right.z, 0),
			new Vector4f(camera_up.x, camera_up.y, camera_up.z, 0),
			new Vector4f(camera_forward.x, camera_forward.y, camera_forward.z, 0),
			new Vector4f(0, 0, 0, 1)
		);
	}
	
}
