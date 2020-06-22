/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.objects.gui;

/**
 *
 * @author Timur
 */
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
 
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
 
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
 
public class StbTtFontResource {
     
    public static final int CHAR_DATA_MALLOC_SIZE = 96;
    public static final int FONT_TEX_W = 512;
    public static final int FONT_TEX_H = FONT_TEX_W;
    public static final int BAKE_FONT_FIRST_CHAR = 32;
    public static final int GLYPH_COUNT = CHAR_DATA_MALLOC_SIZE;
     
    protected final STBTTBakedChar.Buffer charData;
    protected final STBTTFontinfo fontInfo;
    protected final int fontHeight;
    protected final int texGlName;
    protected final float ascent;
    protected final float descent;
    protected final float lineGap;
    protected final String fontName;
    protected boolean disposed;
    
    private int windowWidth = 1280;
    private int windowHeight = 720;
     
    public StbTtFontResource(File ttfFile, int fontHeight) {
        
        /*
 unsigned char ttf_buffer[1<<20];
 unsigned char temp_bitmap[512*512];
 
 stbtt_bakedchar cdata[96]; // ASCII 32..126 is 95 glyphs
 GLuint ftex;
 
 void my_stbtt_initfont(void)
 {
    fread(ttf_buffer, 1, 1<<20, fopen("c:/windows/fonts/times.ttf", "rb"));
    stbtt_BakeFontBitmap(ttf_buffer,0, 32.0, temp_bitmap,512,512, 32,96, cdata); // no guarantee this fits!
    // can free ttf_buffer at this point
    glGenTextures(1, &ftex);
    glBindTexture(GL_TEXTURE_2D, ftex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, 512,512, 0, GL_ALPHA, GL_UNSIGNED_BYTE, temp_bitmap);
    // can free temp_bitmap at this point
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
 }
 
 void my_stbtt_print(float x, float y, char *text)
 {
    // assume orthographic projection with units = screen pixels, origin at top left
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, ftex);
    glBegin(GL_QUADS);
    while (*text) {
       if (*text >= 32 && *text < 128) {
          stbtt_aligned_quad q;
          stbtt_GetBakedQuad(cdata, 512,512, *text-32, &x,&y,&q,1);//1=opengl & d3d10+,0=d3d9
          glTexCoord2f(q.s0,q.t1); glVertex2f(q.x0,q.y0);
          glTexCoord2f(q.s1,q.t1); glVertex2f(q.x1,q.y0);
          glTexCoord2f(q.s1,q.t0); glVertex2f(q.x1,q.y1);
          glTexCoord2f(q.s0,q.t0); glVertex2f(q.x0,q.y1);
       }
       ++text;
    }
    glEnd();
 }*/
        
        fontName = ttfFile.getName();
        this.fontHeight = fontHeight;
        charData = STBTTBakedChar.malloc(CHAR_DATA_MALLOC_SIZE);
        fontInfo = STBTTFontinfo.create();
        int texGlName = 0;
        float ascent = 0, descent = 0, lineGap = 0;
        try {
            ByteBuffer ttfFileData = loadFileToByteBuffer(ttfFile);
            //ByteBuffer ttfFileData = IOUtil.ioResourceToByteBuffer(ttfFile, ttfFileData.limit());
            //ByteBuffer ttfFileData = FileLoader.loadResurce(ttfFile);
            ByteBuffer texData = BufferUtils.createByteBuffer(FONT_TEX_W * FONT_TEX_H);
            int result = STBTruetype.stbtt_BakeFontBitmap(ttfFileData, fontHeight, texData, FONT_TEX_W, FONT_TEX_H, BAKE_FONT_FIRST_CHAR, charData);
            if (result < 1) {
                System.err.println("stbtt_BakeFontBitmap failed with return value: "+result);
            }
            /*
            ByteBuffer bb1 = texData.asReadOnlyBuffer(); 
            bb1.rewind();
            byte[] imageData = bb1.array();
            
            JFrame editorFrame = new JFrame("Image Demo");
            editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            BufferedImage image = null;
            InputStream bais = new ByteArrayInputStream(imageData);
                image = ImageIO.read(bais);
            ImageIcon imageIcon = new ImageIcon(image); 
            JLabel jLabel = new JLabel();
            jLabel.setIcon(imageIcon);
            editorFrame.getContentPane().add(jLabel, BorderLayout.CENTER);
            */
             /*
            try (MemoryStack stack = MemoryStack.stackPush()) {
                STBTruetype.stbtt_InitFont(fontInfo, ttfFileData);
                float pixelScale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, fontHeight);
                 
                IntBuffer bufAscent = stack.ints(0);
                IntBuffer bufDescent = stack.ints(0);
                IntBuffer bufLineGap = stack.ints(0);
                STBTruetype.stbtt_GetFontVMetrics(fontInfo, bufAscent, bufDescent, bufLineGap);
                ascent = bufAscent.get(0) * pixelScale;
                descent = bufDescent.get(0) * pixelScale;
                lineGap = bufLineGap.get(0) * pixelScale;
            }
             */
            texGlName = GL11.glGenTextures();
            GL11.glBindTexture(GL_TEXTURE_2D, texGlName);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, FONT_TEX_W, FONT_TEX_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, texData);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.texGlName = texGlName;
        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
    }
    
