package render.shader.nodes;

import java.util.LinkedHashMap;
import java.util.Map;

import render.UniformBufferObject;

public class UniformBufferObjectSNV {
	
	String name;
	Map<String, ShaderNodeValue> uniforms = new LinkedHashMap<String, ShaderNodeValue>();
	
	UniformBufferObject ubo;
	
	public UniformBufferObjectSNV(String name){
		this.name = name;
	}
	
	public UniformBufferObjectSNV(String name, UniformBufferObject ubo){
		this.name = name;
		this.ubo = ubo;
	}
	
	public String getName(){
		return name;
	}
	
	public void addUniform(String key, ShaderNodeValue value){
		uniforms.put(key, value);
	}
	
	public Map<String, ShaderNodeValue> getUniforms(){
		return uniforms;
	}
	
	public void setUBO(UniformBufferObject ubo){
		this.ubo = ubo;
	}
	
	public UniformBufferObject getUBO(){
		return ubo;
	}

}
