package stacs.graphics;

import stacs.graphics.data.FaceLoader;
import stacs.graphics.render.Render;
import stacs.graphics.render.Window;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {

    private static Window window;

    public void run() throws IOException {
        window = new Window(800, 1000);
        loop();
        window.terminate();
    }

    public void loop() throws IOException {
        var faceLoader = new FaceLoader("mesh.csv", "sh_000.csv", "sh_ev.csv", "tx_ev.csv");
        var faces = faceLoader.loadFromResources(new int[]{1, 2, 3});
        var render = new Render();

        while (!window.shouldClose()) {
            render.cleanup();
            render.render(faces[0].getMesh());
            window.update();
        }
    }

    public static void main(String[] args) throws IOException {
        new App().run();
    }
}
