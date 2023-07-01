package org.example.entity;

import org.example.NN.Chromosome;

public class Plant extends Entity {
    private float x;
    private float y;
    private float nutritionValue;
    private float size;

    public Plant(Chromosome chromosome,  float x, float y, float nutritionValue, float size) {
        super(chromosome, 0, x, y);
        this.x = x;
        this.y = y;
        this.size = size;
        this.nutritionValue = nutritionValue;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getNutritionValue() {
        return nutritionValue;
    }

    public void remove() {
        // Code to remove the plant from the simulation
    }

    public float getSize() {
        return size;
    }
}
