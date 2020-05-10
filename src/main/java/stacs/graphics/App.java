package stacs.graphics;

import stacs.graphics.data.FaceLoader;
import stacs.graphics.render.Render;
import stacs.graphics.render.Window;

import static org.lwjgl.opengl.GL20.glGetAttribLocation;

/**
 * Hello world!
 */
public class App {

    private static Window window;

    public void run() throws Exception {
        window = new Window(800, 1000);
        loop();
        window.terminate();
    }

    public void loop() throws Exception {
        var faceLoader = new FaceLoader(
                "mesh.csv",
                "sh_000.csv",
                "sh_ev.csv",
                "tx_000.csv",
                "tx_ev.csv"
        );
        var faces = faceLoader.loadFromResources(new int[]{1, 2, 3});
        var renderer = new Render(
                "shaders/fragment.shader",
                "shaders/vertex.shader"
        );

        renderer.init(window);

        while (!window.shouldClose()) {
            renderer.clear();
            renderer.render(faces[0], window);
            window.update();
        }

        renderer.cleanup();
    }

    public static void main(String[] args) throws Exception {
        new App().run();
    }
}
