/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xrayinspector;

import engine.graphics.*;
import engine.io.Input;
import engine.io.ModelLoader;
import static engine.io.ImageLoader.createBufferedImgFromDICOMFile;
import engine.io.Window;
import engine.math.*;
import engine.objects.Camera;
import engine.objects.GameObject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Timur
 */

public class Main implements Runnable {
    public Thread game;
    public Window window;
    public Renderer renderer;
    public Shader shader;
    public final int WIDTH = 1280, HEIGHT = 760;
    public long time;
    
    //public File f = new File("E:\\XRAYshots\\1\\CPTAC-LSCC\\C3L-01000\\06-09-2011-MSKT organov grudnoy k-18628\\3.000000-LUNG 1.25mm-28122\\1-001.dcm");
    //public BufferedImage ttt = createBufferedImgFromDICOMFile(f);
    
    //public Mesh[] model = ModelLoader.loadModel("resources/models/cube.obj", "/textures/1-001.png");
    
    public Mesh mesh = new Mesh(new Vertex[] {
        //Back face
        new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),

        //Front face
        new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 0.0f)),

        //Right face
        new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),

        //Left face
        new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),

        //Top face
        new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),

        //Bottom face
        new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),
    }, new int[] {
        //Back face
        0, 1, 2,	
        0, 2, 3,	

        //Front face
        4, 5, 6,
        4, 6, 7,

        //Right face
        8, 9, 10,
        8, 10, 11,

        //Left face
        12, 13, 14,
        12, 14, 15,

        //Top face
        16, 17, 18,
        16, 18, 19,

        //Bottom face
        20, 21, 22,
        20, 22, 23
    });//, new Material("/textures/1-001.png"));
    
    public GameObject object = new GameObject(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), mesh);
    
    public Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f));
    
    public void start() {
        game = new Thread(this, "game");
        game.start();
    }
    
    public void init(){
        System.out.println("Inititalizing Game!");
        window = new Window(WIDTH, HEIGHT, "Viewport");
        //shader = new Shader("/shaders/mainVertex.glsl", "/shaders/SSBOTestFragment.glsl");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/rayCastingVolumeFragment.glsl");
        //shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        
        //
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("dcm", "jpg", "png", "jpeg", "gif", "dcm");
        fileChooser.setFileFilter(filter);
        int selected = fileChooser.showOpenDialog(null);
        File[] files;

        if(selected == JFileChooser.APPROVE_OPTION){
            files = fileChooser.getSelectedFiles();
            renderer = new Renderer(window, shader, files);
        }
        //
        
        //renderer = new Renderer(window, shader);
        //window.setBackgroundColor(0.669327f, 0.544758f, 0.494243f);
        window.setBackgroundColor(0.25f, 0.25f, 0.25f);
        window.create();
        mesh.create();
        shader.create();
        time = System.currentTimeMillis();
    }
    
    public void run(){
        init();
        while (!window.shouldClose()){
            update();
            render();
            if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) return;
            if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) window.mouseState(true);
            if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) window.mouseState(false);
            
            //Открытие файлов
            if (Input.isKeyDown(GLFW.GLFW_KEY_O)){
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("dcm", "jpg", "png", "jpeg", "gif");
                fileChooser.setFileFilter(filter);
                int selected = fileChooser.showOpenDialog(null);

                if(selected == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    //String getselectedImage = file.getAbsolutePath();
                    //JOptionPane.showMessageDialog(null, getselectedImage);

                    try{
                        BufferedImage img = ImageIO.read(file);

                    }catch(IOException e){
                        System.err.println(e.toString());
                    }
                }
            }
        }
        close();
    }
    
    private void update(){
        //System.out.println("Updating Game!");
        window.update();
        camera.update(object);
        //if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) System.out.println("X: " + Input.getScrollX() + ", Y:" + Input.getScrollY());
    }
    
    private void render(){
        //System.out.println("Rendering Game!");
        renderer.renderMesh(object, camera);
        window.swapBuffers();
    }
    
    private void close(){
        window.destroy();
        mesh.destroy();
        shader.destroy();
    }
    
    public static void main(String[] args){
        new Main().start();
    }
}
