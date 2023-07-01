package org.example.entity;

import org.example.NN.Chromosome;
import org.example.NN.NN;

import java.util.List;

public class Carnivore extends Entity {
    private float x;
    private float y;
    private float size;
    private NN nn;
    List<Herbivore> allHerbivores;

    public Carnivore(Chromosome chromosome, float x, float y, float size) {
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
        // 1. The x and y coordinates of the carnivore
        // 2. The distance to the nearest herbivore
        // 3. The angle to the nearest herbivore

        float nearestHerbivoreDistance = calculateNearestHerbivoreDistance();
        float nearestHerbivoreAngle = calculateNearestHerbivoreAngle();

        return new float[] {x, y, nearestHerbivoreDistance, nearestHerbivoreAngle};
    }

    private float calculateNearestHerbivoreDistance() {
        float minDistance = Float.MAX_VALUE;
        for (Herbivore herbivore : allHerbivores) {
            float dx = herbivore.getX() - this.x;
            float dy = herbivore.getY() - this.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private float calculateNearestHerbivoreAngle() {
        float minDistance = Float.MAX_VALUE;
        for (Herbivore herbivore : allHerbivores) {
            float dx = herbivore.getX() - this.x;
            float dy = herbivore.getY() - this.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
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
