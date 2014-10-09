/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectiontest;

import java.awt.Color;

/**
 * Represents the class of particles.
 * 
 * @author palas
 */
public class ParticleClass {
    
    private final String name;
    private final Color color;

    public ParticleClass(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
