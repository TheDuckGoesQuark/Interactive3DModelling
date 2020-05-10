package stacs.graphics.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Mesh {

    private int vao;
    private int vertices;

    public Mesh(int vao, int vertex) {
        this.vao = vao;
        this.vertices = vertex;
    }

    public int getVaoID() {
        return vao;
    }

    public int getVertexCount() {
        return vertices;
    }

    public void render() {
        // bind to the VAO
        GL30.glBindVertexArray(this.getVaoID());
        GL20.glEnableVertexAttribArray(Attribute.COORDINATES.getIndex());
        GL20.glEnableVertexAttribArray(Attribute.COLOUR.getIndex());

        // draw
        GL11.glDrawElements(GL11.GL_TRIANGLES, this.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // restore state
        GL20.glDisableVertexAttribArray(Attribute.COORDINATES.getIndex());
        GL20.glDisableVertexAttribArray(Attribute.COLOUR.getIndex());
        GL30.glBindVertexArray(0);
    }
}