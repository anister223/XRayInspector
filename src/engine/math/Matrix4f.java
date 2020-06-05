/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.math;

import java.util.Arrays;

/**
 *
 * @author Timur
 */
public class Matrix4f {
    public static final int SIZE = 4;
    private float[] elements = new float[SIZE * SIZE];
    
    public static Matrix4f identity(){ //Матрица с 1 на главной диагонялью
        Matrix4f result = new Matrix4f();
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.set(i, j, i==j ? 1:0);
            }
        }
        
        return result;
    }
    
    public static Matrix4f translate(Vector3f translate){
        Matrix4f result = Matrix4f.identity();
        
        result.set(3, 0, translate.getX());
        result.set(3, 1, translate.getY());
        result.set(3, 2, translate.getZ());
        
        return result;
    }
    
    public static Matrix4f rotate(float angle, Vector3f axis){
        Matrix4f result = Matrix4f.identity();
        
        float cos = (float) Math.cos(Math.toRadians(angle));
        float sin = (float) Math.sin(Math.toRadians(angle));
        
        result.set(0, 0, cos + axis.getX() * axis.getX() * (1-cos));
        result.set(0, 1, axis.getX() * axis.getY() * (1-cos) - axis.getZ() * sin);
        result.set(0, 2, axis.getX() * axis.getZ() * (1-cos) + axis.getY() * sin);
        
        result.set(1, 0, axis.getY() * axis.getX() * (1-cos) + axis.getZ() * sin);
        result.set(1, 1, cos + axis.getY() * axis.getY() * (1-cos));
        result.set(1, 2, axis.getY() * axis.getZ() * (1-cos) - axis.getX() * sin);
        
        result.set(2, 0, axis.getZ() * axis.getX() * (1-cos) - axis.getY() * sin);
        result.set(2, 1, axis.getZ() * axis.getY() * (1-cos) + axis.getX() * sin);
        result.set(2, 2, cos + axis.getZ() * axis.getZ() * (1-cos));
        
        return result;
    }
    
    public static Matrix4f scale(Vector3f scalar){
        Matrix4f result = Matrix4f.identity();
        
        result.set(0, 0, scalar.getX());
        result.set(1, 1, scalar.getY());
        result.set(2, 2, scalar.getZ());
        
        return result;
    }
    
    public static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scale){
        Matrix4f result = Matrix4f.identity();
        
        Matrix4f translationMatrix = Matrix4f.translate(position);
        Matrix4f rotXMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1,0,0));
        Matrix4f rotYMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0,1,0));
        Matrix4f rotZMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f(0,0,1));
        Matrix4f scaleMatrix = Matrix4f.scale(scale);
        
        Matrix4f rotationMatrix = Matrix4f.multiply(rotXMatrix, Matrix4f.multiply(rotYMatrix, rotZMatrix));
        result = Matrix4f.multiply(translationMatrix, Matrix4f.multiply(rotationMatrix, rotZMatrix));
        result = Matrix4f.multiply(result, scaleMatrix);
        
        return result;
    }
    
    public static Matrix4f projection(float fov, float aspect, float near, float far){
        Matrix4f result = Matrix4f.identity();
        float tanFOV = (float) Math.tan(Math.toRadians(fov / 2));
        float range = far - near;
        
        result.set(0,0, 1.0f / (aspect*tanFOV));
        result.set(1, 1, 1.0f / tanFOV);
        result.set(2,2, -((far + near) / range));
        result.set(2, 3, -1.0f);
        result.set(3,2, -((2 * far * near) / range));
        result.set(3, 3, 0.0f);
        
        return result;
    }
    
    public static Matrix4f orthoProjection(float aspect, float near, float far){
        Matrix4f result = Matrix4f.identity();
        float range = far - near;
        
        float right = 1.0f * aspect, left = -1.0f * aspect, top = 1.0f, bottom = -1.0f;
        
        result.set(0, 0, 2 / (right - left));
        result.set(1, 1, 2 / (top - bottom));
        result.set(2, 2, -2 / (far - near));
        result.set(3, 0, -(right + left) / (right - left));
        result.set(3, 1, -(top + bottom) / (top - bottom));
        result.set(3, 2, -(far + near) / (far - near));
        
        return result;
    }
    
    public static Matrix4f view(Vector3f position, Vector3f rotation){
        Matrix4f result = Matrix4f.identity();
        
        Vector3f negative = new Vector3f(-position.getX(), -position.getY(), -position.getZ());
        Matrix4f translationMatrix = Matrix4f.translate(negative);
        Matrix4f rotXMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1,0,0));
        Matrix4f rotYMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0,1,0));
        Matrix4f rotZMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f(0,0,1));
        
        Matrix4f rotationMatrix = Matrix4f.multiply(rotZMatrix, Matrix4f.multiply(rotYMatrix, rotXMatrix));
        
        result = Matrix4f.multiply(translationMatrix, rotationMatrix);
        
        return result;
    }
    
    public static Matrix4f multiply(Matrix4f matrix, Matrix4f other){
        Matrix4f result = Matrix4f.identity();
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.set(i, j,matrix.get(i, 0) * other.get(0, j)+
                                matrix.get(i, 1) * other.get(1, j)+
                                matrix.get(i, 2) * other.get(2, j)+
                                matrix.get(i, 3) * other.get(3, j));
            }
        }
        
        return result;
    }
    
    public static Vector3f multiply(Matrix4f matrix, Vector3f vector){
        Vector3f result = new Vector3f(0.0f, 0.0f, 0.0f);
        
        for (int j = 0; j < Vector3f.SIZE; j++) {
            result.setX(result.getX() + (vector.getX() * matrix.get(0, j)));
            result.setY(result.getY() + (vector.getY() * matrix.get(1, j)));
            result.setZ(result.getZ() + (vector.getZ() * matrix.get(2, j)));
        }
        
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Arrays.hashCode(this.elements);
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
        final Matrix4f other = (Matrix4f) obj;
        if (!Arrays.equals(this.elements, other.elements)) {
            return false;
        }
        return true;
    }
    
    
    
    public float get(int x, int y){
        return elements[y * SIZE + x];
    }
    
    public void set(int x, int y, float value){
        elements[y * SIZE + x] = value;
    }
    
    public float[] getAll(){
        return elements;
    }
}
