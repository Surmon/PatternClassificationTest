/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectiontest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author palas
 */
public class ImageDisplayer {
    
    public static void drawParticles(Mat image, List<Particle> particles){
        for (Particle particle : particles) {
            
            ParticleClass pClass = particle.getParticleClass();
            Scalar color = colorToScalar(Color.BLACK);
            if(pClass != null){
                color = colorToScalar(pClass.getColor());
            }
            List<MatOfPoint> list = new ArrayList<>();
            list.add(particle.getContour());
            Imgproc.drawContours(image, list, 0, color);
            Core.rectangle(image, particle.getBoundingRect().tl(), particle.getBoundingRect().br(), color);
        }
    }
    
    public static Scalar colorToScalar(Color c){
        return new Scalar(c.getBlue(), c.getGreen(), c.getRed());
    }
    
    public static BufferedImage toBufferedImage(Mat m){
      int type = BufferedImage.TYPE_BYTE_GRAY;
      if ( m.channels() > 1 ) {
          type = BufferedImage.TYPE_3BYTE_BGR;
      }
      int bufferSize = m.channels()*m.cols()*m.rows();
      byte [] b = new byte[bufferSize];
      m.get(0,0,b); // get all the pixels
      BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
      final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
      System.arraycopy(b, 0, targetPixels, 0, b.length);  
      return image;
    }
    
    public static JFrame imshow(Mat image, String name){
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(500, 500));

        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(toBufferedImage(image)));

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
        
        return frame;
    }
    
}
