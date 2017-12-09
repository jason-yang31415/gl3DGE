package logic;

import util.Matrix4f;
import util.Vector3f;
import util.Vector4f;

public class Transform {

	public static enum Type {
		T2D,
		T3D,
		TORBIT
	}
	Type type = Type.T3D;

	protected Matrix4f matrix;
	protected Vector3f velocity = new Vector3f();

	// TESTING
	protected Matrix4f translate = new Matrix4f();
	protected Matrix4f rotate = new Matrix4f();
	protected Matrix4f scale = new Matrix4f();

	// 3D

	// ORBIT


	public Transform(){
		this(Type.T3D);
	}

	public Transform(Type type){
		this.type = type;

		matrix = new Matrix4f();
		velocity = new Vector3f();

		// 3D

		//ORBIT

	}

	public void reset(){
		matrix = new Matrix4f();
		translate = new Matrix4f();
		rotate = new Matrix4f();
		scale = new Matrix4f();
		velocity = new Vector3f();
	}

	public void update(){
		translate(velocity.x, velocity.y, velocity.z);
	}

	public void setVelocity(Vector3f velocity){
		this.velocity = velocity;
	}

	public Vector3f getVelocity(){
		return velocity;
	}

	public void translate(float x, float y, float z){
		translateAxes(x, y, z, rotate);
	}

	public void translateAxes(float x, float y, float z, Matrix4f axes){
		if (axes == null)
			axes = new Matrix4f();

		float dx = 0;
		float dy = 0;
		float dz = 0;

		dx -= z * axes.m02;
		dy -= z * axes.m12;
		dz -= z * axes.m22;

		dx += x * axes.m00;
		dy += x * axes.m10;
		dz += x * axes.m20;
		switch (type){
		case T2D:
			dy += y;
			break;
		case T3D:
			// ???
			dx += y * axes.m01;
			dy += y * -axes.m11;
			dz += y * axes.m21;
			break;
		case TORBIT:

			break;
		}

		translate = translate.multiply(Matrix4f.translate(dx, dy, dz));
	}

	public void rotate(float r, float x, float y, float z){
		rotateAxes(r, x, y, z, rotate);
	}

	public void rotateAxes(float r, float x, float y, float z, Matrix4f axes){
		if (axes == null)
			axes = new Matrix4f();

		Vector4f axis = new Vector4f(x, y, z, 1);
		axis = axes.multiply(axis);

		rotate = Matrix4f.rotate(-r, axis.x, axis.y, axis.z).multiply(rotate);
	}

	public void scale(float x, float y, float z){
		scale = Matrix4f.scale(x, y, z);
	}

	public void setTransformMode(Type type){
		this.type = type;
	}

	public Vector3f getPos(){
		float x = getMatrix().m03;
		float y = getMatrix().m13;
		float z = getMatrix().m23;
		return new Vector3f(x, y, z);
	}

	public void setPos(float x, float y, float z){
		translate.m03 = x;
		translate.m13 = y;
		translate.m23 = z;
	}

	public Matrix4f getTranslate(){
		return translate;
	}

	public Matrix4f getRotate(){
		return rotate;
	}

	public Matrix4f getScale(){
		return scale;
	}

	public Matrix4f getMatrix(){
		matrix = translate.multiply(rotate).multiply(scale);
		return matrix;
	}

	public Matrix4f getLookAt(){
		return getMatrix().invert();
	}

	// DEBUG

	public void print(){
		System.out.println(			rotate.m00 + "			" + rotate.m01 + "			" + rotate.m02
				+ "\n" +	rotate.m10 + "			" + rotate.m11 + "			" + rotate.m12
				+ "\n" +	rotate.m20 + "			" + rotate.m21 + "			" + rotate.m22);
	}

}
