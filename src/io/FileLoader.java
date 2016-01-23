package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import render.mesh.OBJLoader;

public class FileLoader {

	public static String loadFile(String path) throws IOException {
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
