/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package detectiontest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.opencv.core.Core;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

/**
 * Creates markers for an image.
 * 
 * @author palas
 */
public class Learner extends JPanel {
    
    private static final int NUM_CLASSES = 5;
    private static final int MARKER_RADIUS = 5;
    
    private static final String IMAGE_PATH = "/Users/palas/Dropbox/Pattern/DataSamples/multi2.tif";
    private static final String IMAGE_PATH2 = "/Users/palas/Dropbox/Pattern/DataSamples/multi3.tif";
    
    private int selectedClass = 0;
    
    private List<ClassMarker> descriptors = new ArrayList<>();
    
    private Mat rawImage = null;
    private BufferedImage image = null;
    private AffineTransform at;
    
    private List<Particle> particles;
    private List<Particle> assignedParticles = new ArrayList<>();
    private static final List<ParticleClass> particleClasses = new ArrayList();
    
    private JFrame classificationFrame;
    
    static{ 
        // load opencv library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
        
        // define the classes
        particleClasses.add(new ParticleClass("Name1", Color.GREEN));
        particleClasses.add(new ParticleClass("Name2", Color.BLUE));
        particleClasses.add(new ParticleClass("Name3", Color.RED));
        particleClasses.add(new ParticleClass("Name4", Color.ORANGE));
        particleClasses.add(new ParticleClass("Name5", Color.MAGENTA));
    }
    
    public Learner() {
        setLayout(new BorderLayout()); 
        rawImage = Highgui.imread(IMAGE_PATH, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        
        particles = ParticleDetector.detect(rawImage);
        Mat drawing = Highgui.imread(IMAGE_PATH, Highgui.CV_LOAD_IMAGE_COLOR);
        
        ImageDisplayer.drawParticles(drawing, particles);
        
        image = ImageDisplayer.toBufferedImage(drawing);
        
        if(image != null){
            setSize(image.getWidth(), image.getHeight());
        }
        
        addMouseListener(mMouseAdapter);
        addMouseMotionListener(mMouseAdapter);
        addMouseWheelListener(mMouseAdapter);
        addKeyListener(mKeyAdapter);
        
        double x = (getWidth() -  image.getWidth()) / 2;
        double y = (getHeight() - image.getHeight()) / 2;
        at = AffineTransform.getTranslateInstance(x, y);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame("Particle Clasificator Learner");

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(new Dimension(500, 500));

                frame.getContentPane().setLayout(new BorderLayout());
                Learner learner = new Learner();
                learner.setFocusable(true);

                frame.getContentPane().add(learner, BorderLayout.CENTER);
                frame.setPreferredSize(learner.getSize());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.drawRenderedImage(image, at);
        
        for (ClassMarker d : descriptors) {
            d.draw(g2, MARKER_RADIUS);
        }
        
        // draw selected class indicator
        g2.setColor(particleClasses.get(selectedClass).getColor());
        g2.drawString(particleClasses.get(selectedClass).getName(), 10, 20);
    }
    
    /**
     * Handles mouse action.
     */
    private final MouseAdapter mMouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            ClassMarker desc = new ClassMarker(e.getPoint(), particleClasses.get(selectedClass));
            assingToParticle(desc);
            descriptors.add(desc);
            repaint();
        }
    };
    
    /**
     * Handles keyboard action.
     */
    private final KeyAdapter mKeyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                selectedClass = ++selectedClass % NUM_CLASSES;
                repaint();
            }else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                runClassification(assignedParticles);
            }
        } 
    };
    
    /**
     * Runs classification on selected image.
     * 
     * @param particles 
     */
    public void runClassification(List<Particle> particles){
        ParticleClassificator cl  = new ParticleClassificator(particles, null);
        Mat image2 = Highgui.imread(IMAGE_PATH2, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        List<Particle> list = ParticleDetector.detect(image2);
        
        Mat drawing = Highgui.imread(IMAGE_PATH2, Highgui.CV_LOAD_IMAGE_COLOR);
        
        for (Particle p : list) {
            ParticleClass pClass = cl.clasifyNN(p);
            p.setParticleClass(pClass);
        }
       
        ImageDisplayer.drawParticles(drawing, list);
        if(classificationFrame != null){
            classificationFrame.setVisible(false);
            classificationFrame.dispose();
        }
        classificationFrame = ImageDisplayer.imshow(drawing, "clasified");   
    }
    
    /**
     * Assignes particle class to particle.
     * @param marker 
     */
    private void assingToParticle(ClassMarker marker){
        for (Particle particle : particles) {
            if(particle.contains(new org.opencv.core.Point(marker.p.getX(), marker.p.getY()))){
                particle.setParticleClass(particleClasses.get(selectedClass));
                assignedParticles.add(particle);
                return;
            }
        }
    }
    

    /**
     * Marker of the particle class. Represents the clicked point with proper
     * color.
     */
    public class ClassMarker{
        
        protected final Point2D p;
        protected final ParticleClass particleClass;

        public ClassMarker(Point p, ParticleClass particleClass) {
            this.p = new Point2D.Double(p.x, p.y);
            this.particleClass = particleClass;
        }

        public Point2D getP() {
            return p;
        }

        public ParticleClass getParticleClass() {
            return particleClass;
        }
        
        public void draw(Graphics2D g, int r) {
            int x = (int) p.getX() - r;
            int y = (int) p.getY() - r;
            Ellipse2D.Double circle = new Ellipse2D.Double(x, y, r, r);
            g.setColor(getParticleClass().getColor());
            g.fill(circle);
        }
    }
    
}
