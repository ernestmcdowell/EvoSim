package org.example.window;

import imgui.ImGui;

public class UIComponents {

    public static void createButton(String label, Runnable action) {
        if (ImGui.button(label)) {
            action.run();
        }
    }
}
