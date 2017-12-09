package demo.lighting;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import java.io.IOException;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;

import display.Window;
import game.GameObject;
import game.Screen;
import render.Camera;
import render.Light;
import render.SamplerMap;
import render.Scene;
import render.UniformBufferObject;
import render.VertexDataObject;
import render.mesh.Material;
import render.mesh.Mesh;
import render.mesh.MeshFactory;
import render.shader.nodes.ArraySN;
import render.shader.nodes.ArraySNV;
import render.shader.nodes.BeckmannSpecularSN;
import render.shader.nodes.ColorSNV;
import render.shader.nodes.ConstantSNV;
import render.shader.nodes.DiffuseSN;
import render.shader.nodes.ForLoopSN;
import render.shader.nodes.FresnelSN;
import render.shader.nodes.LightIntensitySN;
import render.shader.nodes.MathSN;
import render.shader.nodes.MathSN.Operation;
import render.shader.nodes.MixSN;
import render.shader.nodes.MixSN.Blend;
import render.shader.nodes.NodeBasedShader;
import render.shader.nodes.PositionSNV;
import render.shader.nodes.RGBChannelSN;
import render.shader.nodes.RGBChannelSN.Channel;
import render.shader.nodes.SamplerSN;
import render.shader.nodes.SamplerSNV;
import render.shader.nodes.ShaderNodeValue;
import render.shader.nodes.Structure;
import render.shader.nodes.StructureSN;
import render.shader.nodes.StructureSNV;
import render.shader.nodes.UniformBufferObjectSNV;
import render.shader.nodes.ValueSNV;
import util.Matrix4f;
import util.Vector3f;

public class MainScreen extends Screen {

	// game data
	GameObject square;
	Camera cam;

	Scene scene;
	Light l1, l2, l3;

	public MainScreen(Window window) {
		super(window);
	}

	@Override
	public void init(){
		setup();

		scene = new Scene();

		cam = new Camera();
		cam.setMode(Camera.Mode.CHASE);
		scene.setCamera(cam);

		l1 = new Light(0, 1.1547f, 3, 1, 0, 0, 5);
		scene.addLight(l1);

		l2 = new Light(-1f, -0.57735f, 3, 0, 1, 0, 5);
		scene.addLight(l2);

		l3 = new Light(1f, -0.57735f, 3, 0, 0, 1, 5);
		scene.addLight(l3);
	}

