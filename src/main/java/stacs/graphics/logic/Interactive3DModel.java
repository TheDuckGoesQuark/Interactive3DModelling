package stacs.graphics.logic;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import stacs.graphics.data.Face;
import stacs.graphics.data.FaceLoader;
import stacs.graphics.engine.IApplicationLogic;
import stacs.graphics.engine.MouseInput;
import stacs.graphics.render.*;
import stacs.graphics.render.renderers.PainterRenderer;
import stacs.graphics.render.renderers.Renderer;
import stacs.graphics.render.renderers.ZBufferRenderer;

import java.util.Arrays;

public class Interactive3DModel implements IApplicationLogic {

    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final Vector3f cameraInc;
    private final Camera camera;
    private Renderer renderer;
    private OutputFace outputFace;
    private SceneRoot sceneRoot;
    private SelectionArea selectionArea;
    private Face[] controlFaces;

    public Interactive3DModel(Configuration configuration) {
        setRenderer(configuration);
        this.camera = new Camera();
        this.cameraInc = new Vector3f();
    }

    private void setRenderer(Configuration configuration) {
        if (configuration.getDepthTestMethod().equals(Configuration.DEPTH_TEST_ZBUFFER)) {
            this.renderer = new ZBufferRenderer();
        } else {
            this.renderer = new PainterRenderer();
        }
    }

    @Override
    public void init() throws Exception {
        sceneRoot = new SceneRoot();
        renderer.init();

        // load control faces
        var faceLoader = new FaceLoader(
                "mesh.csv",
                "sh_000.csv",
                "sh_ev.csv",
                "tx_000.csv",
                "tx_ev.csv"
        );
        controlFaces = faceLoader.loadFromResources(new int[]{1, 2, 3});

        // prepare output face
        var firstFace = controlFaces[0];
        var indices = firstFace.getIndices();
        outputFace = new OutputFace(firstFace.getVertices().length, Arrays.copyOf(indices, indices.length));
        outputFace.setPosition(0f, 0f, 1f);
        outputFace.setScale(1f);

        // prepare interpolation triangle
        selectionArea = new SelectionArea();
        selectionArea.setControlFaces(controlFaces);
        selectionArea.updateOutputFace(outputFace);

        // add renderables to scene
        sceneRoot.addChild(selectionArea);
        sceneRoot.addChild(outputFace);
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
    public void update(float interval, MouseInput mouseInput, Window window) {
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

        // check for selection triangle click and update weighting
        if (mouseInput.isLeftButtonPressed()) {
            // convert window coordinate to world coordinate
            var position = mouseInput.getCurrentPos();
            var triangleCoordinate = renderer.invertScreenCoordinates(window, camera, position);
            selectionArea.setCurrentWeighting(new Vector3f(triangleCoordinate.x, triangleCoordinate.y, 0.0f));
            selectionArea.updateOutputFace(outputFace);
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
