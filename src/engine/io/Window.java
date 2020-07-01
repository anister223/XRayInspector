/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.io;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowMaximizeCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Timur
 */
public class Window {
    private int width, height;
    private String title;
    private long window;
    public Input input;
    private Vector3f background = new Vector3f(0,0,0);
    private GLFWWindowSizeCallback sizeCallback;
    private GLFWWindowMaximizeCallback maxCallback;
    private boolean isResized;
    private Matrix4f projection;
    
    private float FOV = 70.0f;
    
    public static long time;
    
    public int frames;
    
    public Window(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
        //projection = Matrix4f.projection(FOV, (float)width / (float) height, 0.1f, 1000.0f);
        projection = Matrix4f.orthoProjection((float)width / (float) height, 0.1f, 3.5f);
    }
    
    public void create() {
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if(!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        
        input = new Input();
        window = GLFW.glfwCreateWindow(width, height, title, 0,0);
        
        if (window ==0){
            System.err.println("ERROR: Window wasn't created");
            return;
        }
        
        // Get the resolution of the primary monitor
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        // Center the window
        GLFW.glfwSetWindowPos(window, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
        
        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);
        
        // Enable v-sync
        GLFW.glfwSwapInterval(1);
        
        // Make the window visible
        GLFW.glfwShowWindow(window);
        
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        
        // Set the clear color
        GL11.glClearColor(background.getX(),background.getY(),background.getZ(), 1.0f);
        
        createCallbacks();
        
        time = System.currentTimeMillis();
    }
    
    private void createCallbacks(){
        sizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                if (w < 800) width = 800;
                else width = w;
                if (h < 600) height = 600;
                else height = h;
                GLFW.glfwSetWindowSize(window, width, height);
                isResized = true;
            }
        };
        
        maxCallback = new GLFWWindowMaximizeCallback() {
            @Override
            public void invoke(long window, boolean maximized) {
                isResized = true;
            }
        };
        
        GLFW.glfwSetKeyCallback(window, input.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonsCallback());
        GLFW.glfwSetScrollCallback(window, input.getMouseScrollCallback());
        GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
        GLFW.glfwSetWindowMaximizeCallback(window, maxCallback);
    }
    
    public void update(){
        if(isResized){
            GL11.glViewport(0,0, width, height);
            projection = Matrix4f.orthoProjection((float)width / (float) height, 0.1f, 3.5f);
            isResized = false;
        }
        
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        GLFW.glfwPollEvents();
        //GLFW.glfwWaitEvents();
        
        // Frames per second counter
        frames++;
        if(System.currentTimeMillis() > time + 1000){
            GLFW.glfwSetWindowTitle(window, title + "| FPS: " + frames);
            time = System.currentTimeMillis();
            frames = 0;
        }
    }
    
    public void swapBuffers(){
        GLFW.glfwSwapBuffers(window); // swap the color buffers
    }
    
    public boolean shouldClose(){
        return GLFW.glfwWindowShouldClose(window);
    }
    
    public boolean isResized(){
        return isResized;
    }
    
    public void destroy(){
        GLFW.glfwWindowShouldClose(window);
        input.destroy();
        
        // Free the window callbacks and destroy the window
        sizeCallback.free();
        maxCallback.free();
        GLFW.glfwDestroyWindow(window);
        
        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate();
        //GLFW.glfwSetErrorCallback(null).free();
    }
    
    public void setBackgroundColor(float r, float g, float b){
        background.set(r, g, b);
    }

    public void mouseState(boolean lock) {
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, lock ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }
    
    public float getFOV() {
        return FOV;
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }
    
    public Matrix4f getProjectionMatrix() {
        return projection;
    }
}
