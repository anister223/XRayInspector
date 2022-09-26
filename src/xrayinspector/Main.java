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
    public Shader shader, guiShader, fboShader;
    public final int WIDTH = 1280, HEIGHT = 720;
    
    // Объявление переменных пользовательского интерфейса
    public Layout layout;
    private UIBlock block;
    private UITextField textField;
    private UITextField textField2;
    private UIButton buttonOpenFiles;
    private UIButton button;
    private UISlider slider;
    private UISlider sliderTest, sliderZClip, sliderXClip;
    private UICheckBox checkBox;
    
    // Объявленние переменных для работы с данными
    String path = "E:\\XRAYshots\\CPTAC-LSCC\\C3N-01194\\01-26-2000-PET WB LOW BMI-26638";
    private File[] files = null;
    private Raster[] DCMrasters;
    short[][] colors;
    short[][] colors2;
    int dataWidth, dataHeight;
    
    float spacingBetweenSlices;
    float pixelSpacing;
    float shift;
    
    private float brightness;
    private float treshold;
    
    //public Mesh[] model = ModelLoader.loadModel("resources/models/cube.obj", "/textures/1-001.png");
    
    public Mesh mesh = new Mesh(new Vertex[] {
        //Задняя грань объема рендеринга
        new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),

        //Передняя грань
        new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 0.0f)),

        //Правая грань
        new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),

        //Левая грань
        new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),

        //Верхняя грань
        new Vertex(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),

        //Нижняя грань
        new Vertex(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 0.0f)),
    }, new int[] {
        //Задняя грань
        0, 1, 2,	
        0, 2, 3,	

        //Передняя грань
        4, 5, 6,
        4, 6, 7,

        //Правая грань
        8, 9, 10,
        8, 10, 11,

        //Левая грань
        12, 13, 14,
        12, 14, 15,

        //Верхняя грань
        16, 17, 18,
        16, 18, 19,

        //Нижняя грань
        20, 21, 22,
        20, 22, 23
    });//, new Material("/textures/1-001.png"));
    
    public MeshObject object = new MeshObject(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), mesh);
    
    public void start() {
        int t;
        mainThread = new Thread(this, "mainThread");
        mainThread.start();
    }
    
    public void init(){
        // Инициализация
        window = new Window(WIDTH, HEIGHT, "XRayInspector");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/rayCastingVolumeFragment_Shaded.glsl");
        guiShader = new Shader("/shaders/guiVertexShader.txt", "/shaders/guiFragmentShader.txt");
        fboShader = new Shader("/shaders/fboVertex.glsl", "/shaders/fboFragment.glsl");
        renderer = new Renderer(window, shader, guiShader, fboShader);
        
        window.setBackgroundColor(0.0f, 0.0f, 0.0f);
        window.create();
        mesh.create();
        shader.create();
        guiShader.create();
        fboShader.create();
        
        // Инициализация элементов интерфейса
        layout = new Layout(window);
        
        // Фон панели интерфейса
        block = new UIBlock(Color3f.SOARING_EAGLE);
        UIConstraints constraints5 = new UIConstraints();
        constraints5.setX(new PixelConstraint(50, Constraint.BORDER_RIGHT));
        constraints5.setY(new CenterConstraint());
        constraints5.setWidth(new RelativeConstraint(0.225f));
        constraints5.setHeight(new RelativeConstraint(0.9f));
        layout.Add(block, constraints5);
        
        // Кнопка расчета объема
        button = new UIButton(105, 32, Color3f.WHITE);
        UIConstraints constraints = new UIConstraints();
        constraints.setX(new PixelConstraint(50, Constraint.BORDER_RIGHT));
        constraints.setY(new PixelConstraint(225, Constraint.BORDER_TOP));
        constraints.setWidth(new RelativeConstraint(0.1f));
        constraints.setHeight(new RelativeConstraint(0.1f));
        layout.Add(button, constraints);
        
        // Ползунок установки порога
        slider = new UISlider();
        slider.setMax(1.0f - 1.0f / 128);
        UIConstraints constraints2 = new UIConstraints();
        constraints2.setX(new RelativeConstraint(0.875f));
        constraints2.setY(new PixelConstraint(150, Constraint.BORDER_TOP));
        constraints2.setWidth(new RelativeConstraint(0.175f));
        constraints2.setHeight(new RelativeConstraint(0.01f));
        layout.Add(slider, constraints2);
        
        // Текстовое поле вывода значения ползунка установки порога
        textField2 = new UITextField(105, 32, String.valueOf(slider.getMax()));
        UIConstraints constraints8 = new UIConstraints();
        constraints8.setX(new RelativeConstraint(0.875f));
        constraints8.setY(new PixelConstraint(175, Constraint.BORDER_TOP));
        constraints8.setWidth(new RelativeConstraint(0.05f));
        constraints8.setHeight(new RelativeConstraint(0.05f));
        layout.Add(textField2, constraints8);
        
        // Ползунок установки среза по оси Y
        sliderTest = new UISlider();
        UIConstraints constraints7 = new UIConstraints();
        constraints7.setX(new RelativeConstraint(0.875f));
        constraints7.setY(new PixelConstraint(275, Constraint.BORDER_TOP));
        constraints7.setWidth(new RelativeConstraint(0.175f));
        constraints7.setHeight(new RelativeConstraint(0.01f));
        layout.Add(sliderTest, constraints7);
        
        // Ползунок установки среза по оси Z
        sliderZClip = new UISlider();
        UIConstraints constraints9 = new UIConstraints();
        constraints9.setX(new RelativeConstraint(0.875f));
        constraints9.setY(new PixelConstraint(300, Constraint.BORDER_TOP));
        constraints9.setWidth(new RelativeConstraint(0.175f));
        constraints9.setHeight(new RelativeConstraint(0.01f));
        layout.Add(sliderZClip, constraints9);
        
        // Ползунок установки среза по оси X
        sliderXClip = new UISlider();
        UIConstraints constraints10 = new UIConstraints();
        constraints10.setX(new RelativeConstraint(0.875f));
        constraints10.setY(new PixelConstraint(325, Constraint.BORDER_TOP));
        constraints10.setWidth(new RelativeConstraint(0.175f));
        constraints10.setHeight(new RelativeConstraint(0.01f));
        layout.Add(sliderXClip, constraints10);
        
        // Текстовое поле вывода значения объёма
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
        
        // Кнопка для загрузки файлов
        buttonOpenFiles = new UIButton(105, 32, Color3f.WHITE);
        UIConstraints constraints6 = new UIConstraints();
        constraints6.setX(new PixelConstraint(50, Constraint.BORDER_RIGHT));
        constraints6.setY(new PixelConstraint(150, Constraint.BORDER_BOTTOM));
        constraints6.setWidth(new RelativeConstraint(0.1f));
        constraints6.setHeight(new RelativeConstraint(0.1f));
        layout.Add(buttonOpenFiles, constraints6);
        
        layout.create();
        renderer.init();
        this.treshold = slider.getValue();
        renderer.setTreshold(1.0f - this.treshold);
        
        // Привязка методов к событиям
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
        
        sliderTest.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSliderBrightnessActionPerformed(evt);
            }
        });
        
        sliderZClip.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSliderZClipActionPerformed(evt);
            }
        });
        
        sliderXClip.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uiSliderXClipActionPerformed(evt);
            }
        });
        
        checkBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxActionPerformed(evt);
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
        }
        close();
    }
    
    private void update(){
        window.update();
        renderer.update();
        // Не вращать камеру если курсок установлен в области интерфейса
        if(!(Input.getMouseX() > window.getWidth() * (1 - 0.3f)))
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
        long loadTime = System.currentTimeMillis();
        // Если загрузка не была произведена то сразу выход
        if (DCMrasters == null) return;
        float widthInMm = DCMrasters[0].getWidth() * this.pixelSpacing;
        float heightInMm = DCMrasters[0].getHeight()* this.pixelSpacing;
        double volume = volumeInVoxels() * (this.shift * widthInMm * heightInMm) / 1000.0f;
        
        DecimalFormat decimalFormat = new DecimalFormat("#0.000");
        String numberAsString = decimalFormat.format(volume);
        this.textField.setText("Volume is: " + numberAsString + "cm");
        System.out.println("Время подсчета объема:" + (System.currentTimeMillis() - loadTime) / 1000.0f);
    }
    
    private void uiButtonOpenFilesActionPerformed(java.awt.event.ActionEvent evt){
        // Если загрузка файлов удалась то загрузить данные в
        // Shader Storage Buffer Object для последующего рендеринга
        if (loadFiles() == 0) {
            renderer.loadSSBO(colors);
        }
    }
    
    private void uiSliderActionPerformed(java.awt.event.ActionEvent evt){
        this.treshold = slider.getValue();
        this.textField2.setText(String.valueOf(slider.getValue()));
        renderer.setTreshold(1.0f - this.treshold);
    }
    
    private void uiSliderBrightnessActionPerformed(java.awt.event.ActionEvent evt){
        renderer.setYClip(sliderTest.getValue() - 0.5f);
    }
    
    private void uiSliderZClipActionPerformed(java.awt.event.ActionEvent evt){
        renderer.setZClip(sliderZClip.getValue() - 0.5f);
    }
    
    private void uiSliderXClipActionPerformed(java.awt.event.ActionEvent evt){
        renderer.setXClip(sliderXClip.getValue() - 0.5f);
    }
    
    private void checkBoxActionPerformed(java.awt.event.ActionEvent evt){
        //renderer.setTrillinearFilter(checkBox.getState());
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
        
        JFileChooser fileChooser = new JFileChooser(path);
        fileChooser.setApproveButtonText("Выбрать");
        fileChooser.setDialogTitle("Открыть");
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("DICOM files", "dcm");
        fileChooser.setFileFilter(filter);
        // Открытие окна загрузки файлов
        int selected = fileChooser.showOpenDialog(null);
        
        // Если пользователь нажал "ОК" и выбрал файлы то получить пути к файлам
        // В обратном случае вернуть код ошибки
        if(selected == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFiles() != null){
            files = fileChooser.getSelectedFiles();
            path = files[0].getPath();
        } else {
            return -1;
        }
        
        long loadTime = System.currentTimeMillis();
        // Попытся получить аттрибуты DICOM файла
        try{
            System.out.println(getSpacingBetweenSlices(files[0]));
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
        
        // Инициализировать индикатор загрузки
        JProgressBar pbar;
        pbar = new JProgressBar();
        pbar.setMinimum(0);
        pbar.setMaximum(files.length);
        
        JFrame pbarFrame = new JFrame("Загрузка файлов");
        pbarFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pbarFrame.setUndecorated(true);
        pbarFrame.setContentPane(pbar);
        pbarFrame.pack();
        pbarFrame.setLocationRelativeTo(null);
        pbarFrame.setVisible(true);
        
        // Загрузить растр изображений в массив
        DCMrasters = new Raster[files.length];
        for (int i = 0; i < files.length; i++) {
            DCMrasters[i] = createRasterFromDICOMFile(files[i]);
            pbar.setValue(i);
        }
        
        dataWidth = DCMrasters[0].getWidth();
        dataHeight = DCMrasters[0].getHeight();
        int size = dataWidth * dataHeight;
        colors = new short[files.length][size];
        for (int i = 0; i < files.length; i++) {
            // Проверка на однородность размера
            if (size != DCMrasters[i].getWidth() * DCMrasters[i].getHeight()){
                JFrame f = new JFrame();
                f.setAlwaysOnTop(true);
                f.setTitle("Ошибка");
                JOptionPane.showMessageDialog(f, "Изображения должны быть одного размера!");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.dispose();
                pbarFrame.setVisible(false);
                pbarFrame.dispose();
                return -1;
            }
            // Загрузка значений пикселов в массив значений
            int[] sh = new int[size];
            DCMrasters[i].getPixels(0, 0, dataWidth, dataHeight, sh);
            for (int j = 0; j < sh.length; j++) {
                colors[i][j] = (short)sh[j];
            }
            pbar.setValue(i);
        }
        pbar.setMaximum(dataWidth);
        // Создание дублирующих данных ориентированых на ось X
        // Которые занимают старший байт в том же массиве short
        int size2 = files.length * dataHeight;
        for (int z = 0; z < dataWidth; z++) {
            for (int y = 0; y < dataHeight; y++) {
                for (int x = 0; x < files.length; x++) {
                    int index = x + y * files.length + z * size2;
                    short a = colors[x][z + y * dataWidth];
                    colors[index / size][index % size] += a << 8;
                }
            }
            pbar.setValue(z);
        }
        pbarFrame.setVisible(false);
        pbarFrame.dispose();
        
        renderer.setDataWidth(dataWidth);
        renderer.setDataHeight(dataHeight);
        renderer.setDataAmount(files.length);
        
        float widthInMm = dataWidth * this.pixelSpacing;
        float heightInMm = dataHeight * this.pixelSpacing;
        
        // Задать коррекцию объема для рендера по оси Z в соответствии с
        // физической глубиной
        renderer.setCorrection(this.shift / ((widthInMm + heightInMm) / 2));
        object.setScale(new Vector3f(1.0f, 1.0f, this.shift / ((widthInMm + heightInMm) / 2)));
        
        System.out.println("Время загрузки:" + (System.currentTimeMillis() - loadTime) / 1000.0f);
        
        return 0;
    }
    
    // Метод для рассчета объёма в колличестве пикселей
    public float volumeInVoxels(){
        int temp = (int)((1.0f - treshold) * 255);
        float volume = 
        IntStream.range(0, files.length)
        .parallel()
        .mapToLong(r -> IntStream.range(0, DCMrasters[r].getHeight())
                .parallel()
                .mapToLong(y -> IntStream.range(0, DCMrasters[r].getWidth())
                        .parallel()
                        .filter(x -> DCMrasters[r].getSample(x, y, 0) >= temp)
                        .count())
                .sum())
        .sum();
        volume /= DCMrasters.length * dataHeight * dataWidth;

        return volume;
    }
}
