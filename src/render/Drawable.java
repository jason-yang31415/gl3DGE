package render;

import logic.Transform;
import render.shader.ObjectShader;
import render.shader.ShaderResource;
import render.shader.ShaderUpdater;

public class Drawable extends Transform {

	public ObjectShader os;
	public VertexDataObject vdo;

	private BlendFunction blend;
	private ShaderResource resources;

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

	public ShaderResource getShaderResource() {
		return resources;
	}

	public void setShaderResource(ShaderResource resource) {
		this.resources = resource;
	}

	public void update(Scene scene) {
		update(scene, null);
	}

	public void update(Scene scene, ShaderUpdater updater){
		super.update();
		/*shader.bind();
		shader.setUniformMat4f("model", getMatrix());
		shader.setUniformMat4f("view", cam.getLookAt());
		shader.unbind();*/
		if (scene != null)
			os.update(scene, this);
		if (updater != null)
			updater.update(os);
	}

	public void draw() {
		draw(this.resources);
	}

	public void draw(ShaderResource resources){
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
		if (resources != null) {
			os.getShader().bind();
			resources.bind(os.getShader());
		}
		vdo.draw(os);
		if (resources != null)
			resources.unbind();
	}

}
