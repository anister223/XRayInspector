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
public class Vector3f {
    public final static int SIZE = 3;
    private float x,y,z;
        
    public Vector3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public static Vector3f add(Vector3f v1, Vector3f v2){
        return new Vector3f(v1.getX() + v2.getX(), v1.getY() + v2.getY(),v1.getZ() + v2.getZ());
    }
    
    public static Vector3f subtract(Vector3f v1, Vector3f v2){
        return new Vector3f(v1.getX() - v2.getX(), v1.getY() - v2.getY(),v1.getZ() - v2.getZ());
    }
    
    public static Vector3f multiply(Vector3f v1, Vector3f v2){
        return new Vector3f(v1.getX() * v2.getX(), v1.getY() * v2.getY(),v1.getZ() * v2.getZ());
    }
    
    public static Vector3f multiply(Vector3f v1, float value){
        return new Vector3f(v1.getX() * value, v1.getY() * value,v1.getZ() * value);
    }
    
    public static Vector3f divide(Vector3f v1, Vector3f v2){
        return new Vector3f(v1.getX() / v2.getX(), v1.getY() / v2.getY(),v1.getZ() / v2.getZ());
    }
    
    public static float length(Vector3f v){
        return (float) (Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ()));
    }
    
    public static Vector3f normalize(Vector3f v){
        float len = Vector3f.length(v);
        return Vector3f.divide(v, new Vector3f(len, len, len));
    }
    
    public static float dot(Vector3f v1, Vector3f v2){
        return v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Float.floatToIntBits(this.x);
        hash = 41 * hash + Float.floatToIntBits(this.y);
        hash = 41 * hash + Float.floatToIntBits(this.z);
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
        final Vector3f other = (Vector3f) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
            return false;
        }
        return true;
    }
    
    
    
    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public float getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(float z) {
        this.z = z;
    }
    
}
