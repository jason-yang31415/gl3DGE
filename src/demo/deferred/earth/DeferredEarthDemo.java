package demo.deferred.earth;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import audio.AudioEngine;
import display.Window;
import game.Screen;

public class DeferredEarthDemo {

	Window w;

	public static int WIDTH, HEIGHT;

	Screen screen;

	public static void main(String[] args){
		new DeferredEarthDemo().run();
	}

	public void run(){
		System.out.println("LWGJL version " + Version.getVersion());

		try {
			init();
			loop();

			// release window and key callback
			w.destroyWindow();
			//keyCallback.release();
			w.releaseCallbacks();
		} finally {
			// terminate glfw
			glfwTerminate();
			// release error callback
			//errorCallback.release();
			w.releaseErrorCallback();

			AudioEngine.destroyAudioEngine();
		}
	}

	public void init(){
		WIDTH = 1920;
		HEIGHT = 1080;

		Window.setErrorCallback(GLFWErrorCallback.createPrint(System.err));
		w = new Window(WIDTH, HEIGHT, "gl3DGE", true, 4, 0);
		w.setKeyCallback(new GLFWKeyCallback(){

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				// TODO Auto-generated method stub
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, true);
			}

		});

		w.setCursorPos((double) WIDTH / 2, (double) HEIGHT / 2);

		AudioEngine.initAudioEngine();
	}

	public void loop(){
		// very important for unspecified reason
		// do not use gl functions before here
		GL.createCapabilities();

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		//glBlendFunc(GL_ONE, GL_ONE);

		//glClearColor(1f, 1f, 1f, 1f);
		glClearColor(0, 0, 0, 1);

		screen = new MainScreen(w);
		w.navigate(screen);

		while(!w.shouldClose()){
			w.loop();
		}
	}

}
