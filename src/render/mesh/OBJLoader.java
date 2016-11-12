package render.mesh;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.Vector2f;
import util.Vector3f;

public class OBJLoader {
	
	public static void loadGameObjectData(Mesh mesh, String objPath, boolean smooth) 
			throws FileNotFoundException, IOException{
		
		Map<String, Material> materials = new HashMap<String, Material>();
		
		ArrayList<Vector3f> vertex_cache = new ArrayList<Vector3f>();
		ArrayList<Vector2f> tex_cache = new ArrayList<Vector2f>();
		ArrayList<Vector3f> normal_cache = new ArrayList<Vector3f>();
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		
		//ArrayList<Vertex> verts_delete = new ArrayList<Vertex>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		//FloatBuffer vertices = BufferUtils.createFloatBuffer(2048);
		//ArrayList<Float> verts_float = new ArrayList<Float>();
		//int count = 0;
		
		InputStream is = OBJLoader.class.getResourceAsStream(Resource.OBJ_DIR + objPath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		//Material mtl = m.materials.get("default");
		String line;
		Material mtl = null;
		while ((line = reader.readLine()) != null){
			//parse
			if (line.startsWith("mtllib")){
				String file = line.split(" ")[1];
				//mesh.loadMat(Resource.OBJ_DIR + file);
				materials = loadMaterials(Resource.OBJ_DIR + file);
			}
			if (line.startsWith("v ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				//mesh.verts.add(new Vertex(x, y, z));
				vertex_cache.add(new Vector3f(x, y, z));
			}
			else if (line.startsWith("vt ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				//mesh.texcoords.add(new TexCoord(x, y));
				tex_cache.add(new Vector2f(x, y));
			}
			else if (line.startsWith("vn ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				//mesh.normals.add(new Vector3f(x, y, z));
				normal_cache.add(new Vector3f(x, y, z));
			}
			else if (line.startsWith("usemtl ")){
				String name = String.valueOf(line.split(" ")[1]);
				mtl = materials.get(name);
				//System.out.println(name);
			}
			else if (line.startsWith("f ")){
				String[][] face = new String[3][3];
				face[0] = line.split(" ")[1].split("/");
				face[1] = line.split(" ")[2].split("/");
				face[2] = line.split(" ")[3].split("/");
				
				for (String[] j : face){
					Vector3f v = vertex_cache.get(Integer.parseInt(j[0]) - 1);
					Vector2f tex = new Vector2f(0, 0);
					if (!j[1].equals(""))
						tex = tex_cache.get(Integer.parseInt(j[1]) - 1);
					Vector3f normal = normal_cache.get(Integer.parseInt(j[2]) - 1);
					
					Vertex vertex = new Vertex(v);
					
					vertex.setMaterial(mtl);
					vertex.setNormal(normal);
					vertex.setTextureCoordinate(tex);
					
					if (smooth)
						checkIndex(vertices, indices, vertex);
					else {
						vertices.add(vertex);
						indices.add(vertices.size() - 1);
					}
				}
				
				//count += 3;
			}
		}
		reader.close();
		//return m;
		
		mesh.loadVertices(vertices);
		mesh.loadIndices(indices);
		
		/*for (Vertex v : vertices){
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
		dgoi.loadIndices(indexArray);*/
		//return new GameObject(vertices, vertexShader, fragmentShader, indexArray, tex, spec);
	}
	
	public static Map<String, Material> loadMaterials(String file) throws IOException{
		Map<String, Material> mat = new HashMap<String, Material>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(OBJLoader.class.getResourceAsStream(file)));
		String line;
		String current = "";
		while ((line = reader.readLine()) != null){
			//parse
			if (line.startsWith("newmtl ")){
				String name = String.valueOf(line.split(" ")[1]);
				Material mtl = new Material();
				mat.put(name, mtl);
				current = name;
			}
			else if (line.startsWith("Ka ")){
				Material mtl = mat.get(current);
				mtl.ambient = new Vector3f(Float.valueOf(line.split(" ")[1]), 
						Float.valueOf(line.split(" ")[2]), 
						Float.valueOf(line.split(" ")[3]));
			}
			else if (line.startsWith("Kd ")){
				Material mtl = mat.get(current);
				mtl.diffuse = new Vector3f(Float.valueOf(line.split(" ")[1]), 
					Float.valueOf(line.split(" ")[2]), 
					Float.valueOf(line.split(" ")[3]));
			}
			else if (line.startsWith("Ks ")){
				Material mtl = mat.get(current);
				mtl.specular = new Vector3f(Float.valueOf(line.split(" ")[1]), 
					Float.valueOf(line.split(" ")[2]), 
					Float.valueOf(line.split(" ")[3]));
			}
		}
		reader.close();
		
		return mat;
	}
	
	public static void checkIndex(ArrayList<Vertex> verts, ArrayList<Integer> i, Vertex vert){
		int index = -1;
		for (Vertex v : verts){
			if (isSimilar(vert, v) /*&& isSimilar3f(vert.normal, v.normal)*/){
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
	
	public static boolean isSimilar(Vertex a, Vertex b){
		Vector3f posA = a.getPosition();
		Vector3f posB = b.getPosition();
		float posX = Math.abs(posA.x - posB.x);
		float posY = Math.abs(posA.y - posB.y);
		float posZ = Math.abs(posA.z - posB.z);
		if (!(posX < 0.01 && posY < 0.01 && posZ < 0.01))
			return false;
		
		if (a.getTextureCoordinate() != null && b.getTextureCoordinate() != null){
			Vector2f texA = a.getTextureCoordinate();
			Vector2f texB = b.getTextureCoordinate();
			float texX = Math.abs(texA.x - texB.x);
			float texY = Math.abs(texA.y - texB.y);
			if (!(texX < 0.01 && texY < 0.01))
				return false;
				
		}
		return true;
	}
	
}
