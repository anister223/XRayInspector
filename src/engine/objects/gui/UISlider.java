/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;

import engine.graphics.Renderer;
import engine.io.Input;
import static engine.math.Util.Clamp;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Timur
 */
public class UISlider extends UIComponent{
    private float value;
    private boolean grabbed;
    public static final int MARKER_SIZE = 25;
    public static final int LINE_THICKNESS = 10;
    
    private float maxValue;
    
    private UIBlock line;
    
    ArrayList<ActionListener> listeners = new ArrayList<>();
    private ActionEvent event= new ActionEvent((Object) this, 1001, "cut");
    
    public UISlider(){
        super.WIDTH = MARKER_SIZE;
        super.HEIGHT = MARKER_SIZE;
        super.color = Color3f.WHITE;
        
        line = new UIBlock(Color3f.RED);
    }
    
    public float getValue(){
        return (this.value + maxValue / 2) / maxValue;
    }
    
    @Override
    public void create(UIConstraints constraints){
        update(constraints);
    }

    @Override
    public void update(UIConstraints constraints) {
        if (constraints.stateChanged || grabbed) {
            maxValue = constraints.getWidth();
            
            //Marker setup
            float widthNDC = (float)UISlider.MARKER_SIZE / constraints.getWindowWidth();
            float heightNDC = (float)UISlider.MARKER_SIZE / constraints.getWindowHeight();
            int width;
            int height;

            //widthNDC = constraints.getWidth() < widthNDC ? constraints.getWidth() : widthNDC;
            //heightNDC = constraints.getHeight() < heightNDC ? constraints.getHeight() : heightNDC;
            width = MARKER_SIZE; //constraints.getWidthSS() < MARKER_SIZE ? constraints.getWidthSS() : MARKER_SIZE;
            height = MARKER_SIZE; //constraints.getHeightSS() < MARKER_SIZE ? constraints.getHeightSS() : MARKER_SIZE;

            super.setRawModel(constraints.getX() + value * 2f, constraints.getY(), widthNDC, heightNDC);
            super.setBoundingBox(constraints.getXSS() + (int)(value * constraints.getWindowWidth()), constraints.getYSS(), width, height);
            
            //Line setup
            heightNDC = (float)UISlider.LINE_THICKNESS / constraints.getWindowHeight();

            heightNDC = constraints.getHeight() < heightNDC ? constraints.getHeight() : heightNDC;
            height = constraints.getHeightSS() < LINE_THICKNESS ? constraints.getHeightSS() : LINE_THICKNESS;
            
            line.setRawModel(constraints.getX(), constraints.getY(), constraints.getWidth(), heightNDC);
            line.setBoundingBox(constraints.getXSS(), constraints.getYSS(), constraints.getWidthSS(), height);
        }
        isGrabbed();
        if(grabbed){
            float xx = (float)(Input.getMouseX() - constraints.getXSS()) / constraints.getWindowWidth();
            this.value = Clamp(xx, -maxValue / 2, maxValue / 2);
            notifyListeners();
        }
    }
    
    @Override
    public void render(Renderer renderer){
        renderer.renderGuiElement(line.getRawModel(), line.getColor());
        renderer.renderGuiElement(rawModel, color);
    }
    
    @Override
    public void destroy(){
        rawModel.destroy();
    }

    private boolean isGrabbed() {
        if(inBound()){
            if(super.isHoldOn(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
                grabbed = true;
                return true;
            }else {
                grabbed = false;
                return false;
            }
        }else {
            if (!super.isHoldOn(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                grabbed = false;
            }
            return false;
        }
    }
    
    public void addActionListener(ActionListener listener){
        this.listeners.add(listener);
    }
    
    private void notifyListeners() {
        for (ActionListener listener:listeners){
            listener.actionPerformed(event);
        }
    }
}
