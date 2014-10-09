/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectiontest;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Pattern particle detector.
 * 
 * @author palas
 */
public class ParticleDetector {
    
    public static final int THRESHOLD = 150;
    public static final int MAX = 255;
    public static final Point ORIGIN = new Point(0, 0); 
    
    /**
     * Particle detection algorithm.
     * 
     * @param image an image where we want to detect
     * @return list of detected particles
     */
    public static List<Particle> detect(Mat image){
        
        // blur the image to denoise
        Imgproc.blur(image, image, new Size(3, 3));
        
        // thresholds the image
        Mat thresholded = new Mat();
        Imgproc.threshold(image, thresholded,
                THRESHOLD, MAX, Imgproc.THRESH_TOZERO_INV);
        
        // detect contours
        List<MatOfPoint> contours = new ArrayList<>();        
        Imgproc.findContours(
                thresholded,
                contours,
                new Mat(),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE,
                ORIGIN);
        
        // create particle from each contour
        List<Particle> particles = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            particles.add(new Particle(contour));
        }
        
        return particles;
    }
    
}
