package render.shader.nodes;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import io.FileLoader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;

import render.Drawable;
import render.SamplerCube;
import render.SamplerMap;
import render.Scene;
import render.VertexBufferObject;
import render.mesh.Mesh;
import render.mesh.Resource;
import render.mesh.Vertex;
import render.shader.ObjectShader;
import render.shader.Shader;
import render.shader.ShaderProgram;

public class NodeBasedShader extends ObjectShader {
	
	static UBOShaderNodeValue ubo;
	
	Map<String, ShaderNodeValue> inputs = new LinkedHashMap<String, ShaderNodeValue>();
	Map<String, ShaderNodeValue> uniforms = new LinkedHashMap<String, ShaderNodeValue>();
	ArrayList<ShaderNodeValue> constants = new ArrayList<ShaderNodeValue>();
	ArrayList<ShaderNode> nodes = new ArrayList<ShaderNode>();
	ArrayList<String> functions = new ArrayList<String>();
	InputSN in = new InputSN(this);
	OutputSN out = new OutputSN(this);
	
	ArrayList<SamplerMap> samplers = new ArrayList<SamplerMap>();
	ArrayList<SamplerCube> samplerCubes = new ArrayList<SamplerCube>();
	
	int currentId = 0;
	
	public int genNodes(){
		int id = currentId;
		currentId++;
		return id;
	}
	
	public FloatBuffer getVertices(Mesh mesh){
		ArrayList<Float> vertex_data = new ArrayList<Float>();
		for (Vertex v : mesh.getVertices()){
			for (String key : inputs.keySet()){
				if (key.equals(ShaderNodeValue.INPUT_POSITION)){
					vertex_data.add(v.getPosition().x);
					vertex_data.add(v.getPosition().y);
					vertex_data.add(v.getPosition().z);
				}
				else if (key.equals(ShaderNodeValue.INPUT_NORMAL)){
					vertex_data.add(v.getNormal().x);
					vertex_data.add(v.getNormal().y);
					vertex_data.add(v.getNormal().z);
				}
				else if (key.equals(ShaderNodeValue.INPUT_COLOR)){
					vertex_data.add(v.getDiffuseColor().x);
					vertex_data.add(v.getDiffuseColor().y);
					vertex_data.add(v.getDiffuseColor().z);
				}
				else if (key.equals(ShaderNodeValue.INPUT_TEXTURE_COORDINATE)){
					vertex_data.add(v.getTextureCoordinate().x);
					vertex_data.add(v.getTextureCoordinate().y);
				}
			}
		}

		float[] vertArray = new float[vertex_data.size()];
		for (int n = 0; n < vertex_data.size(); n++) {
			vertArray[n] = vertex_data.get(n);
		}

		FloatBuffer vertices = BufferUtils.createFloatBuffer(vertArray.length);
		vertices.put(vertArray).flip();

		return vertices;
	}

	@Override
	public void loadShaders() throws IOException {
		String vertexSource = getVertexSource();
		System.out.println(vertexSource);
		Shader vertexShader = new Shader(GL_VERTEX_SHADER, vertexSource);
		String fragmentSource = getFragmentSource();
		System.out.println(fragmentSource);
		Shader fragmentShader = new Shader(GL_FRAGMENT_SHADER, fragmentSource);
		
		loadVertexShader(vertexShader);
		loadFragmentShader(fragmentShader);
	}
	
	public String getVertexSource(){
		StringBuilder sb = new StringBuilder();
		sb.append("#version 150 core\n");
		for (ShaderNodeValue input : inputs.values()){
			sb.append("in " + input.getType() + " " + input.getAttribute() + ";\n");
			sb.append("out " + input.getType() + " " + input.getVarying() + ";\n");
		}
		sb.append("uniform mat4 model;\n");
		sb.append("uniform mat4 view;\n");
		sb.append("uniform mat4 projection;\n");
		sb.append("void main(){\n");
		for (ShaderNodeValue input : inputs.values())
			//sb.append(input.getVertexGLSL());
			sb.append(input.getVarying() + " = " + input.getAttribute() + ";\n");
		sb.append("mat4 mvp = projection * view * model;\n");
		sb.append("gl_Position = mvp * vec4(in_" + inputs.get(ShaderNodeValue.INPUT_POSITION).getName() + ", 1.0);\n");
		sb.append("}\n");
		
		return sb.toString();
	}
	
	public String getFragmentSource(){
		StringBuilder sb = new StringBuilder();
		sb.append("#version 150 core\n");
		for (ShaderNodeValue input : inputs.values())
			sb.append("in " + input.getType() + " " + input.getVarying() + ";\n");
		sb.append("out vec4 fragColor;\n");
		for (ShaderNodeValue uniform : uniforms.values())
			sb.append("uniform " + uniform.getType() + " " + uniform.getName() + ";\n");
		if (ubo != null){
			sb.append("layout (std140) uniform " + ubo.getName() + " {\n");
			for (ShaderNodeValue uniform : ubo.getUniforms().values())
				sb.append("uniform " + uniform.getType() + " " + uniform.getName() + ";\n");
			sb.append("};\n");
		}

		sb.append("uniform mat4 model;\n");
		sb.append("uniform mat4 view;\n");
		sb.append("uniform mat4 projection;\n");
		//sb.append("uniform vec3 lightPos;\n");
		sb.append("\n");
		for (String function : functions){
			sb.append(function);
		}
		sb.append("\n");
		
		sb.append("void main() {\n");
		//sb.append("	vec3 global_lightPos = lightPos;\n");
		sb.append("vec3 " + getInputNode().getOutWorldPosition().getName() + " = (model * vec4(" + getInputNode().getOutPosition().getName() + ", 1)).xyz;\n");
		for (ShaderNodeValue snv : constants){
			sb.append(snv.getGLSL());
		}
		for (ShaderNode n : nodes){
			sb.append(n.getGLSL());
		}
		sb.append(out.getGLSL());
		sb.append("}\n");
		sb.append("\n");
		
		return sb.toString();
	}
	
