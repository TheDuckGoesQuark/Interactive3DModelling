package stacs.graphics.data;

import java.io.IOException;

public class FaceLoader {

    private final int[] indices;
    private final float[] averageFaceShapeCoords;

    public FaceLoader(String indicesResourceName, String averageFaceShapeCoordinatesResourceName) throws IOException {
        indices = loadIndicesFromResource(indicesResourceName);
        averageFaceShapeCoords = loadCoordinatesFromResource(averageFaceShapeCoordinatesResourceName);
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

    public Face loadFromResource(String name) throws IOException {
        var vertices = loadCoordinatesFromResource(name);
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] += averageFaceShapeCoords[i];
        }
        return new Face(vertices, indices);
    }

    public Face[] loadFromResources(String[] strings) throws IOException {
        var faces = new Face[strings.length];

        for (int i = 0; i < faces.length; i++) {
            faces[i] = loadFromResource(strings[i]);
        }

        return faces;
    }
}
