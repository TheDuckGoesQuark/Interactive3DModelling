package stacs.graphics.logic;

import org.joml.Vector3f;
import stacs.graphics.render.Renderable;

public class Marker extends Renderable {

    private static final float[] POSITIONS = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };

    private static final float[] COLOURS = {
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
    };

    private static final int[] INDICES = {
            0, 1, 2
    };

    public Marker() {
        super(POSITIONS, COLOURS, INDICES);
    }

    public void setColour(Vector3f colour) {
        var colours = this.getColours();
        for (int i = 0; i < colours.length; i += 3) {
            colours[i] = colour.x;
            colours[i + 1] = colour.y;
            colours[i + 2] = colour.z;
        }
        reloadColours();
    }
}
