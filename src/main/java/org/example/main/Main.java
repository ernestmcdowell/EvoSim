package org.example.main;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.flag.ImGuiDockNodeFlags;
import imgui.type.ImBoolean;
import org.example.UI.UI;
import org.example.camera.Camera;
import org.example.entity.EntityManager;
import org.example.opengl.GLUtils;
import org.example.rendering.Renderer;
import org.example.window.UIComponents;
import org.example.window.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Main {
    private WindowManager windowManager;
    private GLUtils glUtils;
    private String windowTitle;
    private EntityManager entityManager = EntityManager.getInstance();
    private Renderer renderer;
    private UI ui;
    private ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private String glslVersion = null;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private float viewPositionX = 0;
    private float viewPositionY = 0;
    private Camera camera;

    private void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        ImGui.createContext();
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        camera = new Camera();
        windowManager = new WindowManager(camera);
        windowManager.initWindow(1920, 1080, "EvoSim"); // Create GLFW window before initializing ImGui
        imGuiGlfw.init(windowManager.getWindow(), true);
        imGuiGl3.init();
        ImGuiIO io = ImGui.getIO();
        io.setConfigFlags(io.getConfigFlags() | ImGuiConfigFlags.DockingEnable);
        renderer = new Renderer(entityManager, camera, windowManager);
        renderer.init();

        // Spawn entities here
        entityManager.spawnCarnivores(10); // spawn 10 carnivores
        entityManager.spawnHerbivores(20);
    }


    private void loop() {
        double lastTime = glfwGetTime();
        float dt = (float) glfwGetTime();
        int frames = 0;

        glfwMakeContextCurrent(windowManager.getWindow()); // Set the GLFW window context as current

        while (!windowManager.shouldClose()) {
            double currentTime = glfwGetTime();
            frames++;
            System.out.println("looping");
            if (currentTime - lastTime >= 1.0) {
                // Print out the frames per second count
                glfwSetWindowTitle(windowManager.getWindow(), "EvoSim - FPS: " + frames);

                // Reset the counter and timer
                frames = 0;
                lastTime += 1.0;
            }
            System.out.println("Number of entities: " + entityManager.getEntities().size());
            glClear(GL_COLOR_BUFFER_BIT);
            camera.applyTransformations();
            renderer.render();
            imGuiGlfw.newFrame();
            ImGui.newFrame();
            setupDockspace();


            // ALL IMGUI STUFF GO HERE //

            // Create the main menu bar
            if (ImGui.beginMainMenuBar()) {
                if (ImGui.beginMenu("View")) {
                    ImGui.menuItem("My Dockable Window", null, true);
                    // Add more menu items if needed

                    ImGui.endMenu();
                }
                ImGui.endMainMenuBar();
            }

            if (ImGui.begin("Test Window")) {
                UIComponents.createButton("Test Button", () -> System.out.println("Test Button was pressed"));
                ImGui.end();
            }
            if (ImGui.begin("Viewport")) {
                // Display the texture in the ImGui window
                ImGui.image(renderer.getTextureId(), 3440, 1200, 0, 1, 1, 0);
                ImGui.end();
            }



            /////////////////////////////////
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            // Start ImGui frame
            glfwMakeContextCurrent(windowManager.getWindow()); // Set the GLFW window context as current

            // Update and render ImGui viewports
            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                GLFW.glfwMakeContextCurrent(GLFW.glfwGetCurrentContext());
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(windowManager.getWindow());
            }

            // Get the framebuffer size
            try (MemoryStack stack = stackPush()) {
                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);
                glfwGetFramebufferSize(windowManager.getWindow(), widthBuffer, heightBuffer);
                int width = widthBuffer.get(0);
                int height = heightBuffer.get(0);

                // Set the viewport
                glViewport(0, 0, width, height);

                // Adjust the camera's projection
//                camera.setPosition(0.0f, 0.0f); // Reset the position
                float aspectRatio = (float) width / height;
                float cameraHeight = 10.0f; // Adjust this value as needed
                float cameraWidth = cameraHeight * aspectRatio;
                camera.setProjection(-cameraWidth / 2.0f, cameraWidth / 2.0f, -cameraHeight / 2.0f, cameraHeight / 2.0f, camera.getViewportWidth(), camera.getViewportHeight());
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

    private void setupDockspace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(windowManager.getWidth(), windowManager.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));
        ImGui.end();
    }
}
