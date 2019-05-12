package demo.deferred.earth;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import display.Window;
import game.GameObject;
import game.Screen;
import render.Camera;
import render.Drawable;
import render.FramebufferObject;
import render.Light;
import render.RenderTarget;
import render.RenderTarget.Target;
import render.SamplerCube;
import render.SamplerMap;
import render.Scene;
import render.UniformBufferObject;
import render.VertexDataObject;
import render.mesh.Mesh;
import render.mesh.MeshFactory;
import render.mesh.OBJLoader;
import render.mesh.Resource;
import render.mesh.Vertex;
import render.shader.ShaderResource;
import render.shader.nodes.ArraySNV;
import render.shader.nodes.BeckmannSpecularSN;
import render.shader.nodes.ConstantSNV;
import render.shader.nodes.DiffuseSN;
import render.shader.nodes.FastGaussianSN;
import render.shader.nodes.FresnelSN;
import render.shader.nodes.GenericSN;
import render.shader.nodes.MathSN;
import render.shader.nodes.MathSN.Operation;
import render.shader.nodes.MixSN;
import render.shader.nodes.MixSN.Blend;
import render.shader.nodes.NodeBasedShader;
import render.shader.nodes.NormalMapConverterSN;
import render.shader.nodes.NormalMapConverterSN.Mode;
import render.shader.nodes.OutputSN;
import render.shader.nodes.PositionSNV;
import render.shader.nodes.RGBChannelSN;
import render.shader.nodes.RGBChannelSN.Channel;
import render.shader.nodes.SamplerCubeSN;
import render.shader.nodes.SamplerCubeSNV;
import render.shader.nodes.SamplerSN;
import render.shader.nodes.SamplerSNV;
import render.shader.nodes.ShaderNodeValue;
import render.shader.nodes.Structure;
import render.shader.nodes.StructureSNV;
import render.shader.nodes.UniformBufferObjectSNV;
import render.shader.nodes.ValueSNV;
import render.text.Text;
import render.text.TrueTypeFont;
import util.Matrix4f;
import util.Vector2f;
import util.Vector3f;

public class MainScreen extends Screen {

	Scene scene;

	// game data
	Drawable earth;
	Camera cam;
	Light light;

	// shading
	FramebufferObject shadingFBO;
	FramebufferObject postFBO1;
	FramebufferObject postFBO2;
	Drawable shadingQuad;
	Drawable skybox;
	Drawable postQuad1;
	Drawable postQuad2;

	UniformBufferObjectSNV lighting;

	// text
	TrueTypeFont ttf;
	Text text;

	public MainScreen(Window window) {
		super(window);
	}

