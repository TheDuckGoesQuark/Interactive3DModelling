package stacs.graphics.render.renderers;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
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

    public Vector2f invertScreenCoordinates(Window window, Camera camera, Vector2d position) {
        // get normalised device coordinates
        var normalisedX = (2 * ((float) position.x) / ((float) window.getWidth())) - 1;
        var normalisedY = 1 - (2 * ((float) position.y) / ((float) window.getHeight()));
        var deviceCoordinates = new Vector3f(normalisedX, normalisedY, 1.0f);

        // clip coordinates
        var homogenousClipCoordinates = new Vector4f(deviceCoordinates.x, deviceCoordinates.y, -1.0f, 1.0f);

        // camera coordinates
        var rayEye = transformation.getPerspectiveProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR)
                .invert().transform(homogenousClipCoordinates);

        // only need x,y, so specify z as 'forwards' (i.e. the ray going into the screen)
        rayEye.z = -1.0f;
        rayEye.w = 0.0f;

        // world coordinates
        var rayWorld = transformation.getViewMatrix(camera).invert().transform(rayEye);

        return new Vector2f(rayWorld.x, rayWorld.y);
    }

}
