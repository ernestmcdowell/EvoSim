package org.example.rendering;

import org.example.entity.Carnivore;
import org.example.entity.Entity;
import org.example.entity.EntityManager;
import org.example.entity.Herbivore;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.awt.Graphics;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private EntityManager entityManager;

    public Renderer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void init() {
        // Initialize OpenGL
        GL.createCapabilities();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 1920, 0, 1080, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public void render() {
        // Clear the color buffer
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        // Render entities
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Carnivore) {
                renderCarnivore((Carnivore) entity);
            } else if (entity instanceof Herbivore) {
                renderHerbivore((Herbivore) entity);
            }
        }
    }

    private void renderHerbivore(Herbivore herbivore) {
        float x = herbivore.getX();
        float y = herbivore.getY();
        float size = herbivore.getSize();

        glColor3f(0.0f, 1.0f, 0.0f);

        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + size, y);
        glVertex2f(x + size, y + size);
        glVertex2f(x, y + size);
        glEnd();
    }

    private void renderCarnivore(Carnivore carnivore) {
        int DEFAULT_CIRCLE_SIDES = 30;
        float x = carnivore.getX();
        float y = carnivore.getY();
        float size = carnivore.getSize();

        glColor3f(1.0f, 0.0f, 0.0f);

        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(x, y);
        for (int i = 0; i <= DEFAULT_CIRCLE_SIDES; i++) {
            double angle = Math.PI * 2 * i / DEFAULT_CIRCLE_SIDES;
            glVertex2f((float) (x + Math.sin(angle) * size), (float) (y + Math.cos(angle) * size));
        }
        glEnd();
    }

}
