package stacs.graphics.logic;

import stacs.graphics.data.Face;

public class OutputFace extends Face {
    public OutputFace(int numVertices, int[] indices) {
        super(new float[numVertices], new float[numVertices], indices);
    }
}
