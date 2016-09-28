package game;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import render.Camera;
import render.Drawable;
import render.Scene;
import render.VertexDataObject;
import render.mesh.Material;
import render.mesh.Mesh;
import render.mesh.Vertex;
import render.shader.ObjectShader;
import render.shader.ParticleShader;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;
import util.Vector4f;

public class ParticleSystem extends Drawable {
	
	Particle[] particles;
	int maxParticles;
	int particleCount = 0;
	
	int lastParticle = 0;
	
	int counter = 0; // USE DELTA
	int particleFrequency = 2;
	
	public static ParticleSystem loadParticleSystem(ObjectShader ps, Vector3f color, int maxParticles)
			throws IOException {
		
		Mesh mesh = new Mesh();
		
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		Material material = new Material();
		material.setDiffuse(color);
		material.setSpecular(new Vector3f(0, 0, 0));
		
		verts.add(new Vertex(new Vector3f(-1, -1, 0), null, material, new Vector2f(1, 1)));
		verts.add(new Vertex(new Vector3f(1, -1, 0), null, material, new Vector2f(-1, 1)));
		verts.add(new Vertex(new Vector3f(1, 1, 0), null, material, new Vector2f(-1, -1)));
		verts.add(new Vertex(new Vector3f(-1, 1, 0), null, material, new Vector2f(1, -1)));
		mesh.loadVertices(verts);
		
		Integer[] index_array = {3, 1, 0, 3, 2, 1};
		ArrayList<Integer> indices = new ArrayList(Arrays.asList(index_array));
		mesh.loadIndices(indices);
		
		//ps.loadObjectData(mesh);
		ps.loadShaders();
		ps.check();
		ps.init();
		
		VertexDataObject vdo = new VertexDataObject();
		vdo.loadVertexData(mesh, ps);
		
		return new ParticleSystem(ps, vdo, maxParticles);
	}
	
	public ParticleSystem(ObjectShader ps, VertexDataObject vdo, int maxParticles){
		super(ps, vdo);
		
		this.maxParticles = maxParticles;
		
		particles = new Particle[maxParticles];
		
		// MVP
		float ratio = 1;
		Matrix4f projection = Matrix4f.perspective(90, ratio, 0.01f, 100);
		ps.setMVP(new Matrix4f(), new Matrix4f(), projection);
	}
	
	public void create(){
		int index = getDeadParticle();
		
		Random rand = new Random();
		Vector3f position = new Vector3f(0, 0, 0); // TEMP
		Vector3f velocity = new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()).subtract(new Vector3f(0.5f, 0.5f, 0.5f)).scale(0.2f);
		Vector4f color = new Vector4f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1);
		float lifespan = 100;
		float size = rand.nextFloat() / 4 + 0.25f;
		
		Particle particle = new Particle(os, position, velocity, color, size, lifespan);
		particles[index] = particle;
		
		if (particleCount < maxParticles)
			particleCount++;
	}
	
	public int getDeadParticle(){
		for (int i = lastParticle; i < maxParticles; i++){
			if (particles[i] != null){
				if (particles[i].isDead()){
					lastParticle = i;
					return i;
				}
			}
			else {
				lastParticle = i;
				return i;
			}
		}
		
		for (int i = 0; i < lastParticle; i++){
			if (particles[i] != null){
				if (particles[i].isDead()){
					lastParticle = i;
					return i;
				}
			}
			else {
				lastParticle = i;
				return i;
			}
		}
		
		return 0;
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
		if (!Float.isNaN(camera_forward.x) && !Float.isNaN(camera_forward.y) && !Float.isNaN(camera_forward.z)){
			rotate = new Matrix4f(
					new Vector4f(camera_right.x, camera_right.y, camera_right.z, 0),
					new Vector4f(camera_up.x, camera_up.y, camera_up.z, 0),
					new Vector4f(camera_forward.x, camera_forward.y, camera_forward.z, 0),
					new Vector4f(0, 0, 0, 1)
				);
		}
		
		for (int i = 0; i < particleCount; i++){
			Particle p = particles[i];
			p.update();
		}
		
		if (counter > particleFrequency){
			counter = 0;
			create();
			//ps.setParticles(particles);
		}
		counter++;
	}
	
	@Override
	public void draw() {
		setBlendFunc();
		
		vdo.getVAO().bind();
		vdo.getEBO().bind();
		os.bind();
		
		//glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		
		for (Particle p : particles){
			if (p != null){
				p.uniform(os.getShader());
				glDrawElements(GL_TRIANGLES, vdo.getCount(), GL_UNSIGNED_INT, 0);
			}
		}
		//glDrawElements(GL_TRIANGLES, vdo.getCount(), GL_UNSIGNED_INT, 0);
		
		glDepthMask(true);
		//glEnable(GL_DEPTH_TEST);
		
		os.unbind();
		vdo.getEBO().unbind();
		vdo.getVAO().unbind();
	}
	
}
