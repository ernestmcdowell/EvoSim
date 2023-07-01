package org.example.main;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.example.UI.UI;
import org.example.entity.EntityManager;
import org.example.opengl.GLUtils;
import org.example.rendering.Renderer;
import org.example.window.WindowManager;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Main {
    private WindowManager windowManager;
    private GLUtils glUtils;
    private String windowTitle;
    private EntityManager entityManager = EntityManager.getInstance();
    private Renderer renderer;
    private UI ui;
    private ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        String windowTitle = "Hello World";
        windowManager = new WindowManager();
        windowManager.initWindow(1920, 1080, "EvoSim"); // Create GLFW window before initializing ImGui
        windowManager.init(); // Initialize ImGui after the GLFW window is created
        renderer = new Renderer(entityManager);
        renderer.init();

        // Initialize ImGui
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        imGuiGlfw.init(windowManager.getWindow(), true);
        imGuiGl3.init("#version 330 core");

        // Make the OpenGL context current again
        glfwMakeContextCurrent(windowManager.getWindow());

        // Spawn entities here
        entityManager.spawnCarnivores(10); // spawn 10 carnivores
        entityManager.spawnHerbivores(20);
    }

    private void loop() {
        double lastTime = glfwGetTime();
        int frames = 0;

        while (!windowManager.shouldClose()) {
            double currentTime = glfwGetTime();
            frames++;

            if (currentTime - lastTime >= 1.0) {
                // Print out the frames per second count
                glfwSetWindowTitle(windowManager.getWindow(), "EvoSim - FPS: " + frames);

                // Reset the counter and timer
                frames = 0;
                lastTime += 1.0;
            }

            System.out.println("Number of entities: " + entityManager.getEntities().size());

            // Render the scene
            glClear(GL_COLOR_BUFFER_BIT);
            renderer.render();

            // Start ImGui frame
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            // ImGui rendering code
            UI.imgui();

            // Render ImGui
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            // Update and render ImGui viewports
            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                GLFW.glfwMakeContextCurrent(GLFW.glfwGetCurrentContext());
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(windowManager.getWindow());
            }

            // Swap buffers and poll events
            windowManager.swapBuffers();
            glfwPollEvents();
        }

        ImGui.destroyContext();

        // Cleanup GLFW
        glfwTerminate();
    }

    private void cleanup() {
        windowManager.destroy();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
