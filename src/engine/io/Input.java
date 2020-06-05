/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.io;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

/**
 *
 * @author Timur
 */
public class Input {
    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX, mouseY;
    private static double scrollX, scrollY;
    
    private GLFWKeyCallback keyboard;
    private GLFWCursorPosCallback mouseMove;
    private GLFWMouseButtonCallback mouseButtons;
    private GLFWScrollCallback mouseScroll;
    
    public Input(){
        keyboard = new GLFWKeyCallback(){
            public void invoke(long window, int key, int scancode, int action, int mods){
                keys[key] = (action != GLFW.GLFW_RELEASE);
            }
        };
        mouseMove = new GLFWCursorPosCallback(){
            public void invoke(long window, double xpos, double ypos){
                mouseX = xpos;
                mouseY = ypos;
            }
        };
        mouseButtons = new GLFWMouseButtonCallback(){
            public void invoke(long window,int button, int action, int mods){
                buttons[button] = (action != GLFW.GLFW_RELEASE);
            }
        };
        
        mouseScroll = new GLFWScrollCallback(){
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scrollX += xoffset;
                scrollY += yoffset;
            }
            
        };
    }
    
    public static boolean isKeyDown(int key){
        return keys[key];
    }
    public static boolean isButtonDown(int button){
        return buttons[button];
    }
    
    public  void destroy(){
        keyboard.free();
        mouseMove.free();
        mouseButtons.free();
    }

    /**
     * @return the mouseX
     */
    public static double getMouseX() {
        return mouseX;
    }

    /**
     * @return the mouseY
     */
    public static double getMouseY() {
        return mouseY;
    }

    public static double getScrollX() {
        return scrollX;
    }
    
    public static double getScrollY() {
        return scrollY;
    }
    
    /**
     * @return the keyboard
     */
    public GLFWKeyCallback getKeyboardCallback() {
        return keyboard;
    }

    /**
     * @return the mouseMove
     */
    public GLFWCursorPosCallback getMouseMoveCallback() {
        return mouseMove;
    }

    /**
     * @return the mouseButtons
     */
    public GLFWMouseButtonCallback getMouseButtonsCallback() {
        return mouseButtons;
    }
    
    public GLFWScrollCallback getMouseScrollCallback() {
        return mouseScroll;
    }
}
