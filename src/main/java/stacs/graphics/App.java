package stacs.graphics;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Hello world!
 */
public class App {

    private long window;

    public void run() {
        init();
        loop();

        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(640, 480, "Interactive 3D Modelling", NULL, NULL);
        if(window == NULL) {
            throw new IllegalStateException("Unable to create GLFW Window");
        }

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {});

        try(MemoryStack stack = stackPush()){
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            GLFW.glfwSetWindowPos(window,(vidmode.width() - pWidth.get(0)) / 2,(vidmode.height() - pHeight.get(0)) / 2);

            GLFW.glfwMakeContextCurrent(window);
            GLFW.glfwSwapInterval(1);
            GLFW.glfwShowWindow(window);
        }
    }

    public void loop() {
        GL.createCapabilities();

        while(!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

            GLFW.glfwSwapBuffers(window);

            GLFW.glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new App().run();
    }
}
