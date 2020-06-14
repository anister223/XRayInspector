/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.math;

/**
 *
 * @author Timur
 */
public class Util {
    
    public static float Clamp(float x, float min, float max){
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }
}
