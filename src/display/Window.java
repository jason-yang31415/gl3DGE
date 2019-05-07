package display;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_COMPAT_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

import game.Screen;

public class Window {

	final long id;

	private int WIDTH;
	private int HEIGHT;
	private boolean fullscreen;

	private GLFWKeyCallback keyCallback;

	private Map<String, Screen> screens = new HashMap<String, Screen>();
	private String screenKey;
	private Screen screen;

	public Window(int WIDTH, int HEIGHT, String title, boolean resizable, int version_major, int version_minor){
		this(WIDTH, HEIGHT, title, resizable, version_major, version_minor, false);
	}

	public Window(int WIDTH, int HEIGHT, String title, boolean resizable, int version_major, int version_minor, boolean fullscreen){
		if (glfwInit() != true)
			throw new IllegalStateException("Unable to initialize GLFW");

		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, (resizable ? GL_TRUE : GL_FALSE));

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, version_major);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, version_minor);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_FALSE);

		// DEBUG
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

		if (fullscreen)
			id = glfwCreateWindow(WIDTH, HEIGHT, title, GLFW.glfwGetPrimaryMonitor(), NULL);
		else
			id = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);

		if (id == NULL)
			throw new RuntimeException("Failed to create GLFW window");

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(id,
				(vidmode.width() - WIDTH) / 2,
				(vidmode.height() - HEIGHT) / 2
				);

		GLFW.glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallbackI(){

			@Override
			public void invoke(long window, int width, int height) {
				setSize(width, height);
			}

		});

		glfwMakeContextCurrent(id);
		glfwSwapInterval(1);

		glfwShowWindow(id);
	}

	public void addScreen(String key, Screen screen){
		screens.put(key, screen);
	}

	public void navigate(Screen screen){
		if (this.screen != null)
			this.screen.onNavigateFrom();
		this.screen = screen;
		this.screen.onNavigateTo();
	}

	public void navigate(String key){
		this.screen = screens.get(key);
	}

	public void loop(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		screen.loop();

		swapBuffers();
		pollEvents();
	}

	public long getID(){
		return id;
	}

	public void setSize(int WIDTH, int HEIGHT){
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
	}

	public int getWidth(){
		return WIDTH;
	}

	public int getHeight(){
		return HEIGHT;
	}

	public void setFullscreen(boolean fullscreen){
		this.fullscreen = fullscreen;
		if (fullscreen){
			glfwSetWindowMonitor(getID(), glfwGetPrimaryMonitor(), 0, 0, getWidth(), getHeight(), GLFW.GLFW_DONT_CARE);
		}
		else {
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowMonitor(getID(), NULL, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2, getWidth(), getHeight(), GLFW.GLFW_DONT_CARE);
		}
	}

	public boolean getFullscreen(){
		return fullscreen;
	}

	public static void setErrorCallback(GLFWErrorCallback ecb){
		glfwSetErrorCallback(ecb);
	}

	public void setKeyCallback(GLFWKeyCallback kcb){
		glfwSetKeyCallback(id, keyCallback = kcb);
	}

	public void releaseErrorCallback(){
		glfwSetErrorCallback(null).free();
	}

	public void releaseCallbacks(){
		keyCallback.free();
	}

	public void destroyWindow(){
		screen.destroy();
		glfwDestroyWindow(id);
	}

	public boolean shouldClose(){
		return glfwWindowShouldClose(id);
	}

	public void swapBuffers(){
		glfwSwapBuffers(id);
	}

	public void pollEvents(){
		glfwPollEvents();
	}

	public int getKey(int key){
		return glfwGetKey(id, key);
	}

	public void getCursorPos(DoubleBuffer xpos, DoubleBuffer ypos){
		glfwGetCursorPos(id, xpos, ypos);
	}

	public void setCursorPos(Double xpos, Double ypos){
		glfwSetCursorPos(id, xpos, ypos);
	}

}
