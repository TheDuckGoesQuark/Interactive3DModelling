package stacs.graphics.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class MeshLoader {

    private static List<Integer> VAOS = new ArrayList<>();
    private static List<Integer> VBOS = new ArrayList<>();

    private static FloatBuffer createFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static IntBuffer createIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static int storeData(Attribute attribute, int dimensions, float[] data) {
        int vbo = GL15.glGenBuffers(); //Creates a VBO ID
        VBOS.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); //Loads the current VBO to store the data
        var buffer = createFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribute.getIndex(), dimensions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //Unloads the current VBO when done.
        return vbo;
    }

    private static int bindIndices(int[] data) {
        int vbo = GL15.glGenBuffers();
        VBOS.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = createIntBuffer(data);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        return vbo;
    }

    private static int genVAO() {
        int vao = GL30.glGenVertexArrays();
        VAOS.add(vao);
        GL30.glBindVertexArray(vao);
        return vao;
    }

    public static Mesh createMesh(float[] positions, float[] colours, int[] indices, float[] normals) {
        int vao = genVAO();
        int coordinateVBO = storeData(Attribute.COORDINATES, 3, positions);
        int colourVBO = storeData(Attribute.COLOUR, 3, colours);
        int normalsVBO = storeData(Attribute.NORMALS, 3, normals);
        int indicesVBO = bindIndices(indices);
        GL30.glBindVertexArray(0);

        return new Mesh(vao, indices.length, coordinateVBO, colourVBO, indicesVBO, normalsVBO);
    }

    public static void updateIndices(Mesh mesh, int[] newIndices) {
        // bind vao to operate on it
        GL30.glBindVertexArray(mesh.getVaoID());
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndicesVBO());

        // create buffer for update indices
        var intBuffer = createIntBuffer(newIndices);

        // update contents of buffer
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);

        // restore state
        GL30.glBindVertexArray(0);
    }

    public static void updateAttribute(Mesh mesh, float[] newValues, Attribute attribute) {
        // bind vao to operate on it
        GL30.glBindVertexArray(mesh.getVaoID());

        switch (attribute) {
            case COORDINATES:
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getCoordinateVBO());
                break;
            case COLOUR:
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getColourVBO());
                break;
            case NORMALS:
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.getNormalsVBO());
                break;
        }

        // create buffer for update indices
        var floatBuffer = createFloatBuffer(newValues);

        // update contents of buffer
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STATIC_DRAW);

        // restore state
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }
}
