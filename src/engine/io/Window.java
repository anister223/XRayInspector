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
        if(!GLFW.glfwInit()){
            System.err.println("ERROR: GLFW wasn't initializied");
            return;
        }
        input = new Input();
        window = GLFW.glfwCreateWindow(width, height, title, 0,0);
        
        if (window ==0){
            System.err.println("ERROR: Window wasn't created");
            return;
        }
        
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        createCallbacks();
        
        GLFW.glfwShowWindow(window);
        
        GLFW.glfwSwapInterval(1);
        
        time = System.currentTimeMillis();
    }
    
    private void createCallbacks(){
        sizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                isResized = true;
            }
        };
        
        GLFW.glfwSetKeyCallback(window, input.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonsCallback());
        GLFW.glfwSetScrollCallback(window, input.getMouseScrollCallback());
        GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
    }
    
    public void update(){
        if(isResized){
            GL11.glViewport(0,0, width, height);
            projection = Matrix4f.orthoProjection((float)width / (float) height, 0.1f, 3.5f);
            isResized = false;
        }
        //GL11.glViewport(0, 0, width, height);
        GL11.glClearColor(background.getX(),background.getY(),background.getZ(), 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
        GLFW.glfwPollEvents();
        // Window display
        frames++;
        if(System.currentTimeMillis() > time + 1000){
            GLFW.glfwSetWindowTitle(window, title + "| FPS: " + frames);
            time = System.currentTimeMillis();
            frames = 0;
        }
    }
    
    public void swapBuffers(){
        GLFW.glfwSwapBuffers(window);
    }
    
    public boolean shouldClose(){
        return GLFW.glfwWindowShouldClose(window);
    }
    
    public boolean isResized(){
        return isResized;
    }
    
    public void destroy(){
        input.destroy();
        sizeCallback.free();
        GLFW.glfwWindowShouldClose(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
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
