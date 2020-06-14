/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;

import engine.objects.gui.constraint.Constraint;
import engine.graphics.RawModel;
import engine.graphics.Renderer;
import engine.math.Vector2f;
import engine.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Timur
 */
public class UIContainer extends UIComponent{
    private int width, height;
    
    private ArrayList<UIComponent> elements;
    private ArrayList<UIConstraints> constraints;
    
    
    public UIContainer(int width, int height, Vector3f color){
        super.WIDTH = width;        //??
        super.HEIGHT = height;  //??
        super.color = color;
        elements = new ArrayList<>();
        constraints = new ArrayList<>();
    }
    
    public void Add(UIComponent element, UIConstraints constraints){
        this.elements.add(element);
        this.constraints.add(constraints);
    }
    
    public Iterable<UIComponent> getElements(){
        return elements;
    }
    
    public UIComponent getElement(int i){
        return elements.get(i);
    }
    
    @Override
    public void create(UIConstraints constraints) {
        update(constraints);
        for (int i = 0; i < this.elements.size(); i++) {
            this.constraints.get(i).create(width, height);
            this.elements.get(i).create(this.constraints.get(i));
        }
    }

    @Override
    public void update(UIConstraints constraints) {
        float widthNDC = (float)this.WIDTH / constraints.getWindowWidth();
        float heightNDC = (float)this.HEIGHT / constraints.getWindowHeight();
        if (true) {
            //float widthNDC = (float)this.WIDTH / constraints.getWindowWidth();
            //float heightNDC = (float)this.HEIGHT / constraints.getWindowHeight();

            x = constraints.getXSS();
            y = constraints.getYSS();

            widthNDC = constraints.getWidth() < widthNDC ? constraints.getWidth() : widthNDC;
            heightNDC = constraints.getHeight() < heightNDC ? constraints.getHeight() : heightNDC;
            width = constraints.getWidthSS() < WIDTH ? constraints.getWidthSS() : WIDTH;
            height = constraints.getHeightSS() < HEIGHT ? constraints.getHeightSS() : HEIGHT;

            super.setRawModel(constraints.getX(), constraints.getY(), widthNDC, heightNDC);
            super.setBoundingBox(x, y, width, height);
        }
        for (int i = 0; i < this.elements.size(); i++) {
            
            this.constraints.get(i).setParentOffset(aabbMin);
            this.constraints.get(i).setParentOffsetRelative(Vector2f.divide(aabbMin, new Vector2f(constraints.getWindowWidth(), constraints.getWindowHeight())));
            this.constraints.get(i).setParentSize(new Vector2f(width, height));
            this.constraints.get(i).setParentSizeRelative(Vector2f.divide(new Vector2f(width, height), new Vector2f(constraints.getWindowWidth(), constraints.getWindowHeight())));
            this.constraints.get(i).update(width, height);
            this.elements.get(i).update(this.constraints.get(i));
        }
    }

    @Override
    public void render(Renderer renderer) {
        renderer.renderGuiElement(rawModel, color);
        for (UIComponent e:elements) {
            e.render(renderer);
        }
    }

    @Override
    public void destroy() {
        for (UIComponent e:elements) {
            e.destroy();
        }
        rawModel.destroy();
    }
}
