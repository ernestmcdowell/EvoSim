package org.example.opengl;

import org.example.window.WindowManager;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
public class GLUtils {
    private WindowManager windowManager;

    public GLUtils(WindowManager windowManager){
        this.windowManager = windowManager;
    }


    public void initOpenGL(){
        glfwMakeContextCurrent(windowManager.getWindow());
        GL.createCapabilities();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //set window background color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }


    public void prepareFrame(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void finishFrame(){
        glfwSwapBuffers(windowManager.getWindow());
    }
}
