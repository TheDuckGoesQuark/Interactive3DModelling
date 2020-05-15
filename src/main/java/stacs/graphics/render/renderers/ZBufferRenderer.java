package stacs.graphics.render.renderers;

import org.joml.*;
import org.lwjgl.opengl.GL11;
import stacs.graphics.data.ResourceLoader;
import stacs.graphics.render.*;

public class ZBufferRenderer extends Renderer {

    private static final String WORLD_MATRIX_NAME = "worldMatrix";
    private static final String PROJECTION_MATRIX_UNIFORM_NAME = "projectionMatrix";
    private static final String VIEW_MATRIX_NAME = "viewMatrix";
    private final String fragmentShaderResourceName = "shaders/fragment.shader";
    private final String vertexShaderResourceName = "shaders/vertex.shader";

    @Override
    public void init() throws Exception {
        // set up shaders
        var resourceLoader = ResourceLoader.getInstance();
        var fragmentShader = resourceLoader.readAllToString(fragmentShaderResourceName);
        var vertexShader = resourceLoader.readAllToString(vertexShaderResourceName);
        shaderProgram = new ShaderProgram();
        shaderProgram.createFragmentShader(fragmentShader);
        shaderProgram.createVertexShader(vertexShader);
        shaderProgram.link();

        shaderProgram.createUniform(PROJECTION_MATRIX_UNIFORM_NAME);
        shaderProgram.createUniform(VIEW_MATRIX_NAME);
        shaderProgram.createUniform(WORLD_MATRIX_NAME);

        // enable use of z-buffer for rendering triangles in correct order
        // opengl implements this for us and is performed on the GPU
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void render(Renderable sceneRoot, Window window, Camera camera) {
        clear();

        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // update matrices
        var projectionMatrix = transformation.getPerspectiveProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform(PROJECTION_MATRIX_UNIFORM_NAME, projectionMatrix);

        var viewMatrix = transformation.getViewMatrix(camera);
        shaderProgram.setUniform(VIEW_MATRIX_NAME, viewMatrix);

        render(sceneRoot, null);

        shaderProgram.unbind();
    }

    @Override
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

    private void render(Renderable renderable, Matrix4f parentWorldMatrix) {
        final Matrix4f worldMatrix;
        if (parentWorldMatrix == null) {
            // this node is root
            worldMatrix = transformation.getWorldMatrix(renderable);
        } else {
            // translate child relative to parent
            worldMatrix = new Matrix4f(parentWorldMatrix)
                    .mul(transformation.getWorldMatrix(renderable));
        }

        shaderProgram.setUniform(ZBufferRenderer.WORLD_MATRIX_NAME, worldMatrix);
        renderable.getMesh().ifPresent(this::drawMesh);

        for (Renderable child : renderable.getChildren()) {
            // render children
            render(child, worldMatrix);
        }
    }

}
