package stacs.graphics.data;

import java.io.IOException;
import java.util.Arrays;

public class FaceLoader {

    private final int[] indices;

    public FaceLoader(String indicesResourceName) throws IOException {
        indices = loadIndicesFromResource(indicesResourceName);
    }

    private static int[] loadIndicesFromResource(String indicesResourceName) throws IOException {
        var csvResourceLoader = new CSVResourceLoader();

        return csvResourceLoader.readToFlatList(indicesResourceName)
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public Face loadFromResource(String name) throws IOException {
        var csvResourceLoader = new CSVResourceLoader();

        var vertexStringValues = csvResourceLoader.readToFlatList(name);
        var vertices = new float[vertexStringValues.size()];
        for (int i = 0; i < vertexStringValues.size(); i++) {
            vertices[i] = Float.parseFloat(vertexStringValues.get(i));
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
