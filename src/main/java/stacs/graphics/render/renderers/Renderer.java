package stacs.graphics.render.renderers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import stacs.graphics.data.ResourceLoader;
import stacs.graphics.render.*;

public abstract class Renderer {
    protected final Transformation transformation;
    protected static final float FOV = (float) Math.toRadians(60.0f);
    protected static final float Z_NEAR = 0.01f;
    protected static final float Z_FAR = 100.f;
    protected ShaderProgram shaderProgram;

    public Renderer() {
        this.transformation = new Transformation();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }

    public void init() throws Exception {
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public abstract void render(Renderable sceneRoot, Window window, Camera camera);

    protected void drawMesh(Mesh mesh) {
        // bind to the VAO
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(Attribute.COORDINATES.getIndex());
        GL20.glEnableVertexAttribArray(Attribute.COLOUR.getIndex());

        // draw
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // restore state
        GL20.glDisableVertexAttribArray(Attribute.COORDINATES.getIndex());
        GL20.glDisableVertexAttribArray(Attribute.COLOUR.getIndex());
        GL30.glBindVertexArray(0);
    }
}
