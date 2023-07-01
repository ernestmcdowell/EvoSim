package org.example.camera;

import static org.lwjgl.opengl.GL11.*;

public class Camera {
    private float x, y;
    private float zoom = 2f;
    private float left, right, bottom, top;
    private int viewportWidth;
    private int viewportHeight;

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void setProjection(float left, float right, float bottom, float top, int viewportWidth, int viewportHeight) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    public void applyTransformations() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(left, right, bottom, top, -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(-x, -y, 0);
        glScalef(zoom, zoom, 1.0f);
    }

    public float getZoom() {
        return zoom;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }
}
