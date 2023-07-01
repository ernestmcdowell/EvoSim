package org.example.entity;

import org.example.NN.Chromosome;
import org.example.NN.NN;

import java.util.List;

public class Herbivore extends Entity {
    private float x;
    private float y;
    private float size;
    private NN nn;
    List<Carnivore> allCarnivores;

    public Herbivore(Chromosome chromosome, float x, float y, float size) {
        super(chromosome, 0);
        this.x = x;
        this.y = y;
        this.size = size;
        this.nn = new NN();
        this.nn.Awake();
        nn.initializeWithWeightsAndBiases(chromosome.getWeightsForNN(), chromosome.getBiasesForNN());
    }

    public void updateState(float[] worldState) {
        float[] decision = nn.Brain(worldState);
        // Update the creature's state based on the decision
        // For example, if the decision is a direction to move in:
        this.x += decision[0];
        this.y += decision[1];
    }

    public void move() {
        float[] inputs = getInputs(); // Get inputs for the neural network
        float[] outputs = nn.Brain(inputs); // Feed inputs through the neural network

        // Interpret the output of the neural network as a direction vector
        float dx = outputs[0];
        float dy = outputs[1];

        // Move in the opposite direction to avoid carnivores
        x -= dx;
        y -= dy;
    }

    public float[] getInputs() {
        // Example inputs for the neural network:
        // 1. The x and y coordinates of the herbivore
        // 2. The distance to the nearest carnivore
        // 3. The angle to the nearest carnivore

        float nearestCarnivoreDistance = calculateNearestCarnivoreDistance();
        float nearestCarnivoreAngle = calculateNearestCarnivoreAngle();

        return new float[] {x, y, nearestCarnivoreDistance, nearestCarnivoreAngle};
    }

    private float calculateNearestCarnivoreDistance() {
        float minDistance = Float.MAX_VALUE;
        for (Carnivore carnivore : allCarnivores) {
            float dx = carnivore.getX() - this.x;
            float dy = carnivore.getY() - this.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private float calculateNearestCarnivoreAngle() {
        float minDistance = Float.MAX_VALUE;
        float minAngle = 0.0f;
        for (Carnivore carnivore : allCarnivores) {
            float dx = carnivore.getX() - this.x;
            float dy = carnivore.getY() - this.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < minDistance) {
                minDistance = distance;
                minAngle = (float) Math.atan2(dy, dx);
            }
        }
        return minAngle;
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
