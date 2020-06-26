/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xrayinspector;

import engine.graphics.*;
import static engine.io.ImageLoader.createRasterFromDICOMFile;
import static engine.io.ImageLoader.getImagePositionPatientVector;
import static engine.io.ImageLoader.getSpacingBetweenSlices;
import static engine.io.ImageLoader.getPixelSpacing;
import engine.io.Input;
import engine.io.Window;
import engine.math.*;
import engine.objects.Camera;
import engine.objects.MeshObject;
import engine.objects.gui.*;
import engine.objects.gui.constraint.*;
import java.awt.image.Raster;
import java.io.File;
import java.text.DecimalFormat;
import java.util.stream.IntStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Timur
 */

public class Main implements Runnable {
    public Thread mainThread;
    public Window window;
    public Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f));
    public Renderer renderer;
    public Shader shader, guiShader;
    public final int WIDTH = 1280, HEIGHT = 720;
    
    //GUI
    public Layout layout;
    private UIBlock block;
    private UITextField textField;
    private UITextField textField2;
    private UIButton buttonOpenFiles;
    private UIButton button;
    private UISlider slider;
    private UISlider sliderBrightness;
    private UICheckBox checkBox;
    
    //Data
    private File[] files;
    private Raster[] DCMrasters;
    int[][] colors;
    
    float spacingBetweenSlices;
    float pixelSpacing;
    float shift;
    
    private float brightness;
    private float treshold;
    
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
    
    public MeshObject object = new MeshObject(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), mesh);
    
    public void start() {
        mainThread = new Thread(this, "mainThread");
        mainThread.start();
    }
    
    public void init(){
        System.out.println("Inititalizing!");
        window = new Window(WIDTH, HEIGHT, "XRayInspector");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/rayCastingVolumeFragment.glsl");
        guiShader = new Shader("/shaders/guiVertexShader.txt", "/shaders/guiFragmentShader.txt");
        renderer = new Renderer(window, shader, guiShader);
        
        window.setBackgroundColor(0.0f, 0.0f, 0.0f);
        window.create();
        mesh.create();
        shader.create();
        guiShader.create();
        
        // Начало объявления данных
        layout = new Layout(window);
        
        block = new UIBlock(Color3f.SOARING_EAGLE);
        UIConstraints constraints5 = new UIConstraints();
        constraints5.setX(new PixelConstraint(50, Constraint.BORDER_RIGHT));
        constraints5.setY(new CenterConstraint());
        constraints5.setWidth(new RelativeConstraint(0.225f));
        constraints5.setHeight(new RelativeConstraint(0.9f));
        layout.Add(block, constraints5);
        
        button = new UIButton(105, 32, Color3f.WHITE);
        UIConstraints constraints = new UIConstraints();
        constraints.setX(new PixelConstraint(50, Constraint.BORDER_RIGHT));
        constraints.setY(new PixelConstraint(225, Constraint.BORDER_TOP));
        constraints.setWidth(new RelativeConstraint(0.1f));
        constraints.setHeight(new RelativeConstraint(0.1f));
        layout.Add(button, constraints);
        
        slider = new UISlider();
        slider.setMin(1.0f / 255);
        UIConstraints constraints2 = new UIConstraints();
        constraints2.setX(new RelativeConstraint(0.875f));
        constraints2.setY(new PixelConstraint(150, Constraint.BORDER_TOP));
        constraints2.setWidth(new RelativeConstraint(0.175f));
        constraints2.setHeight(new RelativeConstraint(0.01f));
        layout.Add(slider, constraints2);
        
        textField2 = new UITextField(105, 32, String.valueOf(slider.getMin()));
        UIConstraints constraints8 = new UIConstraints();
        constraints8.setX(new RelativeConstraint(0.875f));
        constraints8.setY(new PixelConstraint(175, Constraint.BORDER_TOP));
        constraints8.setWidth(new RelativeConstraint(0.05f));
        constraints8.setHeight(new RelativeConstraint(0.05f));
        layout.Add(textField2, constraints8);
        
        sliderBrightness = new UISlider();
        UIConstraints constraints7 = new UIConstraints();
        constraints7.setX(new RelativeConstraint(0.875f));
        constraints7.setY(new PixelConstraint(100, Constraint.BORDER_TOP));
        constraints7.setWidth(new RelativeConstraint(0.175f));
        constraints7.setHeight(new RelativeConstraint(0.01f));
        //layout.Add(sliderBrightness, constraints7);
        
        textField = new UITextField(105, 32, "");
        UIConstraints constraints3 = new UIConstraints();
        constraints3.setX(new PixelConstraint(250, Constraint.BORDER_RIGHT));
        constraints3.setY(new PixelConstraint(275, Constraint.BORDER_TOP));
        constraints3.setWidth(new RelativeConstraint(0.05f));
        constraints3.setHeight(new RelativeConstraint(0.05f));
        layout.Add(textField, constraints3);
        
        checkBox = new UICheckBox();
        UIConstraints constraints4 = new UIConstraints();
        constraints4.setX(new PixelConstraint(50, Constraint.BORDER_RIGHT));
        constraints4.setY(new PixelConstraint(100, Constraint.BORDER_BOTTOM));
        constraints4.setWidth(new RelativeConstraint(0.05f));
        constraints4.setHeight(new RelativeConstraint(0.05f));
        //layout.Add(checkBox, constraints4);
        
        buttonOpenFiles = new UIButton(105, 32, Color3f.WHITE);
        UIConstraints constraints6 = new UIConstraints();
        constraints6.setX(new PixelConstraint(50, Constraint.BORDER_RIGHT));
        constraints6.setY(new PixelConstraint(150, Constraint.BORDER_BOTTOM));
        constraints6.setWidth(new RelativeConstraint(0.1f));
        constraints6.setHeight(new RelativeConstraint(0.1f));
        layout.Add(buttonOpenFiles, constraints6);
        // Конец объявления данных
        
        layout.create();
        
        button.setText("Volume");
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiButtonActionPerformed(evt);
            }
        });
        
        buttonOpenFiles.setText("Browse");
        buttonOpenFiles.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiButtonOpenFilesActionPerformed(evt);
            }
        });
        
        slider.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSliderActionPerformed(evt);
            }
        });
        
        sliderBrightness.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSliderBrightnessActionPerformed(evt);
            }
        });
    }
    
    @Override
    public void run(){
        init();
        while (!window.shouldClose()){
            update();
            render();
            if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) return;
            //if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) window.mouseState(true);
            //if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) window.mouseState(false);
        }
        close();
    }
    
    private void update(){
        window.update();
        renderer.update();
        if(!(Input.getMouseX() > window.getWidth() * (1 - 0.3f)))   //temp
            camera.update(object);
        layout.update();
        Input.buttonsUpRefresh();
    }
    
    private void render(){
        renderer.renderMesh(object, camera);
        //renderer.renderGui(layout);
        layout.render(renderer);
        window.swapBuffers();
    }
    
    private void close(){
        window.destroy();
        mesh.destroy();
        layout.destroy();
        shader.destroy();
        guiShader.destroy();
    }
    
    private void uiButtonActionPerformed(java.awt.event.ActionEvent evt){
        if (DCMrasters == null) {
            return;
        }
        float widthInMm = DCMrasters[0].getWidth() * this.pixelSpacing;
        float heightInMm = DCMrasters[0].getHeight()* this.pixelSpacing;
        double volume = volumeInVoxels() * (this.shift * widthInMm * heightInMm) / 1000.0f;
                
        //double volume = volume() * (this.spacingBetweenSlices * this.pixelSpacing * this.pixelSpacing) / 1000.0f;
        DecimalFormat decimalFormat = new DecimalFormat("#0.000");
        String numberAsString = decimalFormat.format(volume);
        this.textField.setText("Volume is: " + numberAsString + "cm");
    }
    
    private void uiButtonOpenFilesActionPerformed(java.awt.event.ActionEvent evt){
        if (loadFiles() == 0) {
            renderer.loadSSBO(colors);
        }
    }
    
    private void uiSliderActionPerformed(java.awt.event.ActionEvent evt){
        this.treshold = slider.getValue();
        this.textField2.setText(String.valueOf(slider.getValue()));
        renderer.setTreshold(this.treshold);
    }
    
    private void uiSliderBrightnessActionPerformed(java.awt.event.ActionEvent evt){
        this.brightness = sliderBrightness.getValue();
        renderer.setBrightness(this.brightness);
    }
    
    public static void main(String[] args){
        new Main().start();
    }
    
    public int loadFiles(){
        // Локализация компонентов окна JFileChooser
        UIManager.put(
                 "FileChooser.saveButtonText", "Сохранить");
        UIManager.put(
                 "FileChooser.cancelButtonText", "Отмена");
        UIManager.put(
                 "FileChooser.fileNameLabelText", "Наименование файла");
        UIManager.put(
                 "FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put(
                 "FileChooser.lookInLabelText", "Директория");
        UIManager.put(
                 "FileChooser.saveInLabelText", "Сохранить в директории");
        UIManager.put(
                 "FileChooser.folderNameLabelText", "Путь директории");
        
        JFileChooser fileChooser = new JFileChooser("E:\\XRAYshots\\CPTAC-LSCC\\C3N-01194\\01-26-2000-PET WB LOW BMI-26638");
        fileChooser.setApproveButtonText("Выбрать");
        fileChooser.setDialogTitle("Открыть");
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("DICOM files", "dcm");
        fileChooser.setFileFilter(filter);
        int selected = fileChooser.showOpenDialog(null);
        
        files = null;
        if(selected == JFileChooser.APPROVE_OPTION){
            files = fileChooser.getSelectedFiles();
        } else if (selected == JFileChooser.CANCEL_OPTION) {
            return -1;
        }
        
        try{
            System.out.println(getSpacingBetweenSlices(files[0]));
            //this.spacingBetweenSlices = Float.parseFloat(getSpacingBetweenSlices(files[0]));
            this.pixelSpacing = Float.parseFloat(getPixelSpacing(files[0]));
            this.shift = Vector3f.length(Vector3f.subtract(getImagePositionPatientVector(files[0]), getImagePositionPatientVector(files[files.length-1])));
        }catch(Exception e){
            JFrame f = new JFrame();
            f.setAlwaysOnTop(true);
            f.setTitle("Ошибка");
            JOptionPane.showMessageDialog(f, "Загружен неверный формат изображения (необходим .dcm)\nили в одном из загруженых файлах отсутствует обязательный DICOM атрибут!\nВычисление объема невозможно.");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.dispose();
            return -1;
        }
        
        JProgressBar pbar;
        // initialize Progress Bar
        pbar = new JProgressBar();
        pbar.setMinimum(0);
        pbar.setMaximum(files.length);
        // add to JPanel
        //add(pbar);
        
        JFrame frame = new JFrame("Загрузка файлов");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setContentPane(pbar);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        DCMrasters = new Raster[files.length];
        for (int i = 0; i < files.length; i++) {
            DCMrasters[i] = createRasterFromDICOMFile(files[i]);
            pbar.setValue(i);
        }
        
        int size = DCMrasters[0].getWidth() * DCMrasters[0].getHeight();
        colors = new int[files.length][size];  //Использовать байты??
        for (int i = 0; i < files.length; i++) {
            if (size != DCMrasters[i].getWidth() * DCMrasters[i].getHeight()){
                JFrame f = new JFrame();
                f.setAlwaysOnTop(true);
                f.setTitle("Ошибка");
                JOptionPane.showMessageDialog(f, "Изображения должны быть одного размера!");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.dispose();
                frame.setVisible(false);
                frame.dispose();
                return -1;
            }
            DCMrasters[i].getPixels(0, 0, DCMrasters[i].getWidth(), DCMrasters[i].getHeight(), colors[i]);
            pbar.setValue(i);
        }
        
        frame.setVisible(false);
        frame.dispose();
        
        renderer.setDataWidth(DCMrasters[0].getWidth());
        renderer.setDataHeight(DCMrasters[0].getHeight());
        renderer.setDataAmount(DCMrasters.length);
        
        float widthInMm = DCMrasters[0].getWidth() * this.pixelSpacing;
        float heightInMm = DCMrasters[0].getHeight()* this.pixelSpacing;
        
        renderer.setCorrection(this.shift / ((widthInMm + heightInMm) / 2));
        object.setScale(new Vector3f(1.0f, 1.0f, this.shift / ((widthInMm + heightInMm) / 2)));
        
        return 0;
    }
    
    public float volumeInVoxels(){
        int temp = (int)(treshold * 255);
        float volume = 
        IntStream.range(0, DCMrasters.length)
        .parallel()
        .mapToLong(r -> IntStream.range(0, DCMrasters[r].getHeight())
                .parallel()
                .mapToLong(y -> IntStream.range(0, DCMrasters[r].getWidth())
                        .parallel()
                        .filter(x -> DCMrasters[r].getSample(x, y, 0) >= temp)
                        .count())
                .sum())
        .sum();

        volume /= DCMrasters.length * DCMrasters[0].getHeight() * DCMrasters[0].getWidth();

        return volume;
    }
}
