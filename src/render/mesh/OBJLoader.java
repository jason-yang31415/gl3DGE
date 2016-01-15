package render.mesh;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import render.DefaultGameObjectInit;
import render.Shader;
import util.Vector2f;
import util.Vector3f;

public class OBJLoader {
	
	public static void loadGameObjectData(DefaultGameObjectInit dgoi, String objPath, String vertexPath, String fragmentPath, boolean smooth) 
			throws FileNotFoundException, IOException{
		
		Mesh mesh = new Mesh();
		
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		//FloatBuffer vertices = BufferUtils.createFloatBuffer(2048);
		ArrayList<Float> verts_float = new ArrayList<Float>();
		int count = 0;
		
		InputStream is = OBJLoader.class.getResourceAsStream(Resource.OBJ_DIR + objPath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		//Material mtl = m.materials.get("default");
		String line;
		Material mtl = null;
		while ((line = reader.readLine()) != null){
			//parse
			if (line.startsWith("mtllib")){
				String file = line.split(" ")[1];
				mesh.loadMat(Resource.OBJ_DIR + file);
			}
			if (line.startsWith("v ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				//m.vertices.add(new Vector3f(x, y, z));
				mesh.verts.add(new Vertex(x, y, z));
			}
			else if (line.startsWith("vt ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				mesh.texcoords.add(new TexCoord(x, y));
			}
			else if (line.startsWith("vn ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				mesh.normals.add(new Vector3f(x, y, z));
			}
			else if (line.startsWith("usemtl ")){
				String name = String.valueOf(line.split(" ")[1]);
				mtl = mesh.mat.get(name);
				//System.out.println(name);
			}
			else if (line.startsWith("f ")){
				/*Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]),
						Float.valueOf(line.split(" ")[2].split("/")[0]),
						Float.valueOf(line.split(" ")[3].split("/")[0]));
				Vector3f normalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]),
						Float.valueOf(line.split(" ")[2].split("/")[2]),
						Float.valueOf(line.split(" ")[3].split("/")[2]));*/
				//m.faces.add(new Face(vertexIndices, normalIndices, mtl));
				String[][] face = new String[3][3];
				face[0] = line.split(" ")[1].split("/");
				face[1] = line.split(" ")[2].split("/");
				face[2] = line.split(" ")[3].split("/");
				
				for (String[] j : face){
					Vertex v = mesh.verts.get(Integer.parseInt(j[0]) - 1);
					TexCoord t = new TexCoord(0, 0);
					if (!j[1].equals(""))
						t = mesh.texcoords.get(Integer.parseInt(j[1]) - 1);
					Vector3f normal = mesh.normals.get(Integer.parseInt(j[2]) - 1);
					
					Vertex vert = new Vertex(v.position.x, v.position.y, v.position.z);
					vert.ambient = mtl.ambient;
					vert.diffuse = mtl.diffuse;
					vert.specular = mtl.specular;
					vert.normal = new Vector3f(normal.x, normal.y, normal.z);
					vert.tex = new Vector2f(t.position.x, t.position.y);
					
					if (smooth)
						checkIndex(verts, indices, vert);
					else {
						verts.add(vert);
						indices.add(verts.size() - 1);
					}
				}
				
				count += 3;
			}
		}
		reader.close();
		//return m;
		
		for (Vertex v : verts){
			verts_float.add(v.position.x);
			verts_float.add(v.position.y);
			verts_float.add(v.position.z);
			verts_float.add(v.normal.x);
			verts_float.add(v.normal.y);
			verts_float.add(v.normal.z);
			verts_float.add(v.diffuse.x);
			verts_float.add(v.diffuse.y);
			verts_float.add(v.diffuse.z);
			verts_float.add(v.specular.x);
			verts_float.add(v.specular.y);
			verts_float.add(v.specular.z);
			verts_float.add(v.tex.x);
			verts_float.add(v.tex.y);
		}
		
		String vertexSource = readFile(Resource.SHADER_DIR + vertexPath);
		
		Shader vertexShader = new Shader(GL_VERTEX_SHADER, vertexSource);
		
		String fragmentSource = readFile(Resource.SHADER_DIR + fragmentPath);
		
		Shader fragmentShader = new Shader(GL_FRAGMENT_SHADER, fragmentSource);
		
		float[] vertArray = new float[verts_float.size()];
		for (int n = 0; n < verts_float.size(); n++){
			vertArray[n] = verts_float.get(n);
		}
		
		FloatBuffer vertices = BufferUtils.createFloatBuffer(vertArray.length);
		vertices.put(vertArray).flip();
		
		int[] indexArray = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++){
			indexArray[i] = indices.get(i);
		}
		
		dgoi.loadVertices(vertices);
		dgoi.loadIndices(indexArray);
		dgoi.loadVertexShader(vertexShader);
		dgoi.loadFragmentShader(fragmentShader);
		//return new GameObject(vertices, vertexShader, fragmentShader, indexArray, tex, spec);
	}
	
	public static void checkIndex(ArrayList<Vertex> verts, ArrayList<Integer> i, Vertex vert){
		int index = -1;
		for (Vertex v : verts){
			if (isSimilar3f(vert.position, v.position) /*&& isSimilar3f(vert.normal, v.normal)*/){
				index = verts.indexOf(v);
			}
		}
		if (index >= 0){
			i.add(index);
			Vertex v = verts.get(index);
			Vector3f normal = new Vector3f((v.normal.x + vert.normal.x) / 2, (v.normal.y + vert.normal.y) / 2, 
					(v.normal.z + vert.normal.z) / 2);
			v.normal = normal;
		}
		else {
			verts.add(vert);
			i.add(verts.size() - 1);
		}
	}
	
	public static boolean isSimilar3f(Vector3f a, Vector3f b){
		float x = Math.abs(a.x - b.x);
		float y = Math.abs(a.y - b.y);
		float z = Math.abs(a.z - b.z);
		if (x < 0.01 && y < 0.01 && z < 0.01)
			return true;
		return false;
	}
	
	public static String readFile(String path) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(OBJLoader.class.getResourceAsStream(path)));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null){
			sb.append(line);
			sb.append("\n");
		}
		reader.close();
		return sb.toString();
	}
	
}
