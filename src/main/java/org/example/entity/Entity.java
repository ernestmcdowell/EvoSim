package org.example.entity;

import imgui.type.ImBoolean;
import org.example.NN.Chromosome;

public class Entity {
    private Chromosome chromosome;
    private int ID;
    private boolean infoWindowOpen = false;
    private ImBoolean infoWindowOpenRef = new ImBoolean(false);

    public Entity(Chromosome chromosome, int ID) {
        this.chromosome = chromosome;
        this.ID = ID;
    }

    public Chromosome getChromosome() {
        return chromosome;
    }

    public void setChromosome(Chromosome chromosome) {
        this.chromosome = chromosome;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isInfoWindowOpen() {
        return infoWindowOpen;
    }

    public ImBoolean getInfoWindowOpenRef() {
        return infoWindowOpenRef;
    }

    public void toggleInfoWindow() {
        infoWindowOpen = !infoWindowOpen;
        infoWindowOpenRef.set(infoWindowOpen);
    }
}
