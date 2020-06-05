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
public class Vector2f {
    private float x,y;
    
    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    public static Vector2f add(Vector2f v1, Vector2f v2){
        return new Vector2f(v1.getX() + v2.getX(), v1.getY() + v2.getY());
    }
    
    public static Vector2f subtract(Vector2f v1, Vector2f v2){
        return new Vector2f(v1.getX() - v2.getX(), v1.getY() - v2.getY());
    }
    
    public static Vector2f multiply(Vector2f v1, Vector2f v2){
        return new Vector2f(v1.getX() * v2.getX(), v1.getY() * v2.getY());
    }
    
    public static Vector2f divide(Vector2f v1, Vector2f v2){
        return new Vector2f(v1.getX() / v2.getX(), v1.getY() / v2.getY());
    }
    
    public static float length(Vector2f v){
        return (float) (Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY()));
    }
    
    public static Vector2f normalize(Vector2f v){
        float len = Vector2f.length(v);
        return Vector2f.divide(v, new Vector2f(len, len));
    }
    
    public static float dot(Vector2f v1, Vector2f v2){
        return v1.getX() * v2.getX() + v1.getY() * v2.getY();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Float.floatToIntBits(this.x);
        hash = 13 * hash + Float.floatToIntBits(this.y);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector2f other = (Vector2f) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        return true;
    }
    
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
