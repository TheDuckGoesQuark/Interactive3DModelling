package stacs.graphics.data;

import stacs.graphics.render.Mesh;
import stacs.graphics.render.MeshLoader;

public class Face {
    private float MAX = 100000f;
    private float MIN = -100000f;
    private final float[] vertices;
    private final int[] indices;
    private final Mesh mesh;

    public Face(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
        this.mesh = MeshLoader.createMesh(normalise(this.vertices), this.indices);
    }

    private float[] normalise(float[] vertices) {
        float[] normalised = new float[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            normalised[i] = 2f * ((vertices[i] - MIN) / (MAX - MIN)) - 1f;
        }
        return normalised;
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
