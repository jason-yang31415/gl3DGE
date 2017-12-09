package render;

import logic.Transform;
import render.shader.ObjectShader;

public class Drawable extends Transform {

	public ObjectShader os;
	public VertexDataObject vdo;

	BlendFunction blend;

	public Drawable(ObjectShader os, VertexDataObject vdo){
		this.os = os;
		this.vdo = vdo;

		blend = new BlendFunction();
		//os.init();
	}

	/*public void translate(float x, float y, float z){
		transform = transform.multiply(Matrix4f.translate(x, y, z));
	}

	public void rotate(float r, float x, float y, float z){
		transform = transform.multiply(Matrix4f.rotate(r, x, y, z));
	}*/

	public void blendFunc(){
		blend.blendFunc();
	}

	public void setBlendFunc(int sfactor, int dfactor){
		blend.setBlendFunc(sfactor, dfactor);
	}

	public void update(Scene scene){
		super.update();
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
		blendFunc();
		vdo.draw(os);
	}

}
