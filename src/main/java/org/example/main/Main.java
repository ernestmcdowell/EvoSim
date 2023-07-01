package org.example.main;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.flag.ImGuiDockNodeFlags;
import imgui.type.ImBoolean;
import org.example.NN.Chromosome;
import org.example.NN.GeneticAlgorithm;
import org.example.UI.UI;
import org.example.camera.Camera;
import org.example.entity.Carnivore;
import org.example.entity.Entity;
import org.example.entity.EntityManager;
import org.example.entity.Herbivore;
import org.example.opengl.GLUtils;
import org.example.rendering.Renderer;
import org.example.window.UIComponents;
import org.example.window.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Main {
    private WindowManager windowManager;
    private GLUtils glUtils;
    private String windowTitle;
    private boolean isSimulationRunning = false; // Add this line
    private boolean isSimulationTraining = false; // Add this line
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
    private float mouseSensitivity = 0.001f;


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
            if (currentTime - lastTime >= 1.0) {
                // Print out the frames per second count
                glfwSetWindowTitle(windowManager.getWindow(), "EvoSim - FPS: " + frames);

                // Reset the counter and timer
                frames = 0;
                lastTime += 1.0;
            }
            glClear(GL_COLOR_BUFFER_BIT);
            camera.applyTransformations();
            renderer.render();
            imGuiGlfw.newFrame();
            ImGui.newFrame();


            // ALL IMGUI STUFF GO HERE //

            setupDockspace();

            if (ImGui.begin("Test Window")) {
                UIComponents.createButton("Train Simulation", () -> isSimulationTraining = true);
                UIComponents.createButton("Stop Training", () -> isSimulationTraining = false);
                UIComponents.createButton("Run Simulation", () -> isSimulationRunning = false);
                UIComponents.createButton("Stop Running", () -> isSimulationRunning = false);
                ImGui.end();
            }
            if (ImGui.begin("Viewport")) {
                // Display the texture in the ImGui window
                ImGui.image(renderer.getTextureId(), 1920, 1080, 0, 1, 1, 0);

                // Only move the camera if the ImGui viewport window is hovered
                if (ImGui.isWindowHovered()) {
                    // Get the current mouse position in screen space
                    ImVec2 mousePos = new ImVec2();
                    ImGui.getMousePos(mousePos);

                    // Get the top-left corner position of the ImGui window
                    ImVec2 windowPos = new ImVec2();
                    ImGui.getWindowPos(windowPos);
//                    double mouseX = ImGui.getMousePosX();
//                    double mouseY = ImGui.getMousePosY();

                    // Calculate the mouse position relative to the ImGui window
                     double mouseX = mousePos.x - windowPos.x;
                     double mouseY = mousePos.y - windowPos.y;

                    // Calculate the mouse movement since the last frame
                    double dx = (mouseX - lastMouseX) * mouseSensitivity;
                    double dy = (mouseY - lastMouseY) * mouseSensitivity * -1;

                    // Update the last mouse position
                    lastMouseX = mouseX;
                    lastMouseY = mouseY;

                    // Move the camera
                    if (ImGui.isMouseDown(ImGuiMouseButton.Left)) {
                        camera.move(dx, dy);
                    }
                }

                ImGui.end();
            }

            entityWindow();

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
            if(isSimulationTraining){
                // Every 100 frames, apply the genetic algorithm
                if (frames % 1000000 == 0) {
                    List<Chromosome> population = entityManager.getEntities().stream()
                            .map(Entity::getChromosome)
                            .collect(Collectors.toList());
                    GeneticAlgorithm ga = new GeneticAlgorithm();
                    List<Chromosome> newPopulation = ga.evolvePopulation(population);
                    // Replace the old population with the new one
                    // You need to implement this method
                    entityManager.replacePopulation(newPopulation);
                }
            }
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

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

        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));
        ImGui.end();
    }

    public void entityWindow() {
        ImGui.begin("Entity Information"); // Start the ImGui window

        // Retrieve the entities and their genes
        List<Entity> entities = entityManager.getEntities();

        // Display the entity information
        for (Entity entity : entities) {
            // Display the entity's name as a clickable text label
            String entityName = "Entity ID: " + entity.getID();
            if (ImGui.selectable(entityName)) {
                // If the entity's name is clicked, toggle the visibility of its information window
                entity.toggleInfoWindow();
            }

            // Check if the entity's information window is open
            if (entity.isInfoWindowOpen()) {
                String windowTitle = "Entity " + entity.getID() + " Information";
                ImGui.begin(windowTitle, entity.getInfoWindowOpenRef());

                // Add a button to move the camera to the entity's location
                if (ImGui.button("Move Camera Here")) {
                    if (entity instanceof Carnivore) {
                        Carnivore carnivore = (Carnivore) entity;
                        camera.setCameraPosition(carnivore.getX(), carnivore.getY());
                    } else if (entity instanceof Herbivore) {
                        Herbivore herbivore = (Herbivore) entity;
                        camera.setCameraPosition(herbivore.getX(), herbivore.getY());
                    }
                }

                ImGui.text("Genes:");
                ImGui.indent();

                // Access the entity's chromosome and display its genes
                Chromosome chromosome = entity.getChromosome();
                ImGui.text("Hunger Gene: " + chromosome.getHungerGene());
                ImGui.text("Health Gene: " + chromosome.getHealthGene());
                ImGui.text("Reproduction Chance Gene: " + chromosome.getReproductionChanceGene());

                ImGui.unindent();
                ImGui.end();
            }
        }

        ImGui.end(); // End the ImGui window
    }
}
