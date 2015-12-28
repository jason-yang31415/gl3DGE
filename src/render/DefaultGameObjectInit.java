package render;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import game.GameObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import render.mesh.OBJLoader;
import render.mesh.Resource;
import util.Matrix4f;

public class DefaultGameObjectInit extends GameObjectInit {
	
	int[] indices;
	
	int ebo;
	
	TextureMap texture;
	SpecularityMap spec;
	float specularity = 0;
	BumpMap bump;
	EmissionMap emission;
	
	@Override
	public void load(String path) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(GameObject.class.getResourceAsStream(Resource.GAMEOBJECT_DIR + path)));
		String line;
		
		String vertexPath = "vertex";
		String fragmentPath = "fragment";
		String obj = "";
		float radius = 1.0f;
		while ((line = reader.readLine()) != null){
			line = line.replace(" ", "");
			String[] s = line.split(":");
			if (s[0].equals("vertex"))
				vertexPath = s[1];
			else if (s[0].equals("fragment"))
				fragmentPath = s[1];
			else if (s[0].equals("obj"))
				obj = s[1];
			else if (s[0].equals("texture"))
				texture = TextureMap.load(s[1]);
			else if (s[0].equals("specularity")){
				try {
					specularity = Float.parseFloat(s[1]);
				} catch (NumberFormatException e){
					spec = SpecularityMap.load(s[1]);
				}
			}
			else if (s[0].equals("bump"))
				bump = BumpMap.load(s[1]);
			else if (s[0].equals("emission"))
				emission = EmissionMap.load(s[1]);
			else if (s[0].equals("radius"))
				radius = Float.parseFloat(s[1]);
		}
		reader.close();
		
		if (obj != ""){
			OBJLoader.loadGameObjectData(this, obj, vertexPath, fragmentPath);
		}
		else
			throw new FileNotFoundException("Could not find obj file");
	}
	
	public void loadIndices(int[] indices){
		this.indices = indices;
	}
	
	@Override
	public void init() {
		count = indices.length;
		
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();
		
		/*for (int i : indices){
			System.out.println(i);
		}*/
		
		// create vao and vbo
		vao = new VertexArrayObject();
		vao.bind();
		
		vbo = new VertexBufferObject();
		vbo.bind(GL_ARRAY_BUFFER);
		vbo.bufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		
		ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		// create shader program
		shader = new ShaderProgram();
		shader.attachShader(vertexShader);
		shader.attachShader(fragmentShader);
		shader.bindFragDataLocation(0, "fragColor");
		shader.link();
		shader.bind();
		
		int floatSize = 4;
		int stride = 14;
		int posAttrib = shader.getAttribLocation("position");
		shader.enableVertexAttribArray(posAttrib);
		shader.vertexAttribPointer(posAttrib, 3, stride * floatSize, 0);
		int normalAttrib = shader.getAttribLocation("normal");
		shader.enableVertexAttribArray(normalAttrib);
		shader.vertexAttribPointer(normalAttrib, 3, stride * floatSize, 3 * floatSize);
		int colorAttrib = shader.getAttribLocation("color");
		shader.enableVertexAttribArray(colorAttrib);
		shader.vertexAttribPointer(colorAttrib, 3, stride * floatSize, 6 * floatSize);
		int specularColorAttrib = shader.getAttribLocation("specularColor");
		shader.enableVertexAttribArray(specularColorAttrib);
		shader.vertexAttribPointer(specularColorAttrib, 3, stride * floatSize, 9 * floatSize);
		int texAttrib = shader.getAttribLocation("texcoord");
		shader.enableVertexAttribArray(texAttrib);
		shader.vertexAttribPointer(texAttrib, 2, stride * floatSize, 12 * floatSize);
		
		Matrix4f model = new Matrix4f();
		shader.setUniformMat4f("model", model);
		Matrix4f view = new Matrix4f();
		shader.setUniformMat4f("view", view);

		//float ratio = LWJGL3.WIDTH / LWJGL3.HEIGHT;
		float ratio = 1;
		Matrix4f projection = Matrix4f.perspective(90, ratio, 0.01f, 100);
		//Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
		shader.setUniformMat4f("projection", projection);
		
		//Vector3f light = new Vector3f(0, 0, 0);
		//Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
		//shader.setUniform3f("light", light.x,light.y, light.z);
		
		shader.setUniform1i("tex", SamplerMap.TEX_DEFAULT);
		
		int enableTex = ((texture != null) ? 1 : 0);
		shader.setUniform1i("enableTex", enableTex);
		
		shader.setUniform1i("spec", SamplerMap.SPEC_DEFAULT);
		
		int enableSpec = ((spec != null) ? 1 : 0);
		shader.setUniform1i("enableSpec", enableSpec);
		
		shader.setUniform1f("specularity", specularity);
		
		shader.setUniform1i("bump", SamplerMap.BUMP_DEFAULT);
		
		int enableBump = ((bump != null) ? 1 : 0);
		shader.setUniform1i("enableBump", enableBump);
		
		shader.setUniform1i("emission", SamplerMap.EMISSION_DEFAULT);
		
		int enableEmission = ((emission != null) ? 1 : 0);
		shader.setUniform1i("enableEmission", enableEmission);
		
		shader.unbind();
		vbo.unbind(GL_ARRAY_BUFFER);
		vao.unbind();
	}
	
	public void update(Scene scene, Drawable d){
		shader.bind();
		shader.setUniformMat4f("model", d.getMatrix());
		shader.setUniformMat4f("view", scene.getCamera().getLookAt());
		shader.setUniformVec3f("light", scene.getLight().getPos());
		shader.unbind();
	}
	
	public void bindSamplerMaps(){
		if (texture != null)
			texture.bind();
		if (spec != null)
			spec.bind();
		if (bump != null)
			bump.bind();
	}
	
	public void unbindSamplerMaps(){
		if (spec != null)
			spec.unbind();
		if (texture != null)
			texture.unbind();
		if (bump != null)
			bump.unbind();
	}
	
	public void draw(){
		vao.bind();
		shader.bind();
		bindSamplerMaps();
		glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
		unbindSamplerMaps();
		shader.unbind();
		vao.unbind();
	}
	
}
