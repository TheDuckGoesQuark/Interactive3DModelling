package stacs.graphics.logic;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import stacs.graphics.data.FaceLoader;
import stacs.graphics.engine.IApplicationLogic;
import stacs.graphics.engine.MouseInput;
import stacs.graphics.render.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Interactive3DModel implements IApplicationLogic {

    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private final SceneRoot sceneRoot;


    public Interactive3DModel() {
        this.renderer = new Renderer(
                "shaders/fragment.shader",
                "shaders/vertex.shader"
        );
        this.camera = new Camera();
        this.cameraInc = new Vector3f();
        this.sceneRoot = new SceneRoot();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        // load control faces
        var faceLoader = new FaceLoader(
                "mesh.csv",
                "sh_000.csv",
                "sh_ev.csv",
                "tx_000.csv",
                "tx_ev.csv"
        );
        var controlFaces = faceLoader.loadFromResources(new int[]{1, 2, 3});

        // prepare interpolation triangle
        var selectionArea = new SelectionArea();
        selectionArea.addControlFaces(controlFaces);
        sceneRoot.addChild(selectionArea);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // update camera position
        camera.movePosition(
                cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP
        );

        // update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            var rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(sceneRoot, window, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}
