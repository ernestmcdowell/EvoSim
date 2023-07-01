package org.example.entity;

import org.example.NN.Chromosome;

public class Entity {
    private Chromosome chromosome;

    public Entity(Chromosome chromosome) {
        this.chromosome = chromosome;
    }

    public Chromosome getChromosome() {
        return chromosome;
    }

    public void setChromosome(Chromosome chromosome) {
        this.chromosome = chromosome;
    }

}
