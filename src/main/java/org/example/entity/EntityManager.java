package org.example.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class EntityManager {
    private final List<Entity> entities = new ArrayList<>();
    private static EntityManager instance;

    public static EntityManager getInstance() {
        if (instance == null) {
            instance = new EntityManager();
        }
        return instance;
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
}