	public void setup(){
		// SET UP LIGHTING OBJECTS
		UniformBufferObject ubo = new UniformBufferObject(1); //NOTE: FIX
		ubo.bindBufferBase(1);

		UniformBufferObjectSNV lighting = new UniformBufferObjectSNV("lighting", ubo);
		NodeBasedShader.setUBO(lighting);

		Structure lightStruct = new Structure("Light");
		lightStruct.addValue(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION, new PositionSNV(null, "lightStructUBOPos"));
		lightStruct.addValue(ShaderNodeValue.UNIFORM_LIGHT_UBO_COLOR, new ColorSNV(null, "lightUBOColor"));
		lightStruct.addValue(ShaderNodeValue.UNIFORM_LIGHT_UBO_POWER, new ValueSNV(null, "lightUBOPower").defineAsFloat());
		NodeBasedShader.addStructure(lightStruct);

		ArraySNV lightArray = new ArraySNV(null, new StructureSNV(null, "lightUBOStruct", lightStruct), 4);
		lighting.addUniform(ShaderNodeValue.UNIFORM_LIGHT_UBO_STRUCT, lightArray);
		lighting.addUniform(ShaderNodeValue.UNIFORM_LIGHT_UBO_NUMBER, new ValueSNV(null, "lightUBONumber").defineAsFloat());

		// CREATE SQUARE AND SHADER
		Material mat = new Material();
		mat.setDiffuse(new Vector3f(220/255f, 1, 222/255f));
		Mesh mesh = MeshFactory.createSquare(mat);
		mesh.transform(Matrix4f.scale(2, 2, 0));

		NodeBasedShader nbs = new NodeBasedShader();
		nbs.addInput(ShaderNodeValue.INPUT_POSITION, nbs.getInputNode().getOutPosition());
		nbs.addInput(ShaderNodeValue.INPUT_NORMAL, nbs.getInputNode().getOutNormal());
		nbs.addInput(ShaderNodeValue.INPUT_COLOR, nbs.getInputNode().getOutColor());
		nbs.addInput(ShaderNodeValue.INPUT_TEXTURE_COORDINATE, nbs.getInputNode().getOutTextureCoordinate());
		nbs.addUniform(ShaderNodeValue.UNIFORM_CAMERA_POSITION, new PositionSNV(null, "cameraPos"));

		SamplerMap textureMap = SamplerMap.load("/demo/lighting/tex/tex.png", 0);
		nbs.addSampler(textureMap);
		SamplerSNV textureSNV = new SamplerSNV(null, "sample");
		textureSNV.setSampler(textureMap);
		nbs.addUniform("sample", textureSNV);

		SamplerSN texture = new SamplerSN(nbs);
		texture.setInSampler(textureSNV);
		texture.setInTextureCoordinate(nbs.getInputNode().getOutTextureCoordinate());
		nbs.addNode(texture);

		SamplerMap roughnessMap = SamplerMap.load("/demo/lighting/tex/roughness.png", 0);
		nbs.addSampler(roughnessMap);
		SamplerSNV roughnessSNV = new SamplerSNV(null, "sample");
		roughnessSNV.setSampler(roughnessMap);
		nbs.addUniform("sample", roughnessSNV);

		SamplerSN roughness = new SamplerSN(nbs);
		roughness.setInSampler(roughnessSNV);
		roughness.setInTextureCoordinate(nbs.getInputNode().getOutTextureCoordinate());
		nbs.addNode(roughness);

		RGBChannelSN rough = new RGBChannelSN(nbs);
		rough.setChannel(Channel.RED);
		rough.setInColor(roughness.getOutColor());
		nbs.addNode(rough);

		FresnelSN fresnel = new FresnelSN(nbs);
		fresnel.setInIOR(new ConstantSNV(1.3f));
		fresnel.setInNormal(nbs.getInputNode().getOutNormal());
		nbs.addNode(fresnel);

		ValueSNV index = new ValueSNV(null, "index").defineAsFloat(1);
		nbs.addConstant(index);

		ForLoopSN loop = new ForLoopSN(nbs);
		loop.setInIterator(index);
		loop.setInMax((ValueSNV) NodeBasedShader.getUBO().getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_UBO_NUMBER));
		nbs.addNode(loop);

		BeckmannSpecularSN beckmann = new BeckmannSpecularSN(nbs);
		beckmann.setInRoughness(rough.getOutValue());
		beckmann.setInLightIndex(index);
		loop.addNode(beckmann);

		ArraySN arraysn = new ArraySN(nbs);
		arraysn.setInArray((ArraySNV) NodeBasedShader.getUBO().getUniforms().get(ShaderNodeValue.UNIFORM_LIGHT_UBO_STRUCT));
		arraysn.setInIndex(index);
		loop.addNode(arraysn);

		StructureSN structuresn = new StructureSN(nbs, ShaderNodeValue.UNIFORM_LIGHT_UBO_COLOR);
		structuresn.setInStructure((StructureSNV) arraysn.getOutValue());
		loop.addNode(structuresn);

		MathSN specular = new MathSN(nbs, Operation.MULTIPLY);
		specular.setInValue1((ValueSNV) structuresn.getOutValue());
		specular.setInValue2(beckmann.getOutKspec());
		specular.getOutValue().defineAsVector3f();
		loop.addNode(specular);

