/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;

import engine.graphics.Renderer;
import engine.io.Input;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Timur
 */
public class UICheckBox extends UIComponent{
    public static final int SIZE = 25;
    
    private boolean state = false;
    
    ArrayList<ActionListener> listeners = new ArrayList<>();
    private ActionEvent event= new ActionEvent((Object) this, 1001, "cut");
    
    public UICheckBox(){
        super.WIDTH = SIZE;
        super.HEIGHT = SIZE;
        super.color = Color3f.WHITE;
    }
    
    public boolean getState(){
        return this.state;
    }
    
    @Override
    public void create(UIConstraints constraints){
        update(constraints);
    }
    
    @Override
    public void update(UIConstraints constraints){
        if (constraints.stateChanged) {
            float widthNDC = (float)this.WIDTH / constraints.getWindowWidth();
            float heightNDC = (float)this.HEIGHT / constraints.getWindowHeight();
            int width;
            int height;

            x = constraints.getXSS();
            y = constraints.getYSS();

            widthNDC = constraints.getWidth() < widthNDC ? constraints.getWidth() : widthNDC;
            heightNDC = constraints.getHeight() < heightNDC ? constraints.getHeight() : heightNDC;
            width = constraints.getWidthSS() < WIDTH ? constraints.getWidthSS() : WIDTH;
            height = constraints.getHeightSS() < HEIGHT ? constraints.getHeightSS() : HEIGHT;

            super.setRawModel(constraints.getX(), constraints.getY(), widthNDC, heightNDC);
            super.setBoundingBox(x, y, width, height);
        }
        if (isClicked()) {
            state = !state;
            notifyListeners();
        }
        if (state) super.color = Color3f.MIDDLE_BLUE;
        else super.color = Color3f.WHITE;
    }
    
    @Override
    public void render(Renderer renderer){
        renderer.renderGuiElement(rawModel, color);
    }
    
    @Override
    public void destroy(){
        rawModel.destroy();
    }
    
    private void notifyListeners() {
        for (ActionListener listener:listeners){
            listener.actionPerformed(event);
        }
    }
    
    public void addActionListener(ActionListener listener){
        this.listeners.add(listener);
    }
}
