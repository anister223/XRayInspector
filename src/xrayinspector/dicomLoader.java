/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xrayinspector;

import java.io.File;
import java.io.IOException;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.util.SafeClose;

/**
 *
 * @author Timur
 */
public class dicomLoader {
    
    private static Attributes loadDicomObject(File f) throws IOException {
        if (f == null)
            return null;
        DicomInputStream dis = new DicomInputStream(f);
        try {
            return dis.readDataset(-1, -1);
        } finally {
            SafeClose.close(dis);
        }
    }
}
