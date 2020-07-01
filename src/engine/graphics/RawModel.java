/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.graphics;

import engine.math.Vector3f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Timur
 */
public class RawModel {
    
    private Vertex[] vertices;
    private int[] indices;
    //private Material material;
    private int vao, pbo, ibo, cbo, tbo; //Vertex Array Object, Position/Indices/Color/Texture Buffer Object
    
    public RawModel(Vertex[] vertices, int[] indices){
        this.vertices = vertices;
        this.indices = indices;
    }
    
    public void create(){
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        
        float[] positionData = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            positionData[i * 2] = vertices[i].getPosition().getX();
            positionData[i * 2 + 1] = vertices[i].getPosition().getY();
        }
        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
        positionBuffer.put(positionData).flip(); //flip потомучто OpenGL так работает
        pbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //unbind buffer
        
        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
        float[] textureData = new float[vertices.length *2];
        for (int i = 0; i < vertices.length; i++) {
            textureData[i*2] = vertices[i].getTextureCoord().getX();
            textureData[i*2 + 1] = vertices[i].getTextureCoord().getY();
        }
        textureBuffer.put(textureData).flip(); //flip потомучто OpenGL так работает
        
        tbo = storeData(textureBuffer, 1, 2);
        
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); //unbind buffer
        
    }
    
    public int storeData(FloatBuffer buffer, int index, int size){
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID); //bind buffer
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //unbind buffer
        return bufferID;
    }

    public void destroy(){
        GL15.glDeleteBuffers(pbo);
        //GL15.glDeleteBuffers(cbo);
        GL15.glDeleteBuffers(ibo);
        //GL15.glDeleteBuffers(tbo);
        
        GL30.glDeleteVertexArrays(vao);
        
        //material.destroy();
    }
    
    public Vertex[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getVAO() {
        return vao;
    }

    public int getPBO() {
        return pbo;
    }

    public int getIBO() {
        return ibo;
    }

    public int getCBO() {
        return cbo;
    }
    
    public int getTBO() {
        return tbo;
    }
    
    public static RawModel getDefaultRawModer(){
        RawModel rawModel = new RawModel(new Vertex[]{
            new Vertex(new Vector3f(0.0f, 0.0f, 0.0f)),
            new Vertex(new Vector3f(0.0f, 0.0f, 0.0f)),
            new Vertex(new Vector3f(0.0f, 0.0f, 0.0f))
        }, new int[] {
            0, 1, 2
        });
        rawModel.create();
        return rawModel;
    }
}