    public void update(int windowWidth, int windowHeight){
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }
     
    public int getGlName() {
        return texGlName;
    }
     
    public STBTTBakedChar.Buffer getBakedCharData() {
        return charData;
    }
     
    public float getAscent() {
        return ascent;
    }
     
    public float getDescent() {
        return descent;
    }
     
    public float getLineGap() {
        return lineGap;
    }
     
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        charData.free();
        fontInfo.free();
        if (texGlName != 0) {
            GL11.glDeleteTextures(texGlName);
        }
    }
     
    public boolean isDisposed() {
        return disposed;
    }
     
    @Override
    protected void finalize() {
        dispose();
    }
     
    public String getName() {
        return fontName;
    }
     
    public int getPixelSize() {
        return fontHeight;
    }
     
    public static int getCodePoint(String text, int length, int index, IntBuffer out) {
        char c1 = text.charAt(index);
        if (Character.isHighSurrogate(c1) && index + 1 < length) {
            char c2 = text.charAt(index + 1);
            if (Character.isLowSurrogate(c2)) {
                out.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        out.put(0, c1);
        return 1;
    }
     
    /*
    public PSize getSize(String text, MutablePSize result) {
        int width = 0;
        int idx = 0;
        int length = text.length();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer codePnt   = stack.mallocInt(1);
            IntBuffer adv       = stack.mallocInt(1);
            IntBuffer lsb       = stack.mallocInt(1);
             
            while (idx < length) {
                idx += StbTtFontResource.getCodePoint(text, length, idx, codePnt);
                int codepoint = codePnt.get(0);
                 
                STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, codepoint, adv, lsb);
                width += adv.get(0);
            }
        }
        width = (int) (width * STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, fontHeight) + 0.5f);
        if (result == null) {
            return new ImmutablePSize(width, fontHeight);
        } else {
            result.set(width, fontHeight);
            return result;
        }
    }
    */
     
    public static class GlyphDim {
        public final float width, height;
        public GlyphDim(float x0, float y0, float x1, float y1) {
            width = Math.abs(x0 - x1);
            height = Math.abs(y0 - y1);
        }
        public GlyphDim(float w, float h) {
            width = w;
            height = h;
        }
    }
     
    public static ByteBuffer loadFileToByteBuffer(File file) {
        ByteBuffer buffer = null;
        /*
        try{
            InputStream is = new FileInputStream(file);
            byte[] bytes = IOUtils.readFully(is, GL_ALPHA, true);
            buffer = ByteBuffer.wrap(bytes);
            
        }catch(Exception e){
            
        }
        */
        try{
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        }catch(Exception e){}
        return buffer;
    }
    
    public final void drawString(StbTtFontResource fontRes, String text, float x, float y) {
        int fontSize = fontRes.getPixelSize();
        y += fontRes.getAscent();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer bufX = stack.floats(x);
            FloatBuffer bufY = stack.floats(y);

            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
            STBTTBakedChar.Buffer charData = fontRes.getBakedCharData();
            
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, windowWidth, 0, windowHeight, -1, 1);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            // set up textures + transparency
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                
            GL11.glEnable(GL_TEXTURE_2D);
            GL11.glBindTexture(GL_TEXTURE_2D, fontRes.getGlName());
            
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

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

                GL11.glTexCoord2f(q.s0(), q.t0());
                GL11.glVertex2f(q.x0(), q.y0());

                GL11.glTexCoord2f(q.s0(), q.t1());
                GL11.glVertex2f(q.x0(), q.y1());

                GL11.glTexCoord2f(q.s1(), q.t1());
                GL11.glVertex2f(q.x1(), q.y1());

                GL11.glTexCoord2f(q.s1(), q.t1());
                GL11.glVertex2f(q.x1(), q.y1());

                GL11.glTexCoord2f(q.s1(), q.t0());
                GL11.glVertex2f(q.x1(), q.y0());

                GL11.glTexCoord2f(q.s0(), q.t0());
                GL11.glVertex2f(q.x0(), q.y0());
            }
            GL11.glEnd();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
    }
}
