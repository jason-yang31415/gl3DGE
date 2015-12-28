package render;

import logic.Transform;
// fix!!!!!

public abstract class Drawable extends Transform {

	/*int count;
	
	VertexArrayObject vao;
	VertexBufferObject vbo;
	int ebo;
	
	Shader vertexShader;
	Shader fragmentShader;
	ShaderProgram shader;
	
	TextureMap texture;
	SpecularityMap specmap;*/
	
	GameObjectInit goi;
	
	//Matrix4f transform;
	
	/*public static Drawable loadFromFloatArray(float[] verts, Shader vertexShader, Shader fragmentShader){
		// GRAPHICS
		
		// number of verts
		FloatBuffer vertices = BufferUtils.createFloatBuffer(verts.length);
		vertices.put(verts).flip();
		int count = (int) Math.floor(verts.length / 9);
		
		return new Drawable(vertices, vertexShader, fragmentShader, count);
	}*/
	
	/*public Drawable(FloatBuffer vertices, Shader vertexShader, Shader fragmentShader, int[] indices, TextureMap texture, SpecularityMap spec){
		super();
		
		count = indices.length;
		
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();
		
		this.texture = texture;
		this.specmap = spec;
		
		/*for (int i : indices){
			System.out.println(i);
		}
		
		// create vao and vbo
		vao = new VertexArrayObject();
		vao.bind();
		
		vbo = new VertexBufferObject();
		vbo.bind(GL_ARRAY_BUFFER);
		vbo.bufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		
		ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		// shaders
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		
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
		float ratio = 16 / 9;
		Matrix4f projection = Matrix4f.perspective(90, ratio, 0.01f, 100);
		//Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
		shader.setUniformMat4f("projection", projection);
		
		Vector3f light = new Vector3f(0, 0, 0);
		//Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
		shader.setUniform3f("lightPosition", light.x,light.y, light.z);
		
		//int texUnit = SamplerMap.TEX_DEFAULT;
		int texUnit = 0;
		shader.setUniform1i("tex", texUnit);
		
		int textured = ((texture != null) ? 1 : 0);
		shader.setUniform1i("textured", textured);
		
		int specUnit = SamplerMap.SPEC_DEFAULT;
		shader.setUniform1i("spec", specUnit);
		//System.out.println(GL_TEXTURE_2D);
		
		shader.unbind();
		vbo.unbind(GL_ARRAY_BUFFER);
		vao.unbind();
		
		
		// TRANSFORMS
		
		// load identity matrix
		//transform = new Matrix4f();
	}*/
	
	public Drawable(GameObjectInit goi){
		this.goi = goi;
		goi.init();
	}
	
	/*public void translate(float x, float y, float z){
		transform = transform.multiply(Matrix4f.translate(x, y, z));
	}
	
	public void rotate(float r, float x, float y, float z){
		transform = transform.multiply(Matrix4f.rotate(r, x, y, z));
	}*/
	
	public void update(Scene scene){
		/*shader.bind();
		shader.setUniformMat4f("model", getMatrix());
		shader.setUniformMat4f("view", cam.getLookAt());
		shader.unbind();*/
		goi.update(scene, this);
	}
	
	public void draw(){
		/*vao.bind();
		shader.bind();
		if (texture != null)
			texture.bind();
		specmap.bind();
		//glDrawArrays(GL_TRIANGLES, 0, count);
		glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
		specmap.unbind();
		if (texture != null)
			texture.unbind();
		shader.unbind();
		vao.unbind();*/
		goi.draw();
	}
	
}
