package stacs.graphics.data;

import stacs.graphics.render.MeshLoader;
import stacs.graphics.render.Renderable;

public class Face extends Renderable {
    private static final float MAX = 150000f;
    private static final float MIN = -150000f;

    private final float[] vertices;
    private final float[] colours;
    private final int[] indices;

    public Face(float[] vertices, float[] colours, int[] indices) {
        super(MeshLoader.createMesh(normalise(vertices), colours, indices));
        this.vertices = vertices;
        this.colours = colours;
        this.indices = indices;
    }

    private static float[] normalise(float[] vertices) {
        float[] normalised = new float[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            normalised[i] = 2f * ((vertices[i] - MIN) / (MAX - MIN)) - 1f;
        }
        return normalised;
    }
}
