package render.mesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.Vector3f;

public class Mesh {

	ArrayList<Vertex> verts = new ArrayList<Vertex>();
	ArrayList<TexCoord> texcoords = new ArrayList<TexCoord>();
	ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	Map<String, Material> mat = new HashMap<String, Material>();
	
	public void loadMat(String file) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file)));
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
	}
	
}
