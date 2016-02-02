package render;

import logic.Transform;

public abstract class Drawable extends Transform {
	
	public GameObjectInit goi;
	
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
