/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.graphics;

import engine.io.Loader;
import java.awt.List;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author Timur
 */
public class GuiRenderer {
    
    private final RawModel quad;
    
    public GuiRenderer(Loader loader){
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = loader.loadToVAO(positions);
    }
    
    public void render(ArrayList<Integer> guis){
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        for(int gui: guis){
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
}
