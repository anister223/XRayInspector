/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui.constraint;

import engine.io.Window;
import engine.objects.gui.constraint.*;

/**
 *
 * @author Timur
 * 
 * returns value in ScreenSpace coord
 * 
 */
public abstract class Constraint {
    public static final int BORDER_RIGHT = 0;
    public static final int BORDER_LEFT = 1;
    public static final int BORDER_TOP = 2;
    public static final int BORDER_BOTTOM = 3;
    
    private int X, Y, width, height;
    
    public abstract int update(int value);
    
    public abstract int getValue(int value);
    
    public abstract int getX(Window window);
    
    public abstract int getY(Window window);
    
    public abstract int getWidth(Window window);
    
    public abstract int getHeight(Window window);
}
