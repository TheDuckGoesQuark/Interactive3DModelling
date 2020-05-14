package stacs.graphics.logic;

import stacs.graphics.data.Face;
import stacs.graphics.render.Renderable;

public class SelectionArea extends Renderable {

    private static final float[] POSITIONS = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
    };

    private static final float[] COLOURS = {
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.0f, 0.5f,
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
    };

    // TODO use this to test

    private static final int[] INDICES = {
            0, 1, 2, 3, 4, 5
    };

    public SelectionArea() {
        super(POSITIONS, COLOURS, INDICES);
        this.setScale(1);
        this.setPosition(-1f, 0, 0);
    }

    /**
     * Sets the position of the given control faces to be the corners of the the triangle
     *
     * @param controlFaces array of control faces
     */
    public void addControlFaces(Face[] controlFaces) throws Exception {
        if (controlFaces.length < 3) {
            throw new Exception("Too few control faces");
        }

        controlFaces[0].setPosition(POSITIONS[0], POSITIONS[1], POSITIONS[2]);
        controlFaces[1].setPosition(POSITIONS[3], POSITIONS[4], POSITIONS[5]);
        controlFaces[2].setPosition(POSITIONS[6], POSITIONS[7], POSITIONS[8]);

        for (Face controlFace : controlFaces) {
            controlFace.setScale(0.0000025f);
//            addChild(controlFace);
        }
    }
}
