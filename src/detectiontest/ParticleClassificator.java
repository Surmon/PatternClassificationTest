/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectiontest;

import java.util.List;

/**
 * Clasificator for pattern particles.
 * @author palas
 */
public class ParticleClassificator {
    
    private Integer threshold;
    private List<Particle> particles;

    public ParticleClassificator(List<Particle> particles, Integer threshold) {
        this.particles = particles;
        this.threshold = threshold;
    }
    
    /**
     * Nearest neigbour clasificator.
     * 
     * @param particle
     * @return class id
     */
    public ParticleClass clasifyNN(Particle particle){        
        double min = Double.MAX_VALUE;
        int indexOfNearest = 0, i = 0;
        for (Particle trainingParticle : particles) {
            double diff = particle.distanceTo(trainingParticle);
            if(diff < min){
               min = diff;
               indexOfNearest = i;
            }
            i++;
        }
         
        if( threshold == null || min > threshold){
            return particles.get(indexOfNearest).getParticleClass();
        }else{
            return null;
        }  
    }
    
}
