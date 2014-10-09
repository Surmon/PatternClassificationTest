/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectiontest;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author palas
 */
public class Particle {
    
    private ParticleClass particleClass;
    private final Rect boundingRect;
    private final MatOfPoint contour;
    private final double area;
    
    public Particle(MatOfPoint contour){
        this.contour = contour;
        boundingRect = calcBoundingBox(contour);
        area = calcArea(contour);
    }

    public Rect getBoundingRect() {
        return boundingRect;
    }

    public MatOfPoint getContour() {
        return contour;
    }

    public ParticleClass getParticleClass() {
        return particleClass;
    }

    public void setParticleClass(ParticleClass particleClass) {
        this.particleClass = particleClass;
    }
    
    public static double calcArea(MatOfPoint contour){
        return Imgproc.contourArea(contour);
    }
    
    public static Rect calcBoundingBox(MatOfPoint contour){
        MatOfPoint2f curve = new MatOfPoint2f(contour.toArray());
        MatOfPoint2f curveApprox = new MatOfPoint2f();
        Imgproc.approxPolyDP(curve, curveApprox, 3, true);
        return Imgproc.boundingRect(new MatOfPoint(curveApprox.toArray()));
    }
    
    public boolean contains(Point point){
        return getBoundingRect().contains(point);
    }
  
    
    public double distanceTo(Particle p){
        double shapesDistance = Imgproc.matchShapes(
                contour, p.contour, Imgproc.CV_CONTOURS_MATCH_I3, 0);
        double areaDistance = area - p.area;
        
        return Math.sqrt(10*shapesDistance + areaDistance * areaDistance);               
    }
    
}
