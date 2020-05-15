package stacs.graphics.logic;

import org.joml.Vector2f;
import org.joml.Vector3f;
import stacs.graphics.data.Face;
import stacs.graphics.render.Renderable;

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
    private final Vector3f weighting = new Vector3f();

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
    public void addControlFaces(Face[] controlFaces) throws Exception {
        if (controlFaces.length < 3) {
            throw new Exception("Too few control faces");
        }

        controlFaces[0].setPosition(POSITIONS[0], POSITIONS[1], POSITIONS[2]);
        controlFaces[1].setPosition(POSITIONS[3], POSITIONS[4], POSITIONS[5]);
        controlFaces[2].setPosition(POSITIONS[6], POSITIONS[7], POSITIONS[8]);

        for (Face controlFace : controlFaces) {
            controlFace.setScale(0.0000015f);
            addChild(controlFace);
        }
    }

    public void setCurrentWeighting(Vector3f triangleCoordinate) {
        // utilise the fact that this is an isosceles triangle to calculate max distances
        var AB = POINT_A.distance(POINT_B);
        var BC = POINT_B.distance(POINT_C);
        var AB2 = Math.pow(AB, 2);
        var BC2 = Math.pow(BC, 2);
        var aMax = (float) Math.sqrt(AB2 - (BC2 / 2));
        var bcMax = (float) (AB * Math.sqrt(4 * AB2 - BC2) / (2 * AB2));

        var aDistance = POINT_A.distance(triangleCoordinate);
        var bDistance = POINT_B.distance(triangleCoordinate);
        var cDistance = POINT_C.distance(triangleCoordinate);

        // calculate weightings such that weighting is 1 when exactly at corner and 0 when exactly at opposite edge
        weighting.x = (aMax - aDistance) / aMax;
        weighting.y = (bcMax - bDistance) / bcMax;
        weighting.z = (bcMax - cDistance) / bcMax;

        updateMarker(triangleCoordinate);
    }

    private void updateMarker(Vector3f markerCoordinate) {
        marker.setPosition(markerCoordinate.x, markerCoordinate.y, 0.1f);
        System.out.println(markerCoordinate);
        marker.setColour(weighting);
    }

    public void calculateOutputFace(OutputFace outputFace, Face[] controlFaces) {
        // TODO
//        MeshLoader.updateCoordinates();
//        MeshLoader.updateColour();
    }
}
