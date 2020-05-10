package stacs.graphics.data;

import stacs.graphics.render.MeshLoader;
import stacs.graphics.render.Renderable;

public class Face extends Renderable {

    private final float[] vertices;
    private final float[] colours;
    private final int[] indices;

    public Face(float[] vertices, float[] colours, int[] indices) {
        super(MeshLoader.createMesh(vertices, colours, indices));
        this.vertices = vertices;
        this.colours = colours;
        this.indices = indices;
    }
}
