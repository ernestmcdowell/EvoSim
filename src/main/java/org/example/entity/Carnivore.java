package org.example.entity;

import org.example.NN.Chromosome;

public class Carnivore extends Entity {
    private float x;
    private float y;
    private float size;

    public Carnivore(Chromosome chromosome, float x, float y, float size) {
        super(chromosome);
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void graze() {
        // Implement herbivore grazing behavior based on the chromosome and other factors
    }

    // Add other herbivore-specific methods and properties
}
