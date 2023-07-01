package org.example.rendering;
import imgui.ImGui;
import imgui.flag.ImGuiHoveredFlags;
import org.example.camera.Camera;
import org.example.entity.Carnivore;
import org.example.entity.Entity;
import org.example.entity.EntityManager;
import org.example.entity.Herbivore;
import org.example.window.WindowManager;
import org.lwjgl.opengl.GL;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    private EntityManager entityManager;
    private int textureId;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private float viewPositionX = 0;
    private float viewPositionY = 0;
    private Camera camera;
    private WindowManager windowManager;

    public Renderer(EntityManager entityManager, Camera camera, WindowManager windowManager) {
        this.entityManager = entityManager;
        this.camera = camera;
        this.windowManager = windowManager; // Assign the windowManager parameter to the field
    }



    public void init() {
        // Initialize OpenGL
        GL.createCapabilities();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, windowManager.getViewportWidth(), 0, windowManager.getViewportHeight(), -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        camera.setProjection(0, windowManager.getWidth(), 0, windowManager.getHeight(),
                windowManager.getViewportWidth(), windowManager.getViewportHeight());

        textureId = createTextureForViewport();
    }

    public void render(float viewPositionX, float viewPositionY) {
        this.viewPositionX = viewPositionX;
        this.viewPositionY = viewPositionY;
        render();
    }

    public void render() {
        // Apply the camera transformations
        camera.applyTransformations();
        // Bind the texture and set up a framebuffer
        glBindTexture(GL_TEXTURE_2D, textureId);
        int framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);

        // Clear the color buffer
        glClearColor(0.1f, 0.09f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        // Render entities
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Carnivore) {
                renderCarnivore((Carnivore) entity, camera.getX(), camera.getY());
            } else if (entity instanceof Herbivore) {
                renderHerbivore((Herbivore) entity, camera.getX(), camera.getY());
            }
        }

        int viewportWidth = windowManager.getViewportWidth();
        int viewportHeight = windowManager.getViewportHeight();
        if (viewportWidth != camera.getViewportWidth() || viewportHeight != camera.getViewportHeight()) {
            camera.setProjection(0, windowManager.getWidth(), 0, windowManager.getHeight(),
                    viewportWidth, viewportHeight);
        }

        // Unbind the framebuffer and texture
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void renderHerbivore(Herbivore herbivore, float cameraX, float cameraY) {
        float x = herbivore.getX() - cameraX;
        float y = herbivore.getY() - cameraY;
        float size = herbivore.getSize();

        glColor3f(0.0f, 1.0f, 0.0f);

        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + size, y);
        glVertex2f(x + size, y + size);
        glVertex2f(x, y + size);
        glEnd();
    }

    private void renderCarnivore(Carnivore carnivore, float cameraX, float cameraY) {
        int DEFAULT_CIRCLE_SIDES = 30;
        float x = carnivore.getX()- cameraX;
        float y = carnivore.getY()- cameraY;
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

    private int createTextureForViewport() {
        // Create a texture and bind it
        GL.createCapabilities();
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Set the texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, windowManager.getViewportWidth(), windowManager.getViewportHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);

        // Unbind the texture
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureId;
    }

    public int getTextureId() {
        return textureId;
    }

}
