/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui.constraint;

import engine.io.Window;

/**
 *
 * @author Timur
 */
public class PixelConstraint extends Constraint{
    
    int value;
    int border;
    
    public PixelConstraint(int value){
        this.value = value;
    }
    
    public PixelConstraint(int value, int border){
        this.value = value;
        this.border = border;
    }
    
    public int getValue(int value){
        switch (border) {
            case Constraint.BORDER_TOP:
                return this.value;
            case Constraint.BORDER_BOTTOM:
                return value - this.value;
            case Constraint.BORDER_LEFT:
                return this.value;
            case Constraint.BORDER_RIGHT:
                return value - this.value;
            default:
                return -1;
        }
    }

    @Override
    public int getX(Window window) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getY(Window window) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getWidth(Window window) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getHeight(Window window) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(int value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
