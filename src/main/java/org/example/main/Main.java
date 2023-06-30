package org.example.main;

import org.example.opengl.GLUtils;
import org.example.window.WindowManager;
import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private WindowManager windowManager;
    private GLUtils glUtils;
    private String windowTitle;

    private void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        init();
        loop();
        cleanup();
    }

    private void init(){
        windowTitle = "Hello World";
        windowManager = new WindowManager();
        windowManager.initWindow(1920, 1080, windowTitle);
        glUtils = new GLUtils(windowManager);
        glUtils.initOpenGL();

    }

    private void loop(){
        WindowManager manager = windowManager;
        double lastTime = glfwGetTime();
        int frames = 0;

        while (!windowManager.shouldClose()){
            double currentTime = glfwGetTime();
            frames++;

            if (currentTime - lastTime >= 1.0) {
                // Print out the frames per second count
                glfwSetWindowTitle(windowManager.getWindow(), windowTitle + " - FPS: " + frames);

                // Reset the counter and timer
                frames = 0;
                lastTime += 1.0;
            }

            glUtils.prepareFrame();

            // Apply the camera transformations


            glfwPollEvents();
            glUtils.finishFrame();
        }



    }

    private void cleanup() {

        windowManager.cleanup();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}