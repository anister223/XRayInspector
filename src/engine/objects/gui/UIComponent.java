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
import engine.math.Vector2f;
import engine.math.Vector3f;
import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Timur
 */
public abstract class  UIComponent {
    protected int WIDTH, HEIGHT;
    protected int x, y;
    protected Vector3f color;
    protected Vector2f aabbMin, aabbMax;
    protected RawModel rawModel;
    
    private ArrayList<UIComponent> elements;
    
    public abstract void create(UIConstraints constraints);
    public abstract void update(UIConstraints constraints);
    public abstract void render(Renderer renderer);
    public abstract void destroy();
    
    public Vector3f getColor() {return color;}
    public RawModel getRawModel() {return rawModel;}
    public Iterable<UIComponent> getElements() {return elements;}
    
    public void setBoundingBox(int XSS, int YSS, int widthSS, int heightSS){
        aabbMin = new Vector2f(XSS - widthSS / 2, YSS - heightSS / 2);
        aabbMax = new Vector2f(XSS + widthSS / 2, YSS + heightSS / 2);
    }
    
    public void setRawModel(float X, float Y, float width, float height){
        if (rawModel != null) rawModel.destroy();
        rawModel = new RawModel(new Vertex[]{
            new Vertex(new Vector3f(X - width / 1, Y + height / 1, 0.0f)),
            new Vertex(new Vector3f(X + width / 1, Y + height / 1, 0.0f)),
            new Vertex(new Vector3f(X - width / 1, Y - height / 1, 0.0f)),
            new Vertex(new Vector3f(X + width / 1, Y - height / 1, 0.0f))
        }, new int[] {
            0, 1, 2, 3
        });
        rawModel.create();
    }
    
    
    public boolean inBound(){
        return  Input.getMouseX() > aabbMin.getX() && Input.getMouseX() < aabbMax.getX() &&
                Input.getMouseY() > aabbMin.getY() && Input.getMouseY() < aabbMax.getY();
    }
    
    public boolean isClicked(){
        if(inBound()){
            if (isHoldOn(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                this.color = Color3f.MIDDLE_BLUE;
            }
            if (isReleasedOn(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                this.color = Color3f.WHITE;
                return true;
            }
            return false;
        }else{
            this.color = Color3f.WHITE;
            return false;
        }
    }
    
    public Vector2f getMouseRelativePos(){
        Vector2f result = new Vector2f(
                (float)(Input.getMouseX() - x),
                (float)(Input.getMouseY() - y)
        );
        return result;
    }
    
    public boolean isHoldOn(int key){
        return Input.isButtonDown(key);
    }
    
    public boolean isReleasedOn(int key){
        return Input.isButtonReleased(key);
    }
}
