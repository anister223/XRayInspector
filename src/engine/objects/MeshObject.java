/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects;

import engine.graphics.Mesh;
import engine.math.Vector3f;

/**
 *
 * @author Timur
 */
public class MeshObject {
    private Vector3f position, rotation, scale;
    private Mesh mesh;

    public MeshObject(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.mesh = mesh;
    }

    public void update(){
        
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
    
    public void setScale(Vector3f value){
        this.scale = value;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    
    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
