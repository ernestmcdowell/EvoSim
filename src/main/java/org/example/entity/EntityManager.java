package org.example.entity;

import org.example.NN.Chromosome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class EntityManager {
    private final List<Entity> entities = new ArrayList<>();
    private static EntityManager instance;
    private int nextID = 0;
    private List<Herbivore> herbivores;

    private EntityManager(){
        herbivores = new ArrayList<>();
    }

    public static EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
    }

    public List<Herbivore> getHerbivores() {
        return herbivores;
    }

    // Method to add herbivores to the list
    public void addHerbivore(Herbivore herbivore) {
        herbivores.add(herbivore);
    }

    public interface EntitySupplier<T extends Entity> {
        List<T> createEntities(int count);
    }

    public <T extends Entity> void spawnEntities(EntitySupplier<T> entitySupplier, int count) {
        List<T> newEntities = entitySupplier.createEntities(count);
        entities.addAll(newEntities);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void spawnCarnivores(int count) {
        spawnEntities(new EntityFactory()::createCarnivores, count);
    }

    public void spawnHerbivores(int count) {
        spawnEntities(new EntityFactory()::createHerbivores, count);
    }
    public void spawnPlants(int count) {
        spawnEntities(new EntityFactory()::createPlants, count);
    }

    public void replacePopulation(List<Chromosome> newPopulation) {
        // Create new entities with the new chromosomes
        List<Entity> newEntities = new ArrayList<>();
        int entityCount = entities.size();
        EntityFactory entityFactory = new EntityFactory(); // Create an instance of the EntityFactory
        for (int i = 0; i < entityCount; i++) {
            Chromosome chromosome = newPopulation.get(i);
            Entity newEntity = createRandomEntityWithChromosome(chromosome, entityFactory);
            spawnPlants(60);
            newEntities.add(newEntity);
        }

        // Replace the old population with the new one
        entities.clear();
        entities.addAll(newEntities);
    }

    private Entity createRandomEntityWithChromosome(Chromosome chromosome, EntityFactory entityFactory) {
        // Create a random position for the new entity
        float posX = getRandomPosition();
        float posY = getRandomPosition();

        // Determine the type of entity (Carnivore or Herbivore) based on the chromosome
        if (chromosome.getHungerGene() > 0.5) {
            return new Carnivore(chromosome, posX, posY, .1f);
        } else {
            return new Herbivore(chromosome, posX, posY, .1f);
        }
    }

    private float getRandomPosition() {
        float minPos = -10.0f;
        float maxPos = 10.0f;
        return minPos + (float) Math.random() * (maxPos - minPos);
    }

    private int generateNewID() {
        return nextID++;
    }

}
