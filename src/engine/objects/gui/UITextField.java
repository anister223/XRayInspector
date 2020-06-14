/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;

import engine.graphics.RawModel;
import engine.graphics.Renderer;
import engine.math.Vector3f;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author Timur
 */
public class UITextField extends UIComponent{
    private StbTtFontResource font;
    private String text;
    
    ActionListener listener;
    
    public UITextField(int width, int height, String text){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.text = text;
        font = new StbTtFontResource(new File("C:\\Windows\\Fonts\\times.ttf"), 24);
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
        this.x = constraints.getXSS();
        this.y = constraints.getYSS();
    }
    
    @Override
    public void render(Renderer renderer){
        renderer.drawString(font, text, x, y);
    }

    @Override
    public void destroy() {
        font.finalize();
    }
    
    public ActionListener getActionListener(){
        return listener;
    }
}
