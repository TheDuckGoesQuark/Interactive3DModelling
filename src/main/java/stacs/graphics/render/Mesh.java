package stacs.graphics.render;

public class Mesh {

    private int vao;
    private int vertices;
    private final int coordinateVBO;
    private final int colourVBO;
    private final int indicesVBO;

    public Mesh(int vao, int vertex, int coordinateVBO, int colourVBO, int indicesVBO) {
        this.vao = vao;
        this.vertices = vertex;
        this.coordinateVBO = coordinateVBO;
        this.colourVBO = colourVBO;
        this.indicesVBO = indicesVBO;
    }

    public int getVaoID() {
        return vao;
    }

    public int getVertexCount() {
        return vertices;
    }

    public int getCoordinateVBO() {
        return coordinateVBO;
    }

    public int getColourVBO() {
        return colourVBO;
    }

    public int getIndicesVBO() {
        return indicesVBO;
    }
}