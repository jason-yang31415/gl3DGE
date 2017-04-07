package render.shader.nodes;

import java.util.LinkedHashMap;
import java.util.Map;

public class Structure {

	String name;
	Map<String, ShaderNodeValue> values = new LinkedHashMap<String, ShaderNodeValue>();
	
	public Structure(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void addValue(String key, ShaderNodeValue snv){
		values.put(key, snv);
	}
	
	public Map<String, ShaderNodeValue> getValues(){
		return values;
	}
	
}
