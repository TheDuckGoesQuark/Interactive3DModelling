package stacs.graphics.render.renderers;

import org.joml.Matrix4f;
import stacs.graphics.render.Camera;
import stacs.graphics.render.Renderable;
import stacs.graphics.render.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PainterRenderer extends Renderer {

    private static final int MAX_THREADS = 4;
    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(MAX_THREADS);

    @Override
    public void render(Renderable sceneRoot, Window window, Camera camera) {
        clear();

        // transform values
        var projectionMatrix = transformation.getPerspectiveProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        var viewMatrix = transformation.getViewMatrix(camera);

        render(sceneRoot, null, projectionMatrix, viewMatrix);
    }

    private void render(Renderable renderable, Matrix4f parentWorldMatrix, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        final Matrix4f worldMatrix;
        if (parentWorldMatrix == null) {
            // this node is root
            worldMatrix = transformation.getWorldMatrix(renderable);
        } else {
            // translate child relative to parent
            worldMatrix = new Matrix4f(parentWorldMatrix)
                    .mul(transformation.getWorldMatrix(renderable));
        }

        // apply painters algorithm to mesh
        renderWithPainter(renderable, worldMatrix, projectionMatrix, viewMatrix);

        for (Renderable child : renderable.getChildren()) {
            // render children
            render(child, worldMatrix, projectionMatrix, viewMatrix);
        }
    }

    private void renderWithPainter(Renderable m, Matrix4f worldMatrix, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        float[] output = transformVertices(m, worldMatrix, projectionMatrix, viewMatrix);
        int[] indices = m.getIndices();
        sortIndicesByPainter(indices, output);
        m.reloadIndices();
        m.getMesh().ifPresent(this::drawMesh);
    }

    private void sortIndicesByPainter(int[] indices, float[] output) {
        // get max Z for each triangle
        float[] maxZ = new float[(indices.length / 3)];
        for (int i = 0; i < maxZ.length; i++) {
            var max = Math.max(output[indices[i]], output[indices[i + 1]]);
            max = Math.max(output[indices[i + 2]], max);
            maxZ[i] = max;
        }

        // sort indices based on the values in maxZ
        quicksort(maxZ, 0, maxZ.length - 1, indices);
    }

    private static float[] quicksort(float[] maxZs, int from, int to, int[] indices) {
        if (maxZs.length <= 1 || to - from <= 0) {
            return maxZs;
        }
        // Sets pivot value to rightmost value in list
        var pivot = maxZs[to];
        // Compares values on either side of pivot until one is greater than pivot value
        int i = from;
        int j = to;
        while (i <= j) {
            while (pivot > maxZs[i]) {
                i++;
            }
            while (pivot < maxZs[j]) {
                j--;
            }
            if (i <= j) {
                // swap z values
                var temp = maxZs[i];
                maxZs[i] = maxZs[j];
                maxZs[j] = temp;

                // swap indices
                var tempA = indices[i];
                var tempB = indices[i + 1];
                var tempC = indices[i + 2];
                indices[i] = indices[j];
                indices[i + 1] = indices[j + 1];
                indices[i + 2] = indices[j + 2];
                indices[j] = tempA;
                indices[j + 1] = tempB;
                indices[j + 2] = tempC;

                i++;
                j--;
            }
        }
        if (from < j) {
            maxZs = quicksort(maxZs, from, j, indices);
        }
        if (i < to) {
            maxZs = quicksort(maxZs, i, to, indices);
        }
        return maxZs;
    }

    private float[] transformVertices(Renderable m, Matrix4f worldMatrix, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        // prepare pipeline
        var original = m.getVertices();
        var output = Arrays.copyOf(original, original.length);
        // combine matrices
        var transform = worldMatrix
                .mul(projectionMatrix)
                .mul(viewMatrix)
                .mul(worldMatrix);
        var callables = new ArrayList<ShaderCallable>(MAX_THREADS);
        var increments = output.length / MAX_THREADS;

        // allocate section of float array to each thread
        var start = 0;
        var end = 0;
        for (int i = 0; i < MAX_THREADS; i++) {
            start = end;
            end = start + increments;
            if (end > output.length) {
                end = output.length;
            }
            // ensure end allows for multiple of 3 i.e. coordinates arent split between jobs
            var length = end - start;
            var remainder = length % 3;
            if (length % 3 != 0) {
                end += remainder;
            }
            callables.add(new ShaderCallable(output, start, end, transform));
        }

        // carry out transforms
        try {
            var futures = threadPoolExecutor.invokeAll(callables);
        } catch (InterruptedException e) {
            System.err.println("Failed to execute tasks");
            e.printStackTrace();
        }

        return output;
    }
}
