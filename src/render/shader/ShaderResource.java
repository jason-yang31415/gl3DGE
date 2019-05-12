package render.shader;

import java.util.HashMap;
import java.util.Map;

import render.SamplerMap;

public class ShaderResource {

	private HashMap<String, SamplerMap> map = new HashMap<String, SamplerMap>();
	private HashMap<String, String> slots = new HashMap<String, String>();

	public ShaderResource() {

	}

	public ShaderResource(Map<String, String> slots) {
		addSlots(slots);
	}

	public void setSamplerSlot(String slot, SamplerMap sampler) {
		map.put(slot, sampler);
	}

	public void addSlot(String slot, String uniformName) {
		slots.put(slot, uniformName);
	}

	public void addSlots(Map<String, String> slots) {
		this.slots.putAll(slots);
	}

	public void bind(ShaderProgram shader) {
		for (String slot : map.keySet()) {
			shader.setUniform1i(slots.get(slot), map.get(slot).getLocation());
			map.get(slot).bind();
		}
	}

	public void unbind() {
		for (String slot : map.keySet()) {
			map.get(slot).unbind();
		}
	}

	public HashMap<String, SamplerMap> getSamplers() {
		return map;
	}

	public HashMap<String, String> getSlots(){
		return slots;
	}

}
