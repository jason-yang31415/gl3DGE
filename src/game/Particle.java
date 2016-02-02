package game;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import render.Camera;
import render.DefaultGameObjectInit;
import render.Drawable;
import render.GameObjectInit;
import render.Scene;
import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class Particle extends Drawable {

	public static Particle loadParticle(DefaultGameObjectInit dgoi, Vector3f color) throws IOException{
		// WORKS ONLY WITH DEFAULT SHADER
		float[] verts = {1, 1, 0,	 0, 0, -1,	color.x, color.y, color.z,	0, 0, 0,	1, 1,
						 -1, 1, 0,	 0, 0, -1,	color.x, color.y, color.z,	0, 0, 0,	-1, 1,
						 -1, -1, 0,	 0, 0, -1,	color.x, color.y, color.z,	0, 0, 0,	-1, -1,
						 1, -1, 0,	 0, 0, -1,	color.x, color.y, color.z,	0, 0, 0,	1, -1
						};
		int[] index = {3, 1, 0, 3, 2, 1};
		
		FloatBuffer vertices = BufferUtils.createFloatBuffer(verts.length);
		vertices.put(verts).flip();
		
		dgoi.loadVertices(vertices);
		dgoi.loadIndices(index);
		
		dgoi.loadShaders();
		
		dgoi.check();
		
		return new Particle(dgoi);
	}
	
	public Particle(GameObjectInit goi){
		super(goi);
		
		// MVP
		float ratio = 1;
		Matrix4f projection = Matrix4f.perspective(90, ratio, 0.01f, 100);
		goi.setMVP(new Matrix4f(), new Matrix4f(), projection);
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
