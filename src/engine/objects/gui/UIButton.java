/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;

import engine.graphics.Renderer;
import engine.math.Vector3f;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Timur
 */
public class UIButton extends UIComponent{
    ArrayList<ActionListener> listeners = new ArrayList<>();
    private ActionEvent event= new ActionEvent((Object) this, 1001, "cut");
    private StbTtFontResource font;
    
    private int x, y;
    
    private String text;
    
    public UIButton(int width, int height, Vector3f color){
        super.WIDTH = width;
        super.HEIGHT = height;
        super.color = color;
        this.font = new StbTtFontResource(new File("C:\\Windows\\Fonts\\times.ttf"), 24);
    }
    
    public void setText(String text){
        this.text = text;
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

            widthNDC = constraints.getWidth() < widthNDC ? constraints.getWidth() : widthNDC;
            heightNDC = constraints.getHeight() < heightNDC ? constraints.getHeight() : heightNDC;
            width = constraints.getWidthSS() < WIDTH ? constraints.getWidthSS() : WIDTH;
            height = constraints.getHeightSS() < HEIGHT ? constraints.getHeightSS() : HEIGHT;

            super.setRawModel(constraints.getX(), constraints.getY(), widthNDC, heightNDC);
            super.setBoundingBox(constraints.getXSS(), constraints.getYSS(), width, height);
            
            this.x = constraints.getXSS() - width / 4;
            this.y = constraints.getYSS() + height / 4;
        }
        if (isClicked()){
            notifyListeners();
        }
    }
    
    @Override
    public void render(Renderer renderer){
        renderer.renderGuiElement(rawModel, color);
        renderer.drawString(font, text, x, y);
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
