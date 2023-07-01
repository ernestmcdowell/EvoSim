package org.example.entity;

import org.example.NN.Chromosome;
import org.example.NN.NN;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Herbivore extends Entity {
    private float x;
    private float y;
    private float size, rotation;
    private NN nn;
    private static final float MOVE_SPEED = 0.001f;
    private static final float ROTATE_SPEED = 0.1f;
    private static final float MIN_X = -5.0f;
    private static final float MAX_X = 5.0f;
    private static final float MIN_Y = -5.0f;
    private static final float MAX_Y = 5.0f;
    List<Carnivore> allCarnivores;


    public Herbivore(Chromosome chromosome, float x, float y, float size) {
        super(chromosome, 0, x , y);
        this.x = x;
        this.y = y;
        this.size = size;
        this.nn = new NN();
        this.nn.Awake();
    }

    public void Update() {
        if (!isAlive()) {
            // Handle removing entity
            return;
        }

        float[] inputs = getInputs();
        float[] decision = nn.Brain(inputs);

        // Update the state and move the herbivore based on the decision
        updateState(inputs);

        // Move towards the nearest plant
        Plant nearestPlant = findNearestPlant();
        if (nearestPlant != null) {
            float dx = nearestPlant.getX() - x;
            float dy = nearestPlant.getY() - y;
            float distanceToPlant = (float) Math.sqrt(dx * dx + dy * dy);

            // If the herbivore is close enough to the plant, eat it and reward the neural network
            if (distanceToPlant < size) {
                eat(nearestPlant);
                nn.reward(1.0f); // Reward the neural network for eating the plant
            } else {
                // Calculate the direction towards the plant
                float angleToPlant = (float) Math.atan2(dy, dx);

                // Move towards the plant
                move((float) Math.cos(angleToPlant), (float) Math.sin(angleToPlant));
            }
        } else {
            // No plant found, penalize the neural network
            nn.reward(-1.0f);
            setIsAlive(false); // The herbivore dies if there are no plants
        }
    }

    public void updateState(float[] worldState) {
        float[] decision = nn.Brain(worldState);
        // Update the creature's state based on the decision
        // For example, if the decision is a direction to move in:
        float moveX = decision[0];
        float moveY = decision[1];
        move(moveX, moveY);
    }

    private void setIsAlive(boolean b) {
        isAlive = b;
    }

    public void move(float FB, float LR) {
        // Clamp the values of LR and FB
        LR = Math.max(-1, Math.min(LR, 1));
        FB = Math.max(0, Math.min(FB, 1));

        if (isAlive()) {
            // Move forward/backward
            float moveX = (float) Math.cos(Math.toRadians(getRotation())) * MOVE_SPEED * FB;
            float moveY = (float) Math.sin(Math.toRadians(getRotation())) * MOVE_SPEED * FB;

            // Move left/right
            float rotateAmount = ROTATE_SPEED * LR;
            setRotation(getRotation() + rotateAmount);

            // Update the position based on both forward/backward and left/right movement
            float newX = getX() + moveX;
            float newY = getY() + moveY;

            // Check if the new position is within the bounds
            if (newX < MIN_X) {
                newX = MIN_X;
            } else if (newX > MAX_X) {
                newX = MAX_X;
            }

            if (newY < MIN_Y) {
                newY = MIN_Y;
            } else if (newY > MAX_Y) {
                newY = MAX_Y;
            }

            setPosition(newX, newY); // Update the position of the herbivore
        }
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
        EntityManager entityManager = EntityManager.getInstance();
        List<Carnivore> carnivores = entityManager.getEntities().stream()
                .filter(entity -> entity instanceof Carnivore)
                .map(entity -> (Carnivore) entity)
                .collect(Collectors.toList());

        float minDistance = Float.MAX_VALUE;
        for (Carnivore carnivore : carnivores) {
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
        EntityManager entityManager = EntityManager.getInstance();
        List<Carnivore> carnivores = entityManager.getEntities().stream()
                .filter(entity -> entity instanceof Carnivore)
                .map(entity -> (Carnivore) entity)
                .collect(Collectors.toList());

        float minDistance = Float.MAX_VALUE;
        float minAngle = 0.0f;
        for (Carnivore carnivore : carnivores) {
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

    private Plant findNearestPlant() {
        List<Entity> entities = EntityManager.getInstance().getEntities();
        Plant nearestPlant = null;
        float herbivoreX = getX();
        float herbivoreY = getY();

        // Calculate the maximum distance based on the herbivore's visionGene
        float maxDistance = size * getVisionGene();

        for (Entity entity : entities) {
            if (entity instanceof Plant && entity.isAlive()) {
                float plantX = entity.getX();
                float plantY = entity.getY();
                float dx = plantX - herbivoreX;
                float dy = plantY - herbivoreY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance <= maxDistance) {
                    if (nearestPlant == null || distance < maxDistance) {
                        nearestPlant = (Plant) entity;
                        maxDistance = distance;
                    }
                }
            }
        }
        return nearestPlant;
    }

    private float getVisionGene() {
        return (float) getChromosome().getVisionGene();
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

    public void eat(Plant plant) {
        // Check if the herbivore is close enough to the plant to eat it
        if (Math.sqrt(Math.pow(this.x - plant.getX(), 2) + Math.pow(this.y - plant.getY(), 2)) < this.size) {
            // Increase the herbivore's size or health
            this.size += plant.getNutritionValue();

            // Remove the plant from the simulation
            plant.remove();
        }
    }
}
