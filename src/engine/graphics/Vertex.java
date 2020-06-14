/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.graphics;

import engine.math.Vector2f;
import engine.math.Vector3f;

/**
 *
 * @author Timur
 */
public class Vertex {
    private Vector3f position, normal;
    private Vector3f color;
    private Vector2f textureCoord;
    
    public Vertex(Vector3f position, Vector3f normal, Vector3f color, Vector2f textureCoord){
        
        this.position = position;
        this.normal = normal;
        this.color = color;
        this.textureCoord = textureCoord;
    }
    
    public Vertex(Vector3f position, Vector3f normal, Vector2f textureCoord){
        
        this.position = position;
        this.normal = normal;
        this.color = new Vector3f(1.0f, 1.0f, 1.0f);
        this.textureCoord = textureCoord;
    }
    
    public Vertex(Vector3f position, Vector2f textureCoord){
        
        this.position = position;
        this.normal = new Vector3f(1.0f, 1.0f, 1.0f);;
        this.color = new Vector3f(1.0f, 1.0f, 1.0f);
        this.textureCoord = textureCoord;
    }
    
    public Vertex(Vector3f position){
        this.position = position;
        this.normal = new Vector3f(1.0f, 1.0f, 1.0f);;
        this.color = new Vector3f(1.0f, 1.0f, 1.0f);
        this.textureCoord = new Vector2f(1.0f, 1.0f);
    }

    public Vector3f getPosition() {
        return position;
    }
    
    public Vector3f getNormal() {
        return normal;
    }
    
    public Vector3f getColor(){
        return color;
    }

    public Vector2f getTextureCoord() {
        return textureCoord;
    }
}
