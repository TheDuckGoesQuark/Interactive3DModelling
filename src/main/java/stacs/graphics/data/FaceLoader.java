package stacs.graphics.data;

import java.io.IOException;

public class FaceLoader {

    private final int[] indices;
    private final float[] averageFaceShapeCoords;
    private final float[] weightings;

    public FaceLoader(String indicesResourceName, String averageFaceShapeCoordinatesResourceName, String weightingsResourceName) throws IOException {
        indices = loadIndicesFromResource(indicesResourceName);
        averageFaceShapeCoords = loadCoordinatesFromResource(averageFaceShapeCoordinatesResourceName);
        weightings = loadWeightingsFromResource(weightingsResourceName);
    }

    private static float[] loadWeightingsFromResource(String weightingsResourceName) throws IOException {
        var csvResourceLoader = new CSVResourceLoader();

        var weightingStringValues = csvResourceLoader.readToFlatList(weightingsResourceName);
        var weightings = new float[weightingStringValues.size()];
        for (int i = 0; i < weightingStringValues.size(); i++) {
            weightings[i] = Float.parseFloat(weightingStringValues.get(i));
        }

        return weightings;
    }

    private static int[] loadIndicesFromResource(String indicesResourceName) throws IOException {
        var csvResourceLoader = new CSVResourceLoader();

        return csvResourceLoader.readToFlatList(indicesResourceName)
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private float[] loadCoordinatesFromResource(String name) throws IOException {
        var csvResourceLoader = new CSVResourceLoader();

        var vertexStringValues = csvResourceLoader.readToFlatList(name);
        var vertices = new float[vertexStringValues.size()];
        for (int i = 0; i < vertexStringValues.size(); i++) {
            vertices[i] = Float.parseFloat(vertexStringValues.get(i));
        }

        return vertices;
    }

    public Face loadFromResource(int faceNumber) throws IOException {
        var name = String.format("sh_%03d.csv", faceNumber);
        var vertices = loadCoordinatesFromResource(name);
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = averageFaceShapeCoords[i] + (weightings[faceNumber] * vertices[i]);
        }

        return new Face(vertices, indices);
    }

    public Face[] loadFromResources(int[] faceNumbers) throws IOException {
        var faces = new Face[faceNumbers.length];

        for (int i = 0; i < faces.length; i++) {
            faces[i] = loadFromResource(faceNumbers[i]);
        }

        return faces;
    }
}
