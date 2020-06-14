/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;

import engine.graphics.RawModel;
import engine.graphics.Renderer;
import engine.graphics.Vertex;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2f;
import engine.math.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Timur
 */
public class UIBlock extends UIComponent{
    private int width, height;
    
    public UIBlock(Vector3f color){
        this.WIDTH = 32767;
        this.HEIGHT = 32767;
        super.color = color;
    }
    
    public UIBlock(int width, int height, Vector3f color){
        this.WIDTH = width;
        this.HEIGHT = height;
        super.color = color;
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

            widthNDC = constraints.getWidth() < widthNDC ? constraints.getWidth() : widthNDC;
            heightNDC = constraints.getHeight() < heightNDC ? constraints.getHeight() : heightNDC;
            width = constraints.getWidthSS() < WIDTH ? constraints.getWidthSS() : WIDTH;
            height = constraints.getHeightSS() < HEIGHT ? constraints.getHeightSS() : HEIGHT;

            super.setRawModel(constraints.getX(), constraints.getY(), widthNDC, heightNDC);
            super.setBoundingBox(constraints.getXSS(), constraints.getYSS(), width, height);
        }
    }
    
    @Override
    public void render(Renderer renderer){
        renderer.renderGuiElement(rawModel, color);
    }
    
    @Override
    public void destroy(){
        rawModel.destroy();
    }
}
