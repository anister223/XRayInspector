package engine.graphics;

import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.MeshObject;
import engine.objects.gui.StbTtFontResource;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

public class Renderer {
    private Window window;
    private Shader shader, guiShader;
    private Shader fboShader;
    
    private int ssbo;
    
    private int sampleResolution = 250;
    private int dataWidth;
    private int dataHeight;
    private int dataAmount;
    
    private float brightness;
    private float treshold;
    
    private float correction;
    private boolean trillinearFilter;
    private float yClip = 0.5f;
    private float zClip = 0.5f;
    private float xClip = 0.5f;
    
    // Геометрия, текстура и переменные для рендеринга кадрового буффера
    int texture;
    int fbo;
    
    boolean shouldUpdate = true;
    boolean drawLast = true;
    
    RawModel quad = new RawModel(new Vertex[]{
        new Vertex(new Vector3f(-1.0f,  1.0f, 0.0f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 1.0f,  1.0f, 0.0f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f(-1.0f, -1.0f, 0.0f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f( 1.0f, -1.0f, 0.0f), new Vector2f(1.0f, 0.0f))
    }, new int[] {
        0, 1, 2, 3
    });
    
    // Axis Aligned Bounding Box
    Vector3f aabb1 = new Vector3f(-0.5f, -0.5f, -0.5f);
    Vector3f aabb2 = new Vector3f(xClip,  yClip,  zClip);
    
    public Renderer(Window window, Shader shader, Shader guiShader, Shader fboShader){
        this.window = window;
        this.shader = shader;
        this.guiShader = guiShader;
        this.fboShader = fboShader;
    }
    
    public void setYClip(float value){
        this.yClip = value;
        this.aabb2.setY(yClip);
        shouldUpdate = true;
    }
    
    public void setZClip(float value){
        this.zClip = value;
        this.aabb2.setZ(zClip * correction);
        shouldUpdate = true;
    }
    
    public void setXClip(float value){
        this.xClip = value;
        this.aabb2.setX(xClip);
        shouldUpdate = true;
    }
    
    public void setCorrection(float correction){
        this.correction = correction;
        this.aabb1 = new Vector3f(-0.5f, -0.5f, -(correction / 2));
        this.aabb2 = new Vector3f(xClip,  yClip,  zClip * correction);
        shouldUpdate = true;
    }
    
    // Метод для реинициализации кадрового буффера
    private void updFrameBuffer(){
        if (texture != 0) GL13.glDeleteTextures(texture);
        if (fbo != 0) GL30.glDeleteFramebuffers(fbo);
        
        // framebuffer configuration

        fbo = GL30.glGenFramebuffers();
        // create a color attachment texture
        texture = GL30.glGenTextures();
        GL30.glBindTexture(GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGB, window.getWidth(), window.getHeight(), 0, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE, (float[])null);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR); 
        /*
        int rbo;
        rbo = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, 1280, 720);
        */
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0); 
        //GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo);

        // execute victory dance
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0); 
        GL30.glBindTexture(GL_TEXTURE_2D, 0);
        //GL30.glDeleteFramebuffers(fbo);  

        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
            System.out.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
        /*
        glTexImage2D(
        GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, 800, 600, 0, 
        GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, NULL
        );

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, texture, 0);  
        */
    }
    
    // Метод для рендеринга полигонального объекта
    private void drawGeo(MeshObject object, Camera camera){
        GL30.glBindVertexArray(object.getMesh().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);

        // Alpha blending
        GL11.glEnable(GL11.GL_BLEND);
        //GL33.glBlendFunc(GL33.GL_ONE, GL33.GL_ONE_MINUS_SRC_ALPHA);         //If you use premultiplied alpha,
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);   //If you use non-premultiplied alpha,

        // Face culling
        GL11.glEnable(GL11.GL_CULL_FACE); // enables face culling    
        GL11.glCullFace(GL11.GL_BACK); // tells OpenGL to cull back faces (the sane default setting)
        GL11.glFrontFace(GL11.GL_CW); // tells OpenGL which faces are considered 'front' (use GL_CW or GL_CCW)

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());

        shader.bind();
        shader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));
        shader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
        shader.setUniform("projection", window.getProjectionMatrix());

        shader.setUniform("dataWidth", dataWidth);
        shader.setUniform("dataHeight", dataHeight);
        shader.setUniform("dataAmount", dataAmount);  //!

        shader.setUniform("screenHeight", window.getHeight());
        shader.setUniform("screenWidth", window.getWidth());
        shader.setUniform("aabb1", aabb1);
        shader.setUniform("aabb2", aabb2);
        shader.setUniform("cameraPos", camera.getPosition());
        shader.setUniform("sampleResolution", sampleResolution);

        shader.setUniform("brightness", brightness);
        shader.setUniform("treshold", treshold);
        
        shader.setUniform("correction", correction);

        shader.setUniform("useInterpolation", trillinearFilter);
        shader.setUniform("yClip", yClip);
        shader.setUniform("zClip", zClip);
        shader.setUniform("xClip", xClip);

        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);
        shader.unbind();

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL33.glDisable(GL33.GL_BLEND);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
    
    public void init(){
        updFrameBuffer();
        quad.create();
    }
    
    public void update(){
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, window.getWidth(), window.getHeight(), 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }
    
    public void destroy(){
        GL15.glDeleteBuffers(ssbo);
        GL13.glDeleteTextures(texture);
        GL30.glDeleteFramebuffers(fbo);
    }
    
    // Метод загрузки Shader Storage Buffer Object - буффер значений полученых
    // из снимков
    public void loadSSBO(short[][] colors){
        if (ssbo != 0) GL15.glDeleteBuffers(ssbo);
        
        int size = colors[0].length;
        long pixelSIZE = size * (Short.SIZE / 8);
        
        ssbo = GL15.glGenBuffers();
        GL33.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssbo);
        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, pixelSIZE * colors.length, GL15.GL_STATIC_DRAW);
        for (int i = 0; i < colors.length; i++) {
            GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, pixelSIZE * i, colors[i]);
        }
        GL33.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        GL33.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }
    
    // Главный метод для объемного рейкастинга
    public void renderMesh(MeshObject object, Camera camera){
        // Если буффер был инициализирован
        if (ssbo != 0) {
            // Если была произведена манипуляция объектом обновить кадровый
            // буфер и его рендеринг
            // Иначе рендеринг кадрового буфера без обновления
            if (camera.isModified || texture == 0 || window.isResized() || shouldUpdate) {
                if (window.isResized()) {
                    updFrameBuffer();
                }
                // bind to framebuffer and draw scene as we normally would to color texture
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbo);
                GL30.glEnable(GL30.GL_DEPTH_TEST);

                GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
                //
                this.trillinearFilter = false;
                drawGeo(object, camera);
                //
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
                GL30.glDisable(GL30.GL_DEPTH_TEST);

                GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);

                fboShader.bind();
                GL30.glBindVertexArray(quad.getVAO());
                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);
                GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture);
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertices().length);
                fboShader.unbind();
                GL20.glDisableVertexAttribArray(1);
                GL20.glDisableVertexAttribArray(0);
                shouldUpdate = false;
                drawLast = true;
            }else{
                // Если манипуляция закончилась на предыдущем кадре то
                // обновление кадрового буфера с высокими настройками
                // качества рендеринга
                if (drawLast) {
                    // bind to framebuffer and draw scene as we normally would to color texture
                    GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbo);
                    GL30.glEnable(GL30.GL_DEPTH_TEST);

                    GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
                    //
                    this.trillinearFilter = true;
                    drawGeo(object, camera);
                    //
                    GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
                    GL30.glDisable(GL30.GL_DEPTH_TEST);

                    GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
                    drawLast = false;
                }
                
                GL30.glDisable(GL30.GL_DEPTH_TEST);
                fboShader.bind();
                GL30.glBindVertexArray(quad.getVAO());
                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);
                GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture);
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertices().length);
                fboShader.unbind();
                GL20.glDisableVertexAttribArray(1);
                GL20.glDisableVertexAttribArray(0);
                GL30.glEnable(GL30.GL_DEPTH_TEST);
            }
        }
    }
    
    // Главный метод для рендеринга элементов графического интерфейса
    public final void renderGuiElement(RawModel gui, Vector3f color) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL30.glBindVertexArray(gui.getVAO());
        GL20.glEnableVertexAttribArray(0);
        guiShader.bind();
        guiShader.setUniform("c", color);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, gui.getVertices().length);
        guiShader.unbind();
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    
    // Метод для рендеринга текста
    public final void drawString(StbTtFontResource font, String text, float x, float y) {
        int fontSize = font.getPixelSize();
        y += font.getAscent();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer bufX = stack.floats(x);
            FloatBuffer bufY = stack.floats(y);

            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
            STBTTBakedChar.Buffer charData = font.getBakedCharData();
            
            // Face culling
            GL11.glEnable(GL11.GL_CULL_FACE); // enables face culling    
            GL11.glCullFace(GL11.GL_BACK); // tells OpenGL to cull back faces (the sane default setting)
            GL11.glFrontFace(GL11.GL_CW); // tells OpenGL which faces are considered 'front' (use GL_CW or GL_CCW)
            
            // set up textures + transparency
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glBindTexture(GL_TEXTURE_2D, font.getGlName());
            
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glColor4f(0.35f, 0.35f, 0.35f, 1.0f);

            int firstCP = StbTtFontResource.BAKE_FONT_FIRST_CHAR;
            int lastCP = StbTtFontResource.BAKE_FONT_FIRST_CHAR + StbTtFontResource.GLYPH_COUNT - 1;
            for (int i = 0; i < text.length(); i++) {
                int codePoint = text.codePointAt(i);
                if (codePoint == '\n') {
                    bufX.put(0, x);
                    bufY.put(0, y + bufY.get(0) + fontSize);
                    continue;
                } else if (codePoint < firstCP || codePoint > lastCP) {
                    continue;
                }
                
                STBTruetype.stbtt_GetBakedQuad(charData,
                        StbTtFontResource.FONT_TEX_W, StbTtFontResource.FONT_TEX_H,
                        codePoint - firstCP,
                        bufX, bufY, q, true);

                GL11.glTexCoord2f(q.s1(), q.t1()); GL11.glVertex2f(q.x1(), q.y1());
                GL11.glTexCoord2f(q.s0(), q.t1()); GL11.glVertex2f(q.x0(), q.y1());
                GL11.glTexCoord2f(q.s0(), q.t0()); GL11.glVertex2f(q.x0(), q.y0());
                GL11.glTexCoord2f(q.s0(), q.t0()); GL11.glVertex2f(q.x0(), q.y0());
                GL11.glTexCoord2f(q.s1(), q.t0()); GL11.glVertex2f(q.x1(), q.y0());
                GL11.glTexCoord2f(q.s1(), q.t1()); GL11.glVertex2f(q.x1(), q.y1());
            }
            GL11.glEnd();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_ALPHA);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public void setDataWidth(int dataWidth) {
        this.dataWidth = dataWidth;
    }

    public void setDataHeight(int dataHeight) {
        this.dataHeight = dataHeight;
    }

    public void setDataAmount(int dataAmount) {
        this.dataAmount = dataAmount;
    }
    
    public void setTreshold(float value){
        this.treshold = value;
        this.shouldUpdate = true;
    }
}
