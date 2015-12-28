package logic;

import util.Matrix4f;

public class Transform2D extends Transform {

	float ry;
	Matrix4f rot;
	Matrix4f rotCalc;
	Matrix4f rotX;
	Matrix4f rotY;
	
	public Transform2D(){
		super();
		ry = 0;
		rot = new Matrix4f();
		rotCalc = new Matrix4f();
		rotX = new Matrix4f();
		rotY = new Matrix4f();
	}
	
	@Override
	public void translate(float x, float y, float z){
		float dx = 0;
		float dy = 0;
		float dz = 0;
		dx += z * -rotCalc.m02;
		dy += z * -rotCalc.m12;
		dz += z * rotCalc.m22;
		
		dx += x * rotCalc.m00;
		dy += x * rotCalc.m10;
		dz += x * -rotCalc.m20;
		
		dy += y;
		matrix = matrix.multiply(Matrix4f.translate(dx, dy, dz));
	}
	
	@Override
	public void rotate(float r, float x, float y, float z) {
		if (x == 1 && y == 0){
			if (Math.abs(ry + r) < 90){
				rotX = rotX.multiply(Matrix4f.rotate(r, 1, 0, 0));
				ry += r;
			}
		}
		else if (x == 0 && y == 1){
			rotY = rotY.multiply(Matrix4f.rotate(r, 0, 1, 0));
		}
		else
			System.out.println("Transform2D cannot handle transforms in both x and y axes simultaneously");
		
		rot = rotX.multiply(rotY);
		rotCalc = rotY.multiply(rotX);
	}
	
	@Override
	public Matrix4f getMatrix(){
		//System.out.println(rot.m02 + ", " + rot.m12 + ", " + rot.m22);
		
		Matrix4f test = rot.multiply(matrix);
		//System.out.println(test.m03 + ", " + test.m13 + ", " + test.m23);
		return test;
	}
	
}
