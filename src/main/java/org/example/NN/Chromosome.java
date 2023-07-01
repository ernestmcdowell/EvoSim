package org.example.NN;

public class Chromosome {
    private double hungerGene;
    private double minHunger, maxHunger;
    private double healthGene;
    private double minHealth, maxHealth;
    private double reproductionChanceGene;
    private double minreproductionChance, maxReproductionChance;
    private double speedGene;
    private double visionGene;
    private double sizeGene;
    private double staminaGene;
    private double aggressionGene;
    private double passivenessGene;
    private double weightForHunger = 0.5;
    private double weightForHealth = 1.0;
    private double weightForReproductionChance = 0.2;

    public Chromosome(double hungerGene, double healthGene, double reproductionChanceGene, double speedGene, double visionGene, double sizeGene, double staminaGene, double aggressionGene, double passivenessGene){
        this.hungerGene = hungerGene;
        this.healthGene = healthGene;
        this.reproductionChanceGene = reproductionChanceGene;
        this.speedGene = speedGene;
        this.visionGene = visionGene;
        this.sizeGene = sizeGene;
        this.staminaGene = staminaGene;
        this.aggressionGene = aggressionGene;
        this.passivenessGene = passivenessGene;
    }

    public double getHungerGene() {
        return hungerGene;
    }

    public void setHungerGene(double hungerGene) {
        this.hungerGene = hungerGene;
    }

    public double getHealthGene() {
        return healthGene;
    }

    public void setHealthGene(double healthGene) {
        this.healthGene = healthGene;
    }

    public double getReproductionChanceGene() {
        return reproductionChanceGene;
    }

    public void setReproductionChanceGene(double reproductionChanceGene) {
        this.reproductionChanceGene = reproductionChanceGene;
    }

    public double getSpeedGene() {
        return speedGene;
    }

    public void setSpeedGene(double speedGene) {
        this.speedGene = speedGene;
    }

    public double getVisionGene() {
        return visionGene;
    }

    public void setVisionGene(double visionGene) {
        this.visionGene = visionGene;
    }

    public double getSizeGene() {
        return sizeGene;
    }

    public void setSizeGene(double sizeGene) {
        this.sizeGene = sizeGene;
    }

    public double getStaminaGene() {
        return staminaGene;
    }

    public double getAggressionGene(){ return aggressionGene; }
    public double getPassiveGene(){ return passivenessGene; }

    public void setStaminaGene(double staminaGene) {
        this.staminaGene = staminaGene;
    }

    public double calculateFitness() {
        Chromosome chromosome = this;
        double normalizedHunger = normalizeHunger(chromosome.getHungerGene());
        double normalizedHealth = normalizeHealth(chromosome.getHealthGene());
        double normalizedReproductionChance = normalizeReproductionChance(chromosome.getReproductionChanceGene());

        // Apply weights or importance factors to each normalized attribute
        double weightedHunger = normalizedHunger * weightForHunger;
        double weightedHealth = normalizedHealth * weightForHealth;
        double weightedReproductionChance = normalizedReproductionChance * weightForReproductionChance;

        // Combine the weighted attributes into an overall fitness score
        double fitnessScore = weightedHunger + weightedHealth + weightedReproductionChance;

        return fitnessScore;
    }

    private double normalizeHunger(double hungerGene) {
        // Assuming the hungerGene range is [minHunger, maxHunger]
        double normalizedHunger = (hungerGene - minHunger) / (maxHunger - minHunger);

        // Ensure the normalized hunger value is within [0, 1]
        normalizedHunger = Math.max(0.0, Math.min(normalizedHunger, 1.0));

        return normalizedHunger;
    }

    private double normalizeHealth(double healthGene) {
        double normalizedHealth = (healthGene - minHealth) / (maxHealth - minHealth);
        normalizedHealth = Math.max(0.0, Math.min(normalizedHealth, 1.0));

        return normalizedHealth;
    }

    private double normalizeReproductionChance(double reproductionChanceGene) {
        double normalizedReproductionChance = (reproductionChanceGene - minreproductionChance) / (maxReproductionChance - minreproductionChance);
        normalizedReproductionChance = Math.max(0.0, Math.min(normalizedReproductionChance, 1.0));

        return normalizedReproductionChance;
    }

    public float[][] getWeightsForNN() {
        // Assuming each gene corresponds to a weight in the neural network
        return new float[][] {
                { (float) hungerGene, (float) healthGene, (float) reproductionChanceGene },
                { (float) speedGene, (float) visionGene, (float) sizeGene },
                { (float) staminaGene, (float) aggressionGene, (float) passivenessGene }
        };
    }

    public float[] getBiasesForNN() {
        // Assuming each gene also contributes to a bias in the neural network
        return new float[] {
                (float) hungerGene,
                (float) healthGene,
                (float) reproductionChanceGene,
                (float) speedGene,
                (float) visionGene,
                (float) sizeGene,
                (float) staminaGene,
                (float) aggressionGene,
                (float) passivenessGene
        };
    }
}
