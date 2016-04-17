package render;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import render.shader.ObjectShader;
import logic.Transform;

public class Drawable extends Transform {
	
	public ObjectShader os;
	
	public Drawable(ObjectShader os){
		this.os = os;
		os.init();
	}
	
	/*public void translate(float x, float y, float z){
		transform = transform.multiply(Matrix4f.translate(x, y, z));
	}
	
	public void rotate(float r, float x, float y, float z){
		transform = transform.multiply(Matrix4f.rotate(r, x, y, z));
	}*/
	
	public void setBlendFunc(){
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void update(Scene scene){
		/*shader.bind();
		shader.setUniformMat4f("model", getMatrix());
		shader.setUniformMat4f("view", cam.getLookAt());
		shader.unbind();*/
		os.update(scene, this);
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
		setBlendFunc();
		os.draw();
	}
	
}
