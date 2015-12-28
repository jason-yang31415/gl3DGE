package logic;

import util.Matrix4f;

public class Transform3D extends Transform {

	@Override
	public void translate(float x, float y, float z){
		matrix = matrix.multiply(Matrix4f.translate(x, y, z));
	}
	
	@Override
	public void rotate(float r, float x, float y, float z) {
		matrix = matrix.multiply(Matrix4f.rotate(r, x, y, z));
	}
	
}
