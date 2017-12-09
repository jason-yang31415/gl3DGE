package game;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import display.Window;

public abstract class Screen {

	// WINDOW
	public Window window;

	// FPS
	public double delta;
	public double lastTime = 0;
	public double fps;

	public Screen(Window window){
		this.window = window;

		init();
	}

	public abstract void init();

	public void loop(){
		getDelta();
	}

	public void onNavigateFrom(){

	}

	public void onNavigateTo(){

	}

	public void destroy(){

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
