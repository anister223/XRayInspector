/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.graphics;

import engine.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Timur
 */
public class Configuration {
    
    //private ArrayList<Vertex[]> indices;
    private int[][] indices;
    
    private Vertex[] edgeMids = new Vertex[]{
        new Vertex(new Vector3f(0.5f, 0.0f, 0.0f)),
        new Vertex(new Vector3f(1.0f, 0.0f, 0.5f)),
        new Vertex(new Vector3f(0.5f, 0.0f, 1.0f)),
        new Vertex(new Vector3f(0.0f, 0.0f, 0.5f)),
        
        new Vertex(new Vector3f(0.5f, 1.0f, 0.0f)),
        new Vertex(new Vector3f(1.0f, 1.0f, 0.5f)),
        new Vertex(new Vector3f(0.5f, 1.0f, 1.0f)),
        new Vertex(new Vector3f(0.0f, 1.0f, 0.5f)),
        
        new Vertex(new Vector3f(0.0f, 0.5f, 0.0f)),
        new Vertex(new Vector3f(1.0f, 0.5f, 0.0f)),
        new Vertex(new Vector3f(1.0f, 0.5f, 1.0f)),
        new Vertex(new Vector3f(0.0f, 0.5f, 1.0f)),
        
        new Vertex(new Vector3f(0.5f, 0.5f, 0.5f))
    }
            
            ;
    
    public Configuration(){
        //CLock wise
        indices[0] = new int[]{};   //case 0
        indices[1] = new int[]{     //case 1
            8, 0, 3
        };
        indices[2] = new int[]{     //case 2
            8, 9, 1,
            8, 1, 3
        };
        indices[3] = new int[]{     //case 3.1
            8, 0, 3,
            4, 5, 9
        };
        indices[3] = new int[]{     //case 3.2
            8, 4, 3,
            4, 5, 3,
            5, 9, 3,
            9, 0, 3
        };
        indices[4] = new int[]{     //case 4.1
            8, 0, 3,
            6, 5, 10,
        };
        indices[5] = new int[]{     //case 4.2
            8, 5, 0,
            0, 10, 3,
            3, 6, 8,
            5, 0, 10,
            10, 3, 6,
            6, 3, 8
        };
        indices[3] = new int[]{     //case 5
            3, 11, 9,
            3, 9, 0,
            11, 10, 9
        };
        indices[3] = new int[]{     //case 6.1.1
            3, 8, 1,
            8, 9, 1,
            6, 5, 10
        };
        indices[3] = new int[]{     //case 6.1.2 ?
            8, 5, 9,
            9, 5, 1,
            5, 10, 1,
            10, 1, 3,
            6, 10, 3,
            6, 3, 8,
            6, 5, 10
        };
        indices[3] = new int[]{     //case 6.2
            8, 4, 3,
            4, 5, 3,
            5, 9, 3,
            9, 0, 3
        };
        indices[3] = new int[]{     //case 7.1
            7, 4, 8,
            0, 9, 1,
            6, 5, 10
        };
        indices[3] = new int[]{     //case 7.2
            9, 1, 0,
            4, 5, 10,
            4, 10, 8,
            7, 10, 8,
            7, 6, 10
        };
        indices[3] = new int[]{     //case 7.3
            4, 12, 8,
            4, 5, 12,
            5, 9, 12,
            9, 0, 12,
            12, 1, 0,
            12, 10, 1,
            6, 10, 12,
            7, 6, 12,
            7, 12, 8
        };
        indices[3] = new int[]{     //case 7.4.1
            
        };
    }
    
    
    
    public Vertex[] getConfiguration(short index, Vector3f pos){
        Vertex[] vertices = null;
        
        return vertices;
    }
    
    public int computeIndex(){
        return 0;
    }
}
