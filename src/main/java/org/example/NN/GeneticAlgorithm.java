package org.example.NN;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private static final double MUTATION_RATE = 0.1;
    private static final int TOURNAMENT_SIZE = 3;
    private static final int ELITE_COUNT = 2;

    private Random random;

    public GeneticAlgorithm() {
        random = new Random();
    }



    public List<Chromosome> evolvePopulation(List<Chromosome> population) {
        List<Chromosome> newPopulation = new ArrayList<>();

        // Elitism: Preserve the best individuals from the previous population
        for (int i = 0; i < ELITE_COUNT; i++) {
            newPopulation.add(population.get(i));
        }

        // Crossover and Mutation: Generate offspring for the rest of the population
        for (int i = ELITE_COUNT; i < population.size(); i++) {
            Chromosome parent1 = tournamentSelection(population);
            Chromosome parent2 = tournamentSelection(population);

            Chromosome offspring = crossover(parent1, parent2);
            mutate(offspring);

            newPopulation.add(offspring);
        }

        return newPopulation;
    }

    private Chromosome tournamentSelection(List<Chromosome> population) {
        List<Chromosome> tournament = new ArrayList<>();
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int randomIndex = random.nextInt(population.size());
            tournament.add(population.get(randomIndex));
        }

        Chromosome fittest = tournament.get(0);
        for (int i = 1; i < tournament.size(); i++) {
            Chromosome current = tournament.get(i);
            if (current.calculateFitness() > fittest.calculateFitness()) {
                fittest = current;
            }
        }

        return fittest;
    }

    private Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        double childHungerGene = random.nextDouble() < 0.5 ? parent1.getHungerGene() : parent2.getHungerGene();
        double childHealthGene = random.nextDouble() < 0.5 ? parent1.getHealthGene() : parent2.getHealthGene();
        double childReproductionChanceGene= random.nextDouble() < 0.5 ? parent1.getReproductionChanceGene() : parent2.getReproductionChanceGene();
        double childSpeedGene = random.nextDouble() < 0.5 ? parent1.getSpeedGene() : parent2.getSpeedGene();
        double childVisionGene = random.nextDouble() < 0.5 ? parent1.getVisionGene() : parent2.getVisionGene();
        double childSizeGene= random.nextDouble() < 0.5 ? parent1.getSizeGene() : parent2.getSizeGene();
        double childStaminaGene= random.nextDouble() < 0.5 ? parent1.getStaminaGene() : parent2.getStaminaGene();
        double childAggressionGene= random.nextDouble() < 0.5 ? parent1.getAggressionGene() : parent2.getAggressionGene();
        double childPassiveGene= random.nextDouble() < 0.5 ? parent1.getPassiveGene() : parent2.getPassiveGene();
        // Perform crossover for other genes as well

        return new Chromosome(childHungerGene, childHealthGene, childReproductionChanceGene, childSpeedGene, childVisionGene, childSizeGene, childStaminaGene, childAggressionGene, childPassiveGene);
    }

    private void mutate(Chromosome chromosome) {
        if (random.nextDouble() < MUTATION_RATE) {
            chromosome.setHungerGene(random.nextDouble());
        }
        if (random.nextDouble() < MUTATION_RATE) {
            chromosome.setHealthGene(random.nextDouble());
        }
        // Perform mutation for other genes as well
    }
}
