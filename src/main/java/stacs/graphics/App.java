package stacs.graphics;

import stacs.graphics.data.Face;
import stacs.graphics.data.FaceLoader;
import stacs.graphics.render.MeshLoader;
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
        var faceLoader = new FaceLoader("mesh.csv", "sh_000.csv");
        var faces = faceLoader.loadFromResources(new String[]{"sh_001.csv", "sh_002.csv", "sh_003.csv"});
        var render = new Render();

        while (!window.shouldClose()) {
            render.cleanup();
            render.render(faces[2].getMesh());
            window.update();
        }
    }

    public static void main(String[] args) throws IOException {
        new App().run();
    }
}
