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

    private static void storeData(Attribute attribute, int dimensions, float[] data) {
        int vbo = GL15.glGenBuffers(); //Creates a VBO ID
        VBOS.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); //Loads the current VBO to store the data
        var buffer = createFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribute.getIndex(), dimensions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //Unloads the current VBO when done.
    }

    private static void bindIndices(int[] data) {
        int vbo = GL15.glGenBuffers();
        VBOS.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = createIntBuffer(data);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private static int genVAO() {
        int vao = GL30.glGenVertexArrays();
        VAOS.add(vao);
        GL30.glBindVertexArray(vao);
        return vao;
    }

    public static Mesh createMesh(float[] positions, float[] colours, int[] indices) {
        int vao = genVAO();
        storeData(Attribute.COORDINATES, 3, positions);
        storeData(Attribute.COLOUR, 3, colours);
        bindIndices(indices);
        GL30.glBindVertexArray(0);
        return new Mesh(vao, indices.length);
    }
}
