package stacs.graphics.render;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import stacs.graphics.data.ResourceLoader;

public class ZBufferRenderer extends Renderer {

    private final String fragmentShaderResourceName;
    private final String vertexShaderResourceName;
    private final Transformation transformation;
    private ShaderProgram shaderProgram;

    public ZBufferRenderer(String fragmentShaderResourceName, String vertexShaderResourceName) {
        this.fragmentShaderResourceName = fragmentShaderResourceName;
        this.vertexShaderResourceName = vertexShaderResourceName;
        this.transformation = new Transformation();
    }

    @Override
    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }

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
        renderable.getMesh().ifPresent(this::render);

        for (Renderable child : renderable.getChildren()) {
            // render children
            render(child, worldMatrix);
        }
    }

    private void render(Mesh mesh) {
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
