package org.example.entity;

import org.example.NN.Chromosome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EntityFactory {
    private final Random rand = new Random();

    public List<Carnivore> createCarnivores(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Carnivore(createRandomChromosome(), rand.nextFloat(), rand.nextFloat(), .1f))
                .collect(Collectors.toList());
    }

    public List<Herbivore> createHerbivores(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Herbivore(createRandomChromosome(), rand.nextFloat(), rand.nextFloat(), .1f))
                .collect(Collectors.toList());
    }

    private Chromosome createRandomChromosome() {
        // Generate random values for each gene within their desired range
        double hungerGene = rand.nextDouble();
        double healthGene = rand.nextDouble();
        double reproductionChanceGene = rand.nextDouble();
        double speedGene = rand.nextDouble();
        double visionGene = rand.nextDouble();
        double sizeGene = rand.nextDouble();
        double staminaGene = rand.nextDouble();
        double aggressionGene = rand.nextDouble();
        double passivenessGene = rand.nextDouble();

        // Create and return the chromosome with the random values
        return new Chromosome(hungerGene, healthGene, reproductionChanceGene, speedGene, visionGene, sizeGene, staminaGene, aggressionGene, passivenessGene);
    }
}
