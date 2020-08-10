package stacs.graphics.render;


import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private final String title;
    private int width;
    private int height;
    private long window;
    private boolean resized;
    private boolean vsync;

    public Window(String title, int width, int height, boolean vsync) {
        this.title = title;
        this.resized = false;
        this.width = width;
        this.height = height;
        this.vsync = vsync;
    }

    public void init() {
        // Print errors to console
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // create GUI window
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new IllegalStateException("Unable to create GLFW Window");
        }

        // Setup resize callback
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        // setup key callback
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        // center the window on the screen
        var vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(window);

        // ensure that new buffer is only sent once a render is complete
        if (vsync) {
            GLFW.glfwSwapInterval(1);
        }

        // make window visible
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        // set clear colour
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void update() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void terminate() {
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindowHandle() {
        return window;
    }

    public void setClearColour(float r, float g, float b, float alpha) {
        GL11.glClearColor(r, g, b, alpha);
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    public boolean isVsync() {
        return vsync;
    }
}
