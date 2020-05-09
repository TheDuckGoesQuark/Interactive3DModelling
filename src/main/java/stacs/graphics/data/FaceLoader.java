package stacs.graphics.data;

import java.io.IOException;

public class FaceLoader {

    private final int[] indices;
    private final float[] averageFaceShapeCoords;
    private final float[] vertexWeightings;
    private final float[] averageFaceColour;
    private final float[] colourWeightings;

    public FaceLoader(String indicesResourceName,
                      String averageFaceShapeCoordinatesResourceName,
                      String vertexWeightingsResourceName,
                      String averageFaceColoursResourceName,
                      String colourWeightingsResourceName) throws IOException {
        indices = loadIndicesFromResource(indicesResourceName);

        averageFaceShapeCoords = loadFloatsFromCSV(averageFaceShapeCoordinatesResourceName);
        vertexWeightings = loadFloatsFromCSV(vertexWeightingsResourceName);

        averageFaceColour = loadFloatsFromCSV(averageFaceColoursResourceName);
        colourWeightings = loadFloatsFromCSV(colourWeightingsResourceName);
    }

    private static float[] loadFloatsFromCSV(String weightingsResourceName) throws IOException {
        var csvResourceLoader = ResourceLoader.getInstance();

        var weightingStringValues = csvResourceLoader.readToFlatList(weightingsResourceName);
        var weightings = new float[weightingStringValues.size()];
        for (int i = 0; i < weightingStringValues.size(); i++) {
            weightings[i] = Float.parseFloat(weightingStringValues.get(i));
        }

        return weightings;
    }

    private static int[] loadIndicesFromResource(String indicesResourceName) throws IOException {
        var csvResourceLoader = ResourceLoader.getInstance();

        return csvResourceLoader.readToFlatList(indicesResourceName)
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public Face loadFromResource(int faceNumber) throws IOException {
        var coordinatesResourceName = String.format("sh_%03d.csv", faceNumber);
        var vertices = loadFloatsFromCSV(coordinatesResourceName);
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = averageFaceShapeCoords[i] + (vertexWeightings[faceNumber] * vertices[i]);
        }

        var coloursResourceName = String.format("tx_%03d.csv", faceNumber);
        var colours = loadFloatsFromCSV(coloursResourceName);
        for (int i = 0; i < colours.length; i++) {
            colours[i] = averageFaceColour[i] + (colourWeightings[faceNumber] * colours[i]);
        }

        return new Face(vertices, colours, indices);
    }

    public Face[] loadFromResources(int[] faceNumbers) throws IOException {
        var faces = new Face[faceNumbers.length];

        for (int i = 0; i < faces.length; i++) {
            faces[i] = loadFromResource(faceNumbers[i]);
        }

        return faces;
    }
}
