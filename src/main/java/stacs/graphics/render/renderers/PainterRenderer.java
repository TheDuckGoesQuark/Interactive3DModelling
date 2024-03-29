package stacs.graphics.render.renderers;

import org.joml.*;
import stacs.graphics.data.ResourceLoader;
import stacs.graphics.engine.DirectionalLight;
import stacs.graphics.logic.Configuration;
import stacs.graphics.render.Camera;
import stacs.graphics.render.Renderable;
import stacs.graphics.render.ShaderProgram;
import stacs.graphics.render.Window;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * INCREASE THE STACK SIEZ
 */
public class PainterRenderer extends Renderer {

    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    private static final String fragmentShaderResourceName = "shaders/fragment.shader";
    private static final String vertexShaderResourceName = "shaders/vertexSimple.shader";
    private static final String MATRIX_NAME = "transformMatrix";
    private static final int MAX_THREADS = 4;

    public PainterRenderer(Configuration configuration) {
        super(configuration);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        threadPoolExecutor.shutdown();
    }

    @Override
    public void init() throws Exception {
        // set up shaders
        var resourceLoader = ResourceLoader.getInstance();
        var fragmentShader = resourceLoader.readAllToString(fragmentShaderResourceName);
        var vertexShader = resourceLoader.readAllToString(vertexShaderResourceName);
        shaderProgram = new ShaderProgram();
        shaderProgram.createFragmentShader(fragmentShader);
        shaderProgram.createVertexShader(vertexShader);
        shaderProgram.link();

        shaderProgram.createUniform(MATRIX_NAME);
    }

    @Override
    public void render(Renderable sceneRoot, Window window, Camera camera, DirectionalLight directionalLight) {
        clear();

        // transform values
        var projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, zFar);
        var viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.bind();
        render(sceneRoot, null, projectionMatrix, viewMatrix);
        shaderProgram.unbind();
    }

    private void render(Renderable renderable, Matrix4f parentWorldMatrix, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        final Matrix4f worldMatrix;
        if (parentWorldMatrix == null) {
            // this node is root
            worldMatrix = new Matrix4f(transformation.getWorldMatrix(renderable));
        } else {
            // translate child relative to parent
            worldMatrix = new Matrix4f();
            parentWorldMatrix.mul(transformation.getWorldMatrix(renderable), worldMatrix);
        }

        // build matrix
        final var transform = new Matrix4f();
        projectionMatrix.mul(viewMatrix, transform);
        transform.mul(worldMatrix);

        // apply painters algorithm to mesh
        shaderProgram.setUniform(MATRIX_NAME, transform);
        renderWithPainter(renderable, transform);

        for (Renderable child : renderable.getChildren()) {
            // render children
            render(child, worldMatrix, projectionMatrix, viewMatrix);
        }
    }

    private void renderWithPainter(Renderable m, Matrix4f transformMatrix) {
        System.out.println(m.toString());
        float[] output = transformVertices(m, transformMatrix);
        int[] indices = m.getIndices();
        sortIndicesByPainter(indices, output);
        m.reloadIndices();
        m.getMesh().ifPresent(this::drawMesh);
    }

    private void sortIndicesByPainter(int[] indices, float[] coordinates) {
        // get max Z for each triangle
        float[] maxZ = Util.getMaxZs(indices, coordinates);

        // sort indices based on the values in maxZ
        Util.quicksortByZValue(maxZ, 0, maxZ.length - 1, indices);
    }


    private float[] transformVertices(Renderable m, Matrix4f transform) {
        // prepare pipeline
        var original = m.getVertices();
        var verticesCopy = Arrays.copyOf(original, original.length);

        // allocate chunks of the array
        var callables = Allocator.allocate(verticesCopy, transform, MAX_THREADS);

        // carry out transforms
        try {
            threadPoolExecutor.invokeAll(callables);
        } catch (InterruptedException e) {
            System.err.println("Failed to execute tasks");
            e.printStackTrace();
        }

        return verticesCopy;
    }
}
