package display;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
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
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;

public class Window {

	final long id;
	
	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	
	public Window(int WIDTH, int HEIGHT, String title, boolean resizable, int version_major, int version_minor){
		if (glfwInit() != true)
			throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, (resizable ? GL_TRUE : GL_FALSE));
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, version_major);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, version_minor);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		
		id = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);
		if (id == NULL)
			throw new RuntimeException("Failed to create GLFW window");
		
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		glfwSetWindowPos(id, 
			(vidmode.width() - WIDTH) / 2,
			(vidmode.height() - HEIGHT) / 2
		);
		
		
		glfwMakeContextCurrent(id);
		glfwSwapInterval(1);
		
		glfwShowWindow(id);
	}
	
	public void setErrorCallback(GLFWErrorCallback ecb){
		glfwSetErrorCallback(errorCallback = ecb);
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
