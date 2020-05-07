package stacs.graphics;

import stacs.graphics.render.MeshLoader;
import stacs.graphics.render.Render;
import stacs.graphics.render.Window;

/**
 * Hello world!
 */
public class App {

    private static Window window;

    public void run() {
        window = new Window(640, 480);
        loop();
        window.terminate();

    }

    public void loop() {
        float[] vertices = {-0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0f, 0.5f, 0f};
        int[] indices = {0, 1, 2};

        var mesh = MeshLoader.createMesh(vertices, indices);
        var render = new Render();

        while (!window.shouldClose()) {
            render.cleanup();
            render.render(mesh);
            window.update();
        }
    }

    public static void main(String[] args) {
        new App().run();
    }
}