	@Override
	public void init() {
		setup();
		initFonts();

		scene = new Scene();

		cam = new Camera();
		scene.setCamera(cam);
		cam.setPos(0, 0, 5);

		light = new Light(10, 1, 10, 1, 1, 1, 50);
		scene.addLight(light);

		GL11.glClearDepth(1.0f);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public void initFonts() {
		ttf = new TrueTypeFont("demo/deferred/earth/fonts/OpenSans-Regular.ttf", 32);
	}

	public void setup() {
		// set directories for convenience
		Resource.setOBJDir("/demo/deferred/earth/obj/");
		Resource.setTexDir("/demo/deferred/earth/tex/");
		Resource.setShaderDir("/demo/deferred/earth/shaders/");

		/*
		 * definition for struct Light:
		 * 
		 * struct Light {
		 * 		vec3 lightStructUBOPos;
		 * };
		 */
		Structure lightStruct = new Structure("Light");
		lightStruct.addValue(ShaderNodeValue.UNIFORM_LIGHT_UBO_POSITION, new PositionSNV(null, "lightStructUBOPos"));
		// add struct definition to node-based shaders
		NodeBasedShader.addStructure(lightStruct);

		/*
		 * create UBO for scene lighting containing an array of Light structs:
		 * 
		 * uniform lighting {
		 * 		uniform Light[4] lightUBOStruct;
		 * };
		 */
		UniformBufferObject ubo = new UniformBufferObject(3);
		ubo.bindBufferBase(0);
		// shader graph representation of lighting UBO
		lighting = new UniformBufferObjectSNV("lighting", ubo);
		NodeBasedShader.setUBO(lighting);

		// create array variable of type Light[] and add as uniform to lighting UBO:
		ArraySNV lightArray = new ArraySNV(null, new StructureSNV(null, "lightUBOStruct", lightStruct), 4);
		lighting.addUniform(ShaderNodeValue.UNIFORM_LIGHT_UBO_STRUCT, lightArray);

		// initialize shading framebuffer
		initShadingFBO();
		// initialize earth
		initEarth();
		// initialize skybox
		initSkybox();
		// initialize postprocessing (2 passes)
		initPost1();
		initPost2();
	}

	public void initShadingFBO() {
		// create textures for each variable needed for deferred shading (+ depth)
		SamplerMap depth = new SamplerMap(super.getWidth(), super.getHeight(), 0);
		depth.texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, super.getWidth(), super.getHeight(), 0,
				GL_DEPTH_COMPONENT, GL_FLOAT, 0);
		SamplerMap pos = new SamplerMap(super.getWidth(), super.getHeight(), 1);
		pos.texImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGB32F, super.getWidth(), super.getHeight(), 0, GL11.GL_RGB,
				GL11.GL_FLOAT, null);
		SamplerMap norm = new SamplerMap(super.getWidth(), super.getHeight(), 2);
		norm.texImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGB32F, super.getWidth(), super.getHeight(), 0, GL11.GL_RGB,
				GL11.GL_FLOAT, null);
		SamplerMap color = new SamplerMap(super.getWidth(), super.getHeight(), 3);
		color.texImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGB, super.getWidth(), super.getHeight(), 0, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, null);
		SamplerMap emission = new SamplerMap(super.getWidth(), super.getHeight(), 4);
		emission.texImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGB, super.getWidth(), super.getHeight(), 0, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, null);
		SamplerMap roughness = new SamplerMap(super.getWidth(), super.getHeight(), 5);
		roughness.texImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGB, super.getWidth(), super.getHeight(), 0, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, null);

		// add each texture as a target and create FBO
		LinkedHashMap<String, RenderTarget> targets = new LinkedHashMap<String, RenderTarget>();
		targets.put(FramebufferObject.FBO_TARGET_DEPTH, new RenderTarget(depth, Target.DEPTH));
		targets.put("pos", new RenderTarget(pos, Target.COLOR));
		targets.put("norm", new RenderTarget(norm, Target.COLOR));
		targets.put("color", new RenderTarget(color, Target.COLOR));
		targets.put("emission", new RenderTarget(emission, Target.COLOR));
		targets.put("roughness", new RenderTarget(roughness, Target.COLOR));
		shadingFBO = new FramebufferObject(super.getWidth(), super.getHeight(), targets);

		// shader for deferred shading
		NodeBasedShader shadingNBS = new NodeBasedShader();
		// mesh to display shaded scene
		Mesh shadingMesh = MeshFactory.createSquare(null);

		try {
			// add atmospheric effect function from file
			shadingNBS.addFunctionFromFile("atmosphere.glsl");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		/*
		 * define shader inputs from mesh (position, texture coordinate) and uniform
		 * (camera position):
		 * 
		 * in vec3 position;
		 * in vec2 texture_coordinate;
		 * uniform vec3 camera_position;
		 */
		shadingNBS.addInput(ShaderNodeValue.INPUT_POSITION, shadingNBS.getInputNode().getOutPosition());
		shadingNBS.addInput(ShaderNodeValue.INPUT_TEXTURE_COORDINATE,
				shadingNBS.getInputNode().getOutTextureCoordinate());
		shadingNBS.addUniform(ShaderNodeValue.UNIFORM_CAMERA_POSITION, new PositionSNV(null, "cameraPos"));

		/*
		 * define samplers uniforms needed:
		 * 
		 * uniform sampler2D pos;
		 * uniform sampler2D norm;
		 * ...
		 * uniform sampler2D roughness;
		 */
		SamplerSNV posSNV = new SamplerSNV(null, "pos");
		shadingNBS.addSamplerSlot("position", posSNV);

		SamplerSNV normSNV = new SamplerSNV(null, "norm");
		shadingNBS.addSamplerSlot("normal", normSNV);

		SamplerSNV colorSNV = new SamplerSNV(null, "color");
		shadingNBS.addSamplerSlot("color", colorSNV);

		SamplerSNV emissionSNV = new SamplerSNV(null, "emission");
		shadingNBS.addSamplerSlot("emission", emissionSNV);

		SamplerSNV roughnessSNV = new SamplerSNV(null, "roughness");
		shadingNBS.addSamplerSlot("roughness", roughnessSNV);

		// read position from pos texture at texture_coordinate
		SamplerSN posSN = new SamplerSN(shadingNBS);
		posSN.setInSampler(posSNV);
		posSN.setInTextureCoordinate(shadingNBS.getInputNode().getOutTextureCoordinate());
		shadingNBS.addNode(posSN);

		// read normal from norm texture at texture_coordinate
		SamplerSN normSN = new SamplerSN(shadingNBS);
		normSN.setInSampler(normSNV);
		normSN.setInTextureCoordinate(shadingNBS.getInputNode().getOutTextureCoordinate());
		shadingNBS.addNode(normSN);

		// read color from color texture at texture_coordinate
		SamplerSN colorSN = new SamplerSN(shadingNBS);
		colorSN.setInSampler(colorSNV);
		colorSN.setInTextureCoordinate(shadingNBS.getInputNode().getOutTextureCoordinate());
		shadingNBS.addNode(colorSN);

		// read emission from emission texture at texture_coordinate
		SamplerSN emissionSN = new SamplerSN(shadingNBS);
		emissionSN.setInSampler(emissionSNV);
		emissionSN.setInTextureCoordinate(shadingNBS.getInputNode().getOutTextureCoordinate());
		shadingNBS.addNode(emissionSN);

		// read roughness from roughness texture at texture_coordinate
		SamplerSN roughnessSN = new SamplerSN(shadingNBS);
		roughnessSN.setInSampler(roughnessSNV);
		roughnessSN.setInTextureCoordinate(shadingNBS.getInputNode().getOutTextureCoordinate());
		shadingNBS.addNode(roughnessSN);

		// read the red channel of roughness (converts RGB vector to float)
		RGBChannelSN roughnessChannelSN = new RGBChannelSN(shadingNBS);
		roughnessChannelSN.setInColor(roughnessSN.getOutColor());
		roughnessChannelSN.setChannel(Channel.RED);
		shadingNBS.addNode(roughnessChannelSN);

		// calculate specularity via Fresnel node
		FresnelSN fresnel = new FresnelSN(shadingNBS);
		fresnel.setInPosition(posSN.getOutColor());
		fresnel.setInNormal(normSN.getOutColor());
		fresnel.setInIOR(new ConstantSNV(1.33f));
		shadingNBS.addNode(fresnel);

		// calculate diffuse color via Lambert cosine node
		DiffuseSN lambertSN = new DiffuseSN(shadingNBS);
		lambertSN.setInColor(colorSN.getOutColor());
		lambertSN.setInNormal(normSN.getOutColor());
		lambertSN.setInPosition(posSN.getOutColor());
		shadingNBS.addNode(lambertSN);

		// calculate specular intensity via Beckmann node
		BeckmannSpecularSN beckmannSN = new BeckmannSpecularSN(shadingNBS);
		beckmannSN.setInRoughness(roughnessChannelSN.getOutValue());
		beckmannSN.setInNormal(normSN.getOutColor());
		beckmannSN.setInPosition(posSN.getOutColor());
		shadingNBS.addNode(beckmannSN);

		// calculate specular color by multiplying (0.8, 0.8, 1) * Kspec * (1 -
		// roughness)
		MathSN multSN = new MathSN(shadingNBS);
		multSN.setOperation(Operation.MULTIPLY);
		multSN.setInValue1(
				new ConstantSNV("vec3(0.8, 0.8, 1) * (1 - " + roughnessChannelSN.getOutValue().getName() + ")"));
		multSN.setInValue2(beckmannSN.getOutKspec());
		multSN.getOutValue().defineAsVector3f();
		shadingNBS.addNode(multSN);

		// calculate diffuse/specular by mixing diffuse color with specular color, using
		// factor from Fresnel
		MixSN mix = new MixSN(shadingNBS);
		mix.setBlend(Blend.MIX);
		mix.setInFactor(fresnel.getOutR());
		mix.setInValue1(lambertSN.getOutColor());
		mix.setInValue2(multSN.getOutValue());
		mix.getOutValue().defineAsVector3f();
		shadingNBS.addNode(mix);

		// clamp color to [0, 1) via generic node
		GenericSN clamp = new GenericSN(shadingNBS);
		ValueSNV clamped = new ValueSNV(clamp, "clamp").defineAsVector3f();
		clamp.setGLSL("vec3 " + clamped.getName() + " = clamp(" + mix.getOutValue().getName() + ", 0, 1);\n");
		shadingNBS.addNode(clamp);

		// calculate diffuse/specular/emission color by adding emission color to
		// diffuse/specular color
		MathSN add = new MathSN(shadingNBS, Operation.ADD);
		add.setInValue1(clamped);
		add.setInValue2(emissionSN.getOutColor());
		add.getOutValue().defineAsVector3f();
		shadingNBS.addNode(add);

		// calculate atmosphere color via atmosphere node
		AtmosphereSN atmosphereSN = new AtmosphereSN(shadingNBS);
		atmosphereSN.setInPosition(posSN.getOutColor());
		shadingNBS.addNode(atmosphereSN);

		// calculate diffuse/specular/emission/atmosphere color by mixing
		// diffuse/specular/emission color with atmosphere color via screen blend mode
		MixSN mixAtmSN = new MixSN(shadingNBS);
		mixAtmSN.setBlend(Blend.SCREEN);
		mixAtmSN.setInFactor(new ConstantSNV(0.5f));
		mixAtmSN.setInValue1(add.getOutValue());
		mixAtmSN.setInValue2(atmosphereSN.getOutColor());
		mixAtmSN.getOutValue().defineAsVector3f();
		shadingNBS.addNode(mixAtmSN);

		// set output color to diffuse/specular/emission/atmosphere color
		shadingNBS.getOutputNode().setInColor(mixAtmSN.getOutValue(), OutputSN.DEFAULT_OUTPUT);

		// compile shader
		try {
			shadingNBS.loadShaders();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		shadingNBS.check();
		shadingNBS.init();

		// link mesh to shader
		VertexDataObject vdo = new VertexDataObject();
		vdo.loadVertexData(shadingMesh, shadingNBS);
		shadingQuad = new Drawable(shadingNBS, vdo);

		// set model, view, and projection matrices to identity
		shadingNBS.setMVP(new Matrix4f(), new Matrix4f(), new Matrix4f());

		// create a set of shading resources based on the sampler slots required by the shader
		ShaderResource shadingResources = new ShaderResource(shadingNBS.getSamplerSlots());
		// add samplers to each slot
		shadingResources.setSamplerSlot("position", shadingFBO.getTarget("pos").getSampler());
		shadingResources.setSamplerSlot("normal", shadingFBO.getTarget("norm").getSampler());
		shadingResources.setSamplerSlot("color", shadingFBO.getTarget("color").getSampler());
		shadingResources.setSamplerSlot("emission", shadingFBO.getTarget("emission").getSampler());
		shadingResources.setSamplerSlot("roughness", shadingFBO.getTarget("roughness").getSampler());
		// specify that this set of samplers should be bound whenever shadingQuad is drawn
		shadingQuad.setShaderResource(shadingResources);
	}

	public void initEarth() {
		// load UV sphere mesh from .obj file
		Mesh earthMesh = new Mesh();
		try {
			OBJLoader.loadGameObjectData(earthMesh, "earth.obj", true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// shader for earth
		NodeBasedShader earthNBS = new NodeBasedShader();

		/*
		 * define shader inputs from mesh (position, normal, color, texture coordinate)
		 * and uniform (camera position):
		 * 
		 * in vec3 position;
		 * in vec3 normal;
		 * in vec3 color;
		 * in vec2 texture_coordinate;
		 * uniform vec3 camera_position;
		 */
		earthNBS.addInput(ShaderNodeValue.INPUT_POSITION, earthNBS.getInputNode().getOutPosition());
		earthNBS.addInput(ShaderNodeValue.INPUT_NORMAL, earthNBS.getInputNode().getOutNormal());
		earthNBS.addInput(ShaderNodeValue.INPUT_COLOR, earthNBS.getInputNode().getOutColor());
		earthNBS.addInput(ShaderNodeValue.INPUT_TEXTURE_COORDINATE, earthNBS.getInputNode().getOutTextureCoordinate());

		// specify that earthNBS will have an output for each target in shadingFBO
		earthNBS.getOutputNode().initOutputs(shadingFBO);

		/*
		 * define samplers used:
		 * 
		 * uniform sampler2D normal;
		 * uniform sampler2D diffuse;
		 * ...
		 * uniform sampler2D specular;
		 */
		SamplerSNV normalSNV = new SamplerSNV(null, "normal");
		earthNBS.addSamplerSlot("normal", normalSNV);

		SamplerSNV diffuseSNV = new SamplerSNV(null, "diffuse");
		earthNBS.addSamplerSlot("diffuse", diffuseSNV);

		SamplerSNV cloudSNV = new SamplerSNV(null, "clouds");
		earthNBS.addSamplerSlot("cloud", cloudSNV);

		SamplerSNV lightsSNV = new SamplerSNV(null, "lights");
		earthNBS.addSamplerSlot("lights", lightsSNV);

		SamplerSNV specularSNV = new SamplerSNV(null, "specular");
		earthNBS.addSamplerSlot("specular", specularSNV);

		// read normal from normal texture at texture_coordinate
		NormalMapConverterSN normalSN = new NormalMapConverterSN(earthNBS);
		normalSN.setMode(Mode.XZY);
		normalSN.setInSampler(normalSNV);
		normalSN.setInTextureCoordinate(earthNBS.getInputNode().getOutTextureCoordinate());
		earthNBS.addNode(normalSN);

		// multiply normal by (1, 1, -1)
		GenericSN fixNormalSN = new GenericSN(earthNBS);
		ValueSNV nSNV = new ValueSNV(fixNormalSN, "normal").defineAsVector3f();
		fixNormalSN
		.setGLSL("vec3 " + nSNV.getName() + " = " + normalSN.getOutNormal().getName() + " * vec3(1, 1, -1);\n");
		earthNBS.addNode(fixNormalSN);

		// read diffuse color from diffuse texture at texture_coordinate
		SamplerSN diffuseSN = new SamplerSN(earthNBS);
		diffuseSN.setInSampler(diffuseSNV);
		diffuseSN.setInTextureCoordinate(earthNBS.getInputNode().getOutTextureCoordinate());
		earthNBS.addNode(diffuseSN);

		// read cloud color from cloud texture at texture_coordinate
		SamplerSN cloudSN = new SamplerSN(earthNBS);
		cloudSN.setInSampler(cloudSNV);
		cloudSN.setInTextureCoordinate(earthNBS.getInputNode().getOutTextureCoordinate());
		earthNBS.addNode(cloudSN);

		// read the red channel of cloud color (converts RGB vector to float)
		RGBChannelSN cloudChannelSN = new RGBChannelSN(earthNBS);
		cloudChannelSN.setChannel(Channel.RED);
		cloudChannelSN.setInColor(cloudSN.getOutColor());
		earthNBS.addNode(cloudChannelSN);

		// calculate diffuse/cloud color by mixing diffuse color with white, using
		// factor from cloud value
		MixSN mixCloudSN = new MixSN(earthNBS);
		mixCloudSN.setInValue1(diffuseSN.getOutColor());
		mixCloudSN.setInValue2(new ConstantSNV("vec3(1,1,1)"));
		mixCloudSN.setInFactor(cloudChannelSN.getOutValue());
		mixCloudSN.getOutValue().defineAsVector3f();
		earthNBS.addNode(mixCloudSN);

		// read light color from lights texture at texture_coordinate
		SamplerSN lightsSN = new SamplerSN(earthNBS);
		lightsSN.setInSampler(lightsSNV);
		lightsSN.setInTextureCoordinate(earthNBS.getInputNode().getOutTextureCoordinate());
		earthNBS.addNode(lightsSN);

		// invert normal by multiplying normal * -1
		MathSN scaleSN = new MathSN(earthNBS, Operation.MULTIPLY);
		scaleSN.setInValue1(nSNV);
		scaleSN.setInValue2(new ConstantSNV(-1f));
		scaleSN.getOutValue().defineAsVector3f();
		earthNBS.addNode(scaleSN);

		// calculate light intensity via Lambert cosine node using inverted normal
		// (lights are only shown on the dark side)
		DiffuseSN lightLambertSN = new DiffuseSN(earthNBS);
		lightLambertSN.setInColor(lightsSN.getOutColor());
		lightLambertSN.setInNormal(scaleSN.getOutValue());
		earthNBS.addNode(lightLambertSN);

		// calculate visible lights by multiplying light intensity * (1 - clouds);
		// lights are only visible where there are no clouds
		MathSN mixLightsSN = new MathSN(earthNBS, Operation.MULTIPLY);
		mixLightsSN.setInValue1(lightLambertSN.getOutColor());
		mixLightsSN.setInValue2(new ConstantSNV("(1 - " + cloudChannelSN.getOutValue().getName() + ")"));
		mixLightsSN.getOutValue().defineAsVector3f();
		earthNBS.addNode(mixLightsSN);

		// read specularity from specular texture at texture_coordinate
		SamplerSN specularSN = new SamplerSN(earthNBS);
		specularSN.setInSampler(specularSNV);
		specularSN.setInTextureCoordinate(earthNBS.getInputNode().getOutTextureCoordinate());
		earthNBS.addNode(specularSN);

		// calculate roughness by adding specularity with cloud color and clamping to
		// [0, 1); clouds are not very specular
		GenericSN roughness = new GenericSN(earthNBS);
		ValueSNV roughnessSNV = new ValueSNV(roughness, "roughness").defineAsFloat();
		roughness.setGLSL("vec3 " + roughnessSNV.getName() + " = clamp(" + specularSN.getOutColor().getName() + " + "
				+ cloudSN.getOutColor().getName() + ", 0, 1);\n");
		earthNBS.addNode(roughness);

		/*
		 * set outputs: set pos output to world position set norm output to normal set
		 * color output to diffuse/cloud color set emission output to visible light
		 * color set roughness output to roughness
		 */
		earthNBS.getOutputNode().setInColor(earthNBS.getInputNode().getOutWorldPosition(), "pos");
		earthNBS.getOutputNode().setInColor(nSNV, "norm");
		earthNBS.getOutputNode().setInColor(mixCloudSN.getOutValue(), "color");
		earthNBS.getOutputNode().setInColor(mixLightsSN.getOutValue(), "emission");
		earthNBS.getOutputNode().setInColor(roughnessSNV, "roughness");

		// compile shader
		try {
			earthNBS.loadShaders();
		} catch (IOException e) {
			e.printStackTrace();
		}
		earthNBS.check();
		earthNBS.init();

		// link mesh to shader
		VertexDataObject vdo = new VertexDataObject();
		vdo.loadVertexData(earthMesh, earthNBS);
		earth = new GameObject(earthNBS, vdo);

		// calculate perspective projection matrix and apply to shader
		Matrix4f projection = Matrix4f.perspective(75, 16 / 9f, 0.01f, 10);
		earthNBS.setMVP(new Matrix4f(), new Matrix4f(), projection);

		// load normal, diffuse, cloud, lights, and specular textures from files
		SamplerMap normal = SamplerMap.load(Resource.TEX_DIR + "normal.png", SamplerMap.NORMAL_DEFAULT);
		SamplerMap diffuse = SamplerMap.load(Resource.TEX_DIR + "diffuse.png", SamplerMap.TEX_DEFAULT, true);
		SamplerMap cloud = SamplerMap.load(Resource.TEX_DIR + "clouds.png", 6, true);
		SamplerMap lights = SamplerMap.load(Resource.TEX_DIR + "emission.png", SamplerMap.EMISSION_DEFAULT, true);
		SamplerMap specular = SamplerMap.load(Resource.TEX_DIR + "specularity.png", SamplerMap.SPEC_DEFAULT);

		// create a set of shading resources based on the sampler slots required by the shader
		ShaderResource shadingResources = new ShaderResource(earthNBS.getSamplerSlots());
		// add samplers to each slot
		shadingResources.setSamplerSlot("normal", normal);
		shadingResources.setSamplerSlot("diffuse", diffuse);
		shadingResources.setSamplerSlot("cloud", cloud);
		shadingResources.setSamplerSlot("lights", lights);
		shadingResources.setSamplerSlot("specular", specular);
		// specify that this set of samplers should be bound whenever earth is drawn
		earth.setShaderResource(shadingResources);
	}

	public void initSkybox() {
		NodeBasedShader skyboxNBS = new NodeBasedShader();
		Mesh skyboxMesh = new Mesh();
		ArrayList<Vertex> verts = new ArrayList<Vertex>();

		// create cube mesh for skybox
		verts.add(new Vertex(new Vector3f(-1, -1, -1), null, null, null));
		verts.add(new Vertex(new Vector3f(1, -1, -1), null, null, null));
		verts.add(new Vertex(new Vector3f(1, 1, -1), null, null, null));
		verts.add(new Vertex(new Vector3f(-1, 1, -1), null, null, null));
		verts.add(new Vertex(new Vector3f(-1, -1, 1), null, null, null));
		verts.add(new Vertex(new Vector3f(1, -1, 1), null, null, null));
		verts.add(new Vertex(new Vector3f(1, 1, 1), null, null, null));
		verts.add(new Vertex(new Vector3f(-1, 1, 1), null, null, null));
		skyboxMesh.loadVertices(verts);

		Integer[] index_array = { 0, 1, 2, 3, 0, 2, 4, 5, 6, 7, 4, 6, 4, 0, 3, 7, 4, 3, 1, 5, 6, 2, 1, 6, 3, 2, 6, 7, 3,
				6, 4, 5, 1, 0, 4, 1 };
		ArrayList<Integer> indices = new ArrayList<Integer>(Arrays.asList(index_array));
		skyboxMesh.loadIndices(indices);

		skyboxNBS.getOutputNode().initOutputs(shadingFBO);

		skyboxNBS.addInput(ShaderNodeValue.INPUT_POSITION, skyboxNBS.getInputNode().getOutPosition());

		/*
		 * define sampler used:
		 * 
		 * uniform samplerCube sky;
		 */
		SamplerCubeSNV skySNV = new SamplerCubeSNV(null, "sky");
		skyboxNBS.addSamplerSlot("sky", skySNV);

		// read sky color from sky sampler cube at position vector
		SamplerCubeSN skySN = new SamplerCubeSN(skyboxNBS);
		skySN.setInSamplerCube(skySNV);
		skySN.setInVector(skyboxNBS.getInputNode().getOutPosition());
		skyboxNBS.addNode(skySN);

		/*
		 * set outputs: set pos output to world position set emission output to skybox
		 * color set all other outputs to 0
		 */
		skyboxNBS.getOutputNode().setInColor(skyboxNBS.getInputNode().getOutWorldPosition(), "pos");
		skyboxNBS.getOutputNode().setInColor(new ConstantSNV("vec3(0)"), "norm");
		skyboxNBS.getOutputNode().setInColor(new ConstantSNV("vec3(0)"), "color");
		skyboxNBS.getOutputNode().setInColor(skySN.getOutColor(), "emission");
		skyboxNBS.getOutputNode().setInColor(new ConstantSNV("vec3(0)"), "roughness");

		try {
			skyboxNBS.loadShaders();
		} catch (IOException e) {
			e.printStackTrace();
		}
		skyboxNBS.check();
		skyboxNBS.init();

		VertexDataObject vdo = new VertexDataObject();
		vdo.loadVertexData(skyboxMesh, skyboxNBS);
		skybox = new Drawable(skyboxNBS, vdo);

		float ratio = 1;
		Matrix4f projection = Matrix4f.perspective(75, 16 / 9f, 0.01f, 10);
		skyboxNBS.setMVP(new Matrix4f(), new Matrix4f(), projection);

		// skybox is a 4 x 4 x 4 cube
		skybox.scale(2, 2, 2);

		// load skybox textures
		String[] paths = new String[6];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = Resource.TEX_DIR + "sky" + i + ".png";
		}
		SamplerCube sky = SamplerCube.load(paths, 0, true);
		ShaderResource shadingResources = new ShaderResource(skyboxNBS.getSamplerSlots());
		shadingResources.setSamplerSlot("sky", sky);
		skybox.setShaderResource(shadingResources);
	}

	public void initPost1() {
		postFBO1 = FramebufferObject.createFramebuffer(super.getWidth(), super.getHeight(), 4);

		NodeBasedShader postNBS1 = new NodeBasedShader();
		Mesh postMesh1 = MeshFactory.createSquare(null);

		postNBS1.addInput(ShaderNodeValue.INPUT_POSITION, postNBS1.getInputNode().getOutPosition());
		postNBS1.addInput(ShaderNodeValue.INPUT_TEXTURE_COORDINATE, postNBS1.getInputNode().getOutTextureCoordinate());
		postNBS1.addUniform(ShaderNodeValue.UNIFORM_CAMERA_POSITION, new PositionSNV(null, "cameraPos"));

		SamplerSNV textureSNV = new SamplerSNV(null, "texture");
		postNBS1.addSamplerSlot("texture", textureSNV);

		SamplerSN textureSN = new SamplerSN(postNBS1);
		textureSN.setInSampler(textureSNV);
		textureSN.setInTextureCoordinate(postNBS1.getInputNode().getOutTextureCoordinate());
		postNBS1.addNode(textureSN);

		// calculate first blur pass using fast Gaussian blur node; set radius to 5px, direction to Y (0, 1)
		FastGaussianSN blur = new FastGaussianSN(postNBS1);
		blur.setInSampler(textureSNV);
		blur.setInTextureCoordinate(postNBS1.getInputNode().getOutTextureCoordinate());
		ValueSNV radius = new ValueSNV(null, "radius").defineAsFloat(5);
		postNBS1.addConstant(radius);
		blur.setInRadius(radius);
		ValueSNV resolution = new ValueSNV(null, "resolution");
		resolution.defineAsFloat(super.getHeight());
		postNBS1.addConstant(resolution);
		blur.setInResolution(resolution);
		ValueSNV direction = new ValueSNV(null, "direction");
		direction.defineAsVector2f(new Vector2f(0, 1));
		postNBS1.addConstant(direction);
		blur.setInDirection(direction);
		postNBS1.addNode(blur);

		postNBS1.getOutputNode().setInColor(blur.getOutColor(), OutputSN.DEFAULT_OUTPUT);

		try {
			postNBS1.loadShaders();
		} catch (IOException e) {
			e.printStackTrace();
		}
		postNBS1.check();
		postNBS1.init();

		VertexDataObject vdo = new VertexDataObject();
		vdo.loadVertexData(postMesh1, postNBS1);
		postQuad1 = new Drawable(postNBS1, vdo);

		postNBS1.setMVP(new Matrix4f(), new Matrix4f(), new Matrix4f());

		ShaderResource shaderResource = new ShaderResource(postNBS1.getSamplerSlots());
		shaderResource.setSamplerSlot("texture", postFBO1.getTarget(FramebufferObject.FBO_TARGET_TEXTURE).getSampler());
		postQuad1.setShaderResource(shaderResource);
	}

	public void initPost2() {
		postFBO2 = FramebufferObject.createFramebuffer(super.getWidth(), super.getHeight(), 0);

		NodeBasedShader postNBS2 = new NodeBasedShader();
		Mesh postMesh2 = MeshFactory.createSquare(null);

		postNBS2.addInput(ShaderNodeValue.INPUT_POSITION, postNBS2.getInputNode().getOutPosition());
		postNBS2.addInput(ShaderNodeValue.INPUT_TEXTURE_COORDINATE, postNBS2.getInputNode().getOutTextureCoordinate());
		postNBS2.addUniform(ShaderNodeValue.UNIFORM_CAMERA_POSITION, new PositionSNV(null, "cameraPos"));

		SamplerSNV textureSNV = new SamplerSNV(null, "texture");
		postNBS2.addSamplerSlot("texture", textureSNV);

		SamplerSN textureSN = new SamplerSN(postNBS2);
		textureSN.setInSampler(textureSNV);
		textureSN.setInTextureCoordinate(postNBS2.getInputNode().getOutTextureCoordinate());
		postNBS2.addNode(textureSN);

		// calculate second blur pass using fast Gaussian blur node; set radius to 5px, direction to X (1, 0)
		FastGaussianSN blur = new FastGaussianSN(postNBS2);
		blur.setInSampler(textureSNV);
		blur.setInTextureCoordinate(postNBS2.getInputNode().getOutTextureCoordinate());
		ValueSNV radius = new ValueSNV(null, "radius").defineAsFloat(5);
		postNBS2.addConstant(radius);
		blur.setInRadius(radius);
		ValueSNV resolution = new ValueSNV(null, "resolution");
		resolution.defineAsFloat(super.getWidth());
		postNBS2.addConstant(resolution);
		blur.setInResolution(resolution);
		ValueSNV direction = new ValueSNV(null, "direction");
		direction.defineAsVector2f(new Vector2f(1, 0));
		postNBS2.addConstant(direction);
		blur.setInDirection(direction);
		postNBS2.addNode(blur);

		SamplerSNV texture1SNV = new SamplerSNV(null, "texture1");
		postNBS2.addSamplerSlot("texture1", texture1SNV);

		// read texture from first pass
		SamplerSN texture1SN = new SamplerSN(postNBS2);
		texture1SN.setInSampler(texture1SNV);
		texture1SN.setInTextureCoordinate(postNBS2.getInputNode().getOutTextureCoordinate());
		postNBS2.addNode(texture1SN);

		// mix original texture with blurred texture
		MixSN mixSN = new MixSN(postNBS2, Blend.MIX);
		mixSN.setInValue1(blur.getOutColor());
		mixSN.setInValue2(texture1SN.getOutColor());
		ValueSNV mixFac = new ValueSNV(null, "mixFac").defineAsFloat(0.6f);
		postNBS2.addConstant(mixFac);
		mixSN.setInFactor(mixFac);
		mixSN.getOutValue().defineAsVector3f();
		postNBS2.addNode(mixSN);

		// gamma correction
		GenericSN gammaSN = new GenericSN(postNBS2);
		ValueSNV corrected = new ValueSNV(gammaSN, "corrected").defineAsVector3f();
		gammaSN.setGLSL("vec3 " + corrected.getName() + " = pow(" + mixSN.getOutValue().getName() + ", vec3(1.0/2.2));\n");
		postNBS2.addNode(gammaSN);

		postNBS2.getOutputNode().setInColor(corrected, OutputSN.DEFAULT_OUTPUT);

		try {
			postNBS2.loadShaders();
		} catch (IOException e) {
			e.printStackTrace();
		}
		postNBS2.check();
		postNBS2.init();

		VertexDataObject vdo = new VertexDataObject();
		vdo.loadVertexData(postMesh2, postNBS2);
		postQuad2 = new Drawable(postNBS2, vdo);

		postNBS2.setMVP(new Matrix4f(), new Matrix4f(), new Matrix4f());

		ShaderResource shaderResource = new ShaderResource(postNBS2.getSamplerSlots());
		shaderResource.setSamplerSlot("texture", postFBO2.getTarget(FramebufferObject.FBO_TARGET_TEXTURE).getSampler());
		shaderResource.setSamplerSlot("texture1", postFBO1.getTarget(FramebufferObject.FBO_TARGET_TEXTURE).getSampler());
		postQuad2.setShaderResource(shaderResource);
	}

	@Override
	public void loop() {
		super.loop();

		// handle input
		input();

		// update camera
		cam.update();

		// update UBO lighting info from scene
		NodeBasedShader.updateUBO(scene);

		// bind shading framebuffer and render scene objects (earth, skybox)
		shadingFBO.bind();
		shadingFBO.clear();

		// turn off depth mask writing so skybox can't occlude any scene objects
		glDepthMask(false);
		// translate skybox to be centered on camera and draw
		skybox.translate(cam.getPos().x - skybox.getPos().x, skybox.getPos().y - cam.getPos().y,
				skybox.getPos().z - cam.getPos().z);
		skybox.update(scene);
		skybox.draw();
		glDepthMask(true);

		// rotate earth and draw
		earth.rotate((float) delta * 3f, 0, 1, 0);
		earth.update(scene);
		earth.draw();

		shadingFBO.unbind();

		// bind first postprocessing pass framebuffer
		postFBO1.bind();
		postFBO1.clear();
		// pass camera information to deferred shader (should really be part of the UBO)
		shadingQuad.os.getShader().bind();
		shadingQuad.os.getShader().setUniformVec3f(
				((NodeBasedShader) shadingQuad.os).getUniforms().get(ShaderNodeValue.UNIFORM_CAMERA_POSITION).getName(),
				scene.getCamera().getPos());
		shadingQuad.draw();
		postFBO1.unbind();

		// bind second postprocessing pass framebuffer; draw output of first pass
		postFBO2.bind();
		postFBO2.clear();
		postQuad1.draw();
		postFBO2.unbind();

		// draw output of second pass onto screen
		postQuad2.draw();


		// create text and draw
		Text text = new Text(String.format("FPS: %.2f", 1 / delta), ttf);
		text.translate(10, -10, -1);
		text.draw();
	}

	public void input() {
		// KEYBOARD

		Vector3f velocity = new Vector3f();

		// set velocity to 1 if shift is down, 0.5 otherwise
		float v = 0.5f;
		if (window.getKey(GLFW_KEY_LEFT_SHIFT) == 1)
			v = 1f;

		// add to velocity in each direction if key is down
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
		cam.setVelocity(velocity);

		// MOUSE

		DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
		window.getCursorPos(x, y);
		// rotate camera based on mouse position
		cam.rotateAxes((float) (x.get(0) - super.getWidth() / 2) / 4, 0, 1, 0, null);
		cam.rotate((float) (y.get(0) - super.getHeight() / 2) / 4, 1, 0, 0);

		// return mouse to center
		window.setCursorPos((double) super.getWidth() / 2, (double) super.getHeight() / 2);
	}

}