	public void addNode(ShaderNode node){
		nodes.add(node);
	}
	
	public void addInput(String name, ShaderNodeValue input){
		inputs.put(name, input);
	}
	
	public void addUniform(String name, ShaderNodeValue uniform){
		uniforms.put(name, uniform);
	}
	
	public void addConstant(ShaderNodeValue value){
		constants.add(value);
	}
	
	public void addSampler(SamplerMap sampler){
		samplers.add(sampler);
	}
	
	public void addSamplerCube(SamplerCube samplerCube){
		samplerCubes.add(samplerCube);
	}
	
	public void addFunction(String function){
		functions.add(function);
	}
	
	public void addFunctionFromFile(String path) throws IOException{
		addFunction(FileLoader.loadFile(Resource.SHADER_DIR + path));
	}
	
	public InputSN getInputNode(){
		return in;
	}
	
	public Map<String, ShaderNodeValue> getUniforms(){
		return uniforms;
	}
	
	public OutputSN getOutputNode(){
		return out;
	}
	
	public static void setUBO(UBOShaderNodeValue ubo){
		NodeBasedShader.ubo = ubo;
	}
	
	public static UBOShaderNodeValue getUBO(){
		return ubo;
	}

	@Override
	public void init() {
		shader = new ShaderProgram();
		shader.attachShader(vertexShader);
		shader.attachShader(fragmentShader);
		shader.bindFragDataLocation(0, "fragColor");
		shader.link();
		shader.bind();
		
		for (ShaderNodeValue snv : uniforms.values()){
			if (snv instanceof SamplerSNV)
				shader.setUniform1i(snv.getName(), ((SamplerSNV) snv).getSampler().getLocation());
			else if (snv instanceof SamplerCubeSNV)
				shader.setUniform1i(snv.getName(), ((SamplerCubeSNV) snv).getSamplerCube().getLocation());
		}
		shader.uniformBlockBinding(ubo.getName(), ubo.getUBO());
		shader.unbind();
	}
	
	public void setVBOPointers(VertexBufferObject vbo){
		shader.bind();
		vbo.bind(GL_ARRAY_BUFFER);
		int floatSize = 4; //NOTE: FIX
		int stride = 0;
		for (ShaderNodeValue input : inputs.values())
			stride += input.getSize();
		
		int offset = 0;
		for (ShaderNodeValue input : inputs.values()){
			int attrib = shader.getAttribLocation(input.getAttribute());
			shader.enableVertexAttribArray(attrib);
			shader.vertexAttribPointer(attrib, input.getSize(), stride * floatSize, offset * floatSize);
			offset += input.getSize();
		}
		vbo.unbind(GL_ARRAY_BUFFER);
		shader.unbind();
	}

	@Override
	public void update(Scene scene, Drawable d) {
		shader.bind();
		shader.setUniformMat4f("model", d.getMatrix());
		shader.setUniformMat4f("view", scene.getCamera().getLookAt());
		/*if (uniforms.containsKey(ShaderNodeValue.UNIFORM_LIGHT_POSITION))
			shader.setUniformVec3f(uniforms.get(ShaderNodeValue.UNIFORM_LIGHT_POSITION).getName(), scene.getLight().getPos());*/
		if (uniforms.containsKey(ShaderNodeValue.UNIFORM_CAMERA_POSITION))
			shader.setUniformVec3f(uniforms.get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName(), scene.getCamera().getPos());
		
		shader.unbind();
	}
	
	public static void updateUBO(Scene scene){
		ArrayList<Float> array = new ArrayList<Float>();
		for (String key : ubo.getUniforms().keySet()){
			ShaderNodeValue snv = ubo.getUniforms().get(key);
			int size = std140(snv.getSize());
			int extra = array.size() % size;
			int num = (extra == 0) ? 0 : size - extra;
			for (int i = 0; i < num; i++)
				array.add(1f);
			
			if (key.equals(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION)){
				array.add(scene.getLight().getPos().x);
				array.add(scene.getLight().getPos().y);
				array.add(scene.getLight().getPos().z);
			}
			else if (key.equals(ShaderNodeValue.UNIFORM_LIGHT_UBO_COLOR)){
				array.add(scene.getLight().getColor().x);
				array.add(scene.getLight().getColor().y);
				array.add(scene.getLight().getColor().z);
			}
			else if (key.equals(ShaderNodeValue.UNIFORM_LIGHT_UBO_POWER)){
				array.add(scene.getLight().getPower());
			}
		}
		FloatBuffer fb = BufferUtils.createFloatBuffer(array.size());
		for (float f : array)
			fb.put(f);
		fb.flip();
		ubo.getUBO().bind();
		//GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, fb);
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, fb, GL15.GL_DYNAMIC_DRAW);
		ubo.getUBO().unbind();
	}
	
	public static int std140(int size){
		switch (size){
		case 1:
			return 1;
		case 2:
			return 2;
		case 3:
			return 4;
		case 4:
			return 4;
		default:
			return 0;
		}
	}
	
	public void bind(){
		shader.bind();
		for (SamplerMap sm : samplers)
			sm.bind();
		for (SamplerCube sc : samplerCubes)
			sc.bind();
	}
	
	public void unbind(){
		for (SamplerMap sm : samplers)
			sm.unbind();
		for (SamplerCube sc : samplerCubes)
			sc.unbind();
		shader.unbind();
	}
	
}
