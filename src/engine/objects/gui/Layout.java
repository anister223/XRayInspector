/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;

import engine.graphics.Renderer;
import engine.objects.gui.constraint.Constraint;
import engine.io.Window;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author Timur
 */
public class Layout {
    //int X, Y, width, height;
    private Window window;
    
    private ArrayList<UIComponent> elements;
    private ArrayList<UIConstraints> constraints;
    //private StbTtFontResource font;
    
    public Layout(Window window){
        this.window = window;
        this.elements = new ArrayList<>();
        this.constraints = new ArrayList<>();
    }
    /*
    public int getWidth(){
        return this.width;
    }
    
    public int getHeight(){
        return height;
    }
    */
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
    
    public void create(){
        for (int i = 0; i < elements.size(); i++) {
            constraints.get(i).create(window.getWidth(), window.getHeight());
            elements.get(i).create(constraints.get(i));
        }
    }
    
    public void update(){
        for (int i = 0; i < elements.size(); i++) {
            constraints.get(i).update(window.getWidth(), window.getHeight());
            elements.get(i).update(constraints.get(i));
        }
    }
    
    public void render(Renderer renderer){
        for (UIComponent e:elements) {
            e.render(renderer);
        }
    }
    
    public void destroy(){
        for (UIComponent e:elements) {
            e.destroy();
        }
    }
}
