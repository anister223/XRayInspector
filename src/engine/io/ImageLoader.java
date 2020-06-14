/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.io;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.io.DicomInputStream;

/**
 *
 * @author Timur
 */
public class ImageLoader {
    
    public static BufferedImage createBufferedImgFromDICOMFile(File dicomFile){
        Raster raster = null;
        System.out.println("Input: " + dicomFile.getName());
        
        // Open the DICOM file and get its pixel data
        try{
            Iterator iter = ImageIO.getImageReadersByFormatName("DICOM");
            ImageReader reader = (ImageReader) iter.next();
            DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
            ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);
            reader.setInput(iis, false);
            // Returns a new Raster (rectangular array of pixels) containing the raw pixel data from the image stream
            raster = reader.readRaster(0, param);
            if(raster == null)
                System.out.println("Error: couldn't read Dicom image!");
            iis.close();
            
        } catch(Exception e){
            System.out.println("Error: couldn't read dicom image! " + e.getMessage());
            e.printStackTrace();
        }

        return get16bitBuffImage(raster);
    }

    private static BufferedImage get16bitBuffImage(Raster raster) {
        short[] pixels = ((DataBufferUShort) raster.getDataBuffer()).getData();
        ColorModel colorModel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[]{16},
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_USHORT);
        DataBufferUShort db = new DataBufferUShort(pixels, pixels.length);
        WritableRaster outRaster = Raster.createInterleavedRaster(db,
                raster.getWidth(),
                raster.getHeight(),
                raster.getWidth(),
                1,
                new int[1],
                null);
        return new BufferedImage(colorModel, outRaster, false, null);
    }
    
    public static Raster createRasterFromDICOMFile(File dicomFile){
        Raster raster = null;
        System.out.println("Input: " + dicomFile.getName());
        
        // Open the DICOM file and get its pixel data
        try{
            //Iterator iter = ImageIO.getImageReadersByFormatName("DICOM");
            ImageReader reader = ImageIO.getImageReadersByFormatName("DICOM").next();//(ImageReader) iter.next();
            DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
            ImageInputStream iis = ImageIO.createImageInputStream(dicomFile);
            reader.setInput(iis);//, false);
            // Returns a new Raster (rectangular array of pixels) containing the raw pixel data from the image stream
            raster = reader.read(0, param).getRaster();
            
            if(raster == null)
                System.out.println("Error: couldn't read Dicom image!");
            iis.close();
        } catch(Exception e){
            System.out.println("Error: couldn't read dicom image! \n" + e.getMessage());
            e.printStackTrace();
        }

        return raster;
    }
}

