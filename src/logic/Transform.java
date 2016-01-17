package logic;

import util.Matrix4f;
import util.Vector3f;

public abstract class Transform {
	
	public static enum Type {
		T2D,
		T3D,
		TORBIT
	}
	Type type = Type.T3D;
	
	protected Matrix4f matrix;
	
	// TESTING
	Matrix4f translate = new Matrix4f();
	Matrix4f rotate = new Matrix4f();
	
	// 2D
	float ry;
	Matrix4f rotX;
	Matrix4f rotY;
	
	// 3D
	
	// ORBIT
	
	
	public Transform(){
		this(Type.T3D);
	}
	
	public Transform(Type type){
		this.type = type;
		
		matrix = new Matrix4f();
		
		// 2D
		ry = 0;
		rotX = new Matrix4f();
		rotY = new Matrix4f();
		
		// 3D
		
		//ORBIT
		
	}
	
	public void translate(float x, float y, float z){
		float dx = 0;
		float dy = 0;
		float dz = 0;
		dx -= z * rotate.m02;
		dy -= z * rotate.m12;
		dz -= z * rotate.m22;
		
		dx += x * rotate.m00;
		dy += x * rotate.m10;
		dz += x * rotate.m20;
		switch (type){
		case T2D:
			dy += y;
			break;
		case T3D:
			// ???
			dx += y * rotate.m01;
			dy += y * -rotate.m11;
			dz += y * rotate.m21;
			break;
		case TORBIT:
			
			break;
		}
		translate = translate.multiply(Matrix4f.translate(dx, dy, dz));
	}
	
	public void rotate(float r, float x, float y, float z){
		switch (type){
		case T2D:
			if (x == 1 && y == 0){
				if (Math.abs(ry + r) < 90){
					rotX = rotX.multiply(Matrix4f.rotate(-r, 1, 0, 0));
					ry += r;
				}
			}
			else if (x == 0 && y == 1){
				rotY = rotY.multiply(Matrix4f.rotate(-r, 0, 1, 0));
			}
			else
				System.out.println("Transform2D cannot handle transforms in both x and y axes simultaneously");
			
			rotate = rotY.multiply(rotX);
			break;
		case T3D:
			rotate = rotate.multiply(Matrix4f.rotate(r, x, y, z));
			break;
		case TORBIT:
			
			break;
		}
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
	
	public Matrix4f getMatrix(){
		/*switch (type){
		case T2D:
			return rot.multiply(matrix);
		case T3D:
			return matrix;
		case TORBIT:
			return matrix;
		default:
			return matrix;
		}*/
		matrix = translate.multiply(rotate);
		return matrix;
	}
	
	public Matrix4f getLookAt(){
		/*Matrix4f view = new Matrix4f();
		switch (type){
		case T2D:
			view = rotX.multiply(rotY).multiply(translate);
			break;
		case T3D:
			view = rotate.multiply(translate);
			break;
		case TORBIT:
			break;
		}*/
		
		return getMatrix().invert();
	}
	
	// DEBUG
	
	public void print(){
		System.out.println(			rotate.m00 + "			" + rotate.m01 + "			" + rotate.m02
						+ "\n" +	rotate.m10 + "			" + rotate.m11 + "			" + rotate.m12
						+ "\n" +	rotate.m20 + "			" + rotate.m21 + "			" + rotate.m22);
	}
	
}