		DiffuseSN lambert = new DiffuseSN(nbs);
		lambert.setInLightIndex(index);
		lambert.setInColor(texture.getOutColor());
		loop.addNode(lambert);

		MathSN diffuse = new MathSN(nbs, Operation.MULTIPLY);
		diffuse.setInValue1(lambert.getOutColor());
		diffuse.setInValue2((ValueSNV) structuresn.getOutValue());
		diffuse.getOutValue().defineAsVector3f();
		loop.addNode(diffuse);

		MixSN mix = new MixSN(nbs);
		mix.setBlend(Blend.MIX);
		mix.setInFactor(fresnel.getOutR());
		mix.setInValue1(diffuse.getOutValue());
		mix.setInValue2(specular.getOutValue());
		mix.getOutValue().defineAsVector3f();
		loop.addNode(mix);

		LightIntensitySN intensity = new LightIntensitySN(nbs);
		intensity.setInLightIndex(index);
		loop.addNode(intensity);

		MathSN shading = new MathSN(nbs, Operation.MULTIPLY);
		shading.setInValue1(mix.getOutValue());
		shading.setInValue2(intensity.getOutIntensity());
		shading.getOutValue().defineAsVector3f();
		loop.addNode(shading);

		loop.setInValue(shading.getOutValue());
		loop.getOutValue().defineAsVector3f();

		nbs.getOutputNode().setInColor(loop.getOutValue());

		try {
			nbs.loadShaders();
		} catch (IOException e) {
			e.printStackTrace();
		}
		nbs.check();
		nbs.init();

		VertexDataObject vdo = new VertexDataObject();
		vdo.loadVertexData(mesh, nbs);
		square = new GameObject(nbs, vdo);
		Matrix4f projection = Matrix4f.perspective(90, 16/9f, 0.01f, 10);
		nbs.setMVP(new Matrix4f(), new Matrix4f(), projection);
	}

	@Override
	public void loop() {
		super.loop();

		input();

		cam.update();

		System.out.println(1 / delta);

		NodeBasedShader.updateUBO(scene);

		square.update(scene);
		square.draw();
	}

	public void input(){
		// keyboard
		Vector3f velocity = new Vector3f();

		float v = 0.5f;
		if (window.getKey(GLFW_KEY_LEFT_SHIFT) == 1)
			v = 1f;

		if (window.getKey(GLFW_KEY_W) == 1)
			velocity = velocity.add(new Vector3f(0, 0, (float) delta * v));
		if (window.getKey(GLFW_KEY_S) == 1)
			velocity = velocity.add(new Vector3f(0, 0, (float) -delta * v));
		if (window.getKey(GLFW_KEY_A) == 1)
			velocity = velocity.add(new Vector3f((float) -delta * v, 0, 0));
		if (window.getKey(GLFW_KEY_D) == 1)
			velocity = velocity.add(new Vector3f((float) delta * v, 0, 0));
		if (window.getKey(GLFW_KEY_E) == 1)
			velocity = velocity.add(new Vector3f(0, (float) delta * v, 0));
		if (window.getKey(GLFW_KEY_Q) == 1)
			velocity = velocity.add(new Vector3f(0, (float) -delta * v, 0));
		if (window.getKey(GLFW_KEY_1) == 1)
			l1.setPos(cam.getPos());
		if (window.getKey(GLFW_KEY_2) == 1)
			l2.setPos(cam.getPos());
		if (window.getKey(GLFW_KEY_3) == 1)
			l3.setPos(cam.getPos());
		cam.setVelocity(velocity);

		// mouse
		DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
		window.getCursorPos(x, y);

		cam.rotateAxes((float) (x.get(0) - super.getWidth() / 2) / 2, 0, 1, 0, null);
		cam.rotate((float) (y.get(0) - super.getHeight() / 2) / 2, 1, 0, 0);

		window.setCursorPos((double) super.getWidth() / 2, (double) super.getHeight() / 2);
	}

}
