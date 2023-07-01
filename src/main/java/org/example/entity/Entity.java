package org.example.entity;

import imgui.type.ImBoolean;
import org.example.NN.Chromosome;

public class Entity {
    private Chromosome chromosome;
    private int ID;
    private boolean infoWindowOpen = false;
    private float x, y, rotation;
    private ImBoolean infoWindowOpenRef = new ImBoolean(false);
    protected boolean isAlive = true;

    public Entity(Chromosome chromosome, int ID, float x, float y) {
        this.chromosome = chromosome;
        this.ID = ID;
        this.x = x;
        this.y = y;
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation % 360;

        if (this.rotation < 0) {
            this.rotation += 360;
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public float getRotation(){
        return rotation;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
