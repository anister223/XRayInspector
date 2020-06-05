/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.graphics;

import static engine.io.ImageLoader.*;
import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;

/**
 *
 * @author Timur
 */
public class Renderer {
    private Shader shader;
    private Window window;
    private BufferedImage pngTest;
    private File[] files;
    private Raster[] DCMrasters;
    
    int ssbo;
    int sampleResolution = 100;
    
    int[][] colors;
    int SIZE;
    int width, height;
    int normalizationFactor;
    
    boolean initialized = false;
    
    float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
    private final RawModel quad = loader.loadToVAO(positions);
    
    public Renderer(Window window, Shader shader, File[] files){
        this.shader = shader;
        this.window = window;
        
        //User multiple files input
        this.files = files;
        this.DCMrasters = new Raster[files.length];
        for (int i = 0; i < files.length; i++) {
            DCMrasters[i] = createRasterFromDICOMFile(files[i]);
        }
        //User multiple files input
        SIZE = 0;
        
        colors = new int[files.length][];   //Использовать байты??
        
        int max = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < files.length; i++) {
            
            SIZE = DCMrasters[i].getWidth() * DCMrasters[i].getHeight();
            colors[i] = new int[SIZE];
            DCMrasters[i].getPixels(0, 0, DCMrasters[i].getWidth(), DCMrasters[i].getHeight(), colors[i]);
            
            //temp normalization method
            for (int j = 0; j < colors[i].length; j++) {
                int temp = colors[i][j] & 0xFFFF;
                max = temp > max ? temp:max;
                min = temp < min ? temp:min;
            }
            //!temp
        }
        width = DCMrasters[0].getWidth();
        height = DCMrasters[0].getHeight();
        normalizationFactor = max;
        System.out.println("max value is: " + max);
        System.out.println("min value is: " + min);
    }
    
    private void init(){
        
        //SSBO initialization
        ssbo = GL15.glGenBuffers();
        GL33.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssbo);
        
        long pixelSIZE = SIZE * (Integer.SIZE / 8);
        
        //ssbo User multiple files input
        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, pixelSIZE * files.length, GL15.GL_DYNAMIC_COPY);   // declare buffer
        for (int i = 0; i < files.length; i++) {
            GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, pixelSIZE * i, colors[i]);
        }
        GL33.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        
        GL33.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);    //unbind
        
        initialized = true;
    }
    
    public void renderMesh(GameObject object, Camera camera){
        GL30.glBindVertexArray(object.getMesh().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        
        // Alpha blending
        GL33.glEnable(GL33.GL_BLEND);
        //GL33.glBlendFunc(GL33.GL_ONE, GL33.GL_ONE_MINUS_SRC_ALPHA);         //If you use premultiplied alpha,
        GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);   //If you use non-premultiplied alpha,
        
        // Face culling
        GL33.glEnable(GL33.GL_CULL_FACE); // enables face culling    
        GL33.glCullFace(GL33.GL_BACK); // tells OpenGL to cull back faces (the sane default setting)
        GL33.glFrontFace(GL33.GL_CW); // tells OpenGL which faces are considered 'front' (use GL_CW or GL_CCW)
        
        if (ssbo == 0) {        //!?
            init();
        }
        
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());
        shader.bind();
        shader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));
        shader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
        shader.setUniform("projection", window.getProjectionMatrix());
        
        shader.setUniform("width", width);
        shader.setUniform("height", height);
        shader.setUniform("dcmsAmount", files.length);  //!
        
        shader.setUniform("normalizationFactor", normalizationFactor);
        
        //bounding box
        Vector3f aabb1 = new Vector3f(-0.5f, -0.5f, -0.5f);
        Vector3f aabb2 = new Vector3f( 0.5f,  0.5f,  0.5f);
        
        
        shader.setUniform("screenHeight", window.getHeight());
        shader.setUniform("screenWidth", window.getWidth());
        shader.setUniform("aabb1", aabb1);
        shader.setUniform("aabb2", aabb2);
        shader.setUniform("cameraPos", camera.getPosition());
        //shader.setUniform("cameraVec", camera.getRotation());
        //shader.setUniform("fov", window.getFOV());
        shader.setUniform("sampleResolution", sampleResolution);
        
        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);
        shader.unbind();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL33.glDisable(GL33.GL_BLEND);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
    
    public void renderGuiElement(ArrayList<Integer> guis){
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        for(int gui: guis){
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
    
    public void destroy(){
        GL15.glDeleteBuffers(ssbo);
    }
}
