package stacs.graphics.logic;

import org.joml.Vector3f;
import stacs.graphics.data.Face;
import stacs.graphics.render.Attribute;
import stacs.graphics.render.MeshLoader;
import stacs.graphics.render.Renderable;
import stacs.graphics.render.renderers.Util;

public class SelectionArea extends Renderable {

    private static final Vector3f POINT_A = new Vector3f(0.0f, 0.5f, 0.0f);
    private static final Vector3f POINT_B = new Vector3f(-0.5f, -0.5f, 0.0f);
    private static final Vector3f POINT_C = new Vector3f(0.5f, -0.5f, 0.0f);

    private static final Vector3f RED = new Vector3f(0.5f, 0.0f, 0.0f);
    private static final Vector3f GREEN = new Vector3f(0.0f, 0.5f, 0.0f);
    private static final Vector3f BLUE = new Vector3f(0.0f, 0.0f, 0.5f);

    private static final float[] POSITIONS = {
            POINT_A.x, POINT_A.y, POINT_A.z,
            POINT_B.x, POINT_B.y, POINT_B.z,
            POINT_C.x, POINT_C.y, POINT_C.z,
    };

    private static final float[] COLOURS = {
            RED.x, RED.y, RED.z,
            GREEN.x, GREEN.y, GREEN.z,
            BLUE.x, BLUE.y, BLUE.z,
    };


    private static final int[] INDICES = {
            0, 1, 2
    };

    private final Marker marker = new Marker();
    private Vector3f weighting = new Vector3f();
    private Face[] controlFaces;

    public SelectionArea() {
        super(POSITIONS, COLOURS, INDICES);
        this.setScale(1);
        this.setPosition(0, 0, 0);

        // calculate center point
        var center = new Vector3f(
                (POINT_A.x + POINT_B.x + POINT_C.x) / 3.0f,
                (POINT_A.y + POINT_B.y + POINT_C.y) / 3.0f,
                0.0f
        );
        setCurrentWeighting(center);
        marker.setPosition(0, 0, 0.1f);
        marker.setScale(0.1f);
        addChild(marker);
    }

    /**
     * Sets the position of the given control faces to be the corners of the the triangle
     *
     * @param controlFaces array of control faces
     */
    public void setControlFaces(Face[] controlFaces) throws Exception {
        if (controlFaces.length < 3) {
            throw new Exception("Too few control faces");
        }

        controlFaces[0].setPosition(POINT_A.x, POINT_A.y, POINT_A.z);
        controlFaces[1].setPosition(POINT_B.x, POINT_B.y, POINT_B.z);
        controlFaces[2].setPosition(POINT_C.x, POINT_C.y, POINT_C.z);

        for (Face controlFace : controlFaces) {
            controlFace.setScale(0.0000015f);
            addChild(controlFace);
        }

        this.controlFaces = controlFaces;
    }

    public void setCurrentWeighting(Vector3f point) {
        var denominator = (POINT_B.y - POINT_C.y) * (POINT_A.x - POINT_C.x) + (POINT_C.x - POINT_B.x) * (POINT_A.y - POINT_C.y);
        var xNumerator = (POINT_B.y - POINT_C.y) * (point.x - POINT_C.x) + (POINT_C.x - POINT_B.x) * (point.y - POINT_C.y);
        var yNumerator = (POINT_C.y - POINT_A.y) * (point.x - POINT_C.x) + (POINT_A.x - POINT_C.x) * (point.y - POINT_C.y);

        var weighting = new Vector3f();
        weighting.x = xNumerator / denominator;
        weighting.y = yNumerator / denominator;
        weighting.z = 1 - weighting.x - weighting.y;

        // bounds check before updating
        if (!((weighting.x > 1 || weighting.x < 0) ||
                (weighting.y > 1 || weighting.y < 0) ||
                (weighting.z > 1 || weighting.z < 0)
        )) {
            this.weighting = weighting;
            updateMarker(point);
        }
    }

    private void updateMarker(Vector3f markerCoordinate) {
        marker.setPosition(markerCoordinate.x, markerCoordinate.y, 0.1f);
        marker.setColour(weighting);
    }

    public void updateOutputFace(OutputFace outputFace) {
        final var outputVertices = outputFace.getVertices();
        final var outputColours = outputFace.getColours();

        final var aVertices = controlFaces[0].getVertices();
        final var aColours = controlFaces[0].getColours();
        final var bVertices = controlFaces[1].getVertices();
        final var bColours = controlFaces[1].getColours();
        final var cVertices = controlFaces[2].getVertices();
        final var cColours = controlFaces[2].getColours();

        // use barycentric coordinates to produce coordinate from weightings
        for (int x = 0; x < outputVertices.length; x += 3) {
            var y = x + 1;
            var z = x + 2;
            outputVertices[x] = (weighting.x * aVertices[x]) + (weighting.y * bVertices[x]) + (weighting.z * cVertices[x]);
            outputVertices[y] = (weighting.x * aVertices[y]) + (weighting.y * bVertices[y]) + (weighting.z * cVertices[y]);
            outputVertices[z] = (weighting.x * aVertices[z]) + (weighting.y * bVertices[z]) + (weighting.z * cVertices[z]);

            outputColours[x] = (weighting.x * aColours[x]) + (weighting.y * bColours[x]) + (weighting.z * cColours[x]);
            outputColours[y] = (weighting.x * aColours[y]) + (weighting.y * bColours[y]) + (weighting.z * cColours[y]);
            outputColours[z] = (weighting.x * aColours[z]) + (weighting.y * bColours[z]) + (weighting.z * cColours[z]);
        }

        // recalculate normals since coordinates have changed
        Util.vertexNormals(outputVertices, outputFace.getIndices(), outputFace.getNormals());

        outputFace.reloadCoordinates();
        outputFace.reloadNormals();
        outputFace.reloadColours();
    }
}
