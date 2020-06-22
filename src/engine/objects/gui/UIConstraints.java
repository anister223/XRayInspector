/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;
/**
 *
 * @author Timur
 * 
 * returns value in NDC space
 */
import engine.math.Vector2f;
import engine.objects.gui.constraint.*;

public class UIConstraints {
    
    private int windowWidth, windowHeight;
    private Constraint X, Y, width, height;
    
    private Vector2f parentSize, parentOffset;
    private Vector2f parentSizeRelative, parentOffsetRelative;
    
    int safeFrame = 10; //temp!
    
    public boolean stateChanged = true;
    
    public UIConstraints(){
        this.windowWidth = 1280;
        this.windowHeight = 720;
    }
    
    public int getWindowWidth(){
        return windowWidth;
    }
    
    public int getWindowHeight(){
        return windowHeight;
    }
    
    public void create(int windowWidth, int windowHeight){
        
    }
    
    public void update(int windowWidth, int windowHeight){
        if (this.windowWidth != windowWidth || this.windowHeight != windowHeight) {
            this.windowWidth = windowWidth;
            this.windowHeight = windowHeight;
            stateChanged = true;
        }else{
            stateChanged = false;
        }
    }
    
    public float getX(){
        return ((float)getXSS() / windowWidth - 0.5f) * 2;
    }
    
    public float getY(){
        return -((float)getYSS() / windowHeight - 0.5f) * 2;
    }
    
    public float getWidth(){
        return (float)getWidthSS() / windowWidth;
    }
    
    public float getHeight(){
        return (float)getHeightSS() / windowHeight;
    }
    
    public int getXSS(){
        int x = X.getValue(windowWidth);
        // Коректировка правого края
        if (getWidthSS() / 2 + x > windowWidth - safeFrame) {
            x = windowWidth - getWidthSS() / 2 - safeFrame;
        } 
        // Коректировка левого края
        if (x - getWidthSS() / 2 < safeFrame) {
            x = getWidthSS() / 2 + safeFrame / 2;
        }
        return x;
    }
    
    public int getYSS(){
        int y = Y.getValue(windowHeight);
        if (getHeightSS() / 2 + y > windowHeight - safeFrame) {
            y = windowHeight - getHeightSS() / 2 - safeFrame;
        } 
        if (y - getHeightSS() / 2 < safeFrame) {
            y = getHeightSS() / 2 + safeFrame / 2;
        }
        return y;
    }
    
    public int getWidthSS(){
        int width = this.width.getValue(windowWidth);
        if (width > windowWidth - safeFrame) {
            width = windowWidth - safeFrame;
        }
        return width;
    }
    
    public int getHeightSS(){
        int height = this.height.getValue(windowHeight);
        if (height > windowHeight - safeFrame) {
            height = windowHeight - safeFrame;
        }
        return height;
    }
    
    public void setX(Constraint constraint){
        this.X = constraint;
    }
    
    public void setY(Constraint constraint){
        this.Y = constraint;
    }
    
    public void setWidth(Constraint constraint){
        this.width = constraint;
    }
    
    public void setHeight(Constraint constraint){
        this.height = constraint;
    }

    public Vector2f getParentSize() {
        return parentSize;
    }

    public void setParentSize(Vector2f parentSize) {
        this.parentSize = parentSize;
    }

    public Vector2f getParentOffset() {
        return parentOffset;
    }

    public void setParentOffset(Vector2f parentOffset) {
        this.parentOffset = parentOffset;
    }

    public Vector2f getParentSizeRelative() {
        return parentSizeRelative;
    }

    public void setParentSizeRelative(Vector2f parentSizeRelative) {
        this.parentSizeRelative = parentSizeRelative;
    }

    public Vector2f getParentOffsetRelative() {
        return parentOffsetRelative;
    }

    public void setParentOffsetRelative(Vector2f parentOffsetRelative) {
        this.parentOffsetRelative = parentOffsetRelative;
    }
}
