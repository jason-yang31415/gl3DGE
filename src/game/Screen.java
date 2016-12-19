package game;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

import display.Window;

public abstract class Screen {

	// WINDOW
	public Window window;
	
	// FPS
	public double delta;
	public double lastTime = 0;
	public double fps;
	
	public Screen(int WIDTH, int HEIGHT, Window window){
		this.window = window;
		
		init();
	}
	
	public abstract void init();
	
	public void loop(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		getDelta();
	}
	
	public int getWidth(){
		return window.getWidth();
	}
	
	public int getHeight(){
		return window.getHeight();
	}
	
	public double getDelta(){
		delta = glfwGetTime() - lastTime;
		lastTime = glfwGetTime();
		fps = 1 / delta;
		return delta;
	}
	
	public double getFPS(){
		return fps;
	}
	
}
