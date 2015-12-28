package render;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Shader {

	private final int id;
	
	public Shader(int type, CharSequence source){
		id = glCreateShader(type);
		glShaderSource(id, source);
		glCompileShader(id);
		
		check();
	}
	
	public void check(){
		int status = glGetShaderi(id, GL_COMPILE_STATUS);
		if (status != GL_TRUE)
			throw new RuntimeException(glGetShaderInfoLog(id));
	}
	
	public void delete(){
		glDeleteShader(id);
	}
	
	public int getID(){
		return id;
	}
	
	public static Shader loadShader(int type, InputStream in){
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null){
				sb.append(line).append("\n");
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		
		CharSequence source = sb.toString();
		Shader shader = new Shader(type, source);
		return shader;
	}
	
}
