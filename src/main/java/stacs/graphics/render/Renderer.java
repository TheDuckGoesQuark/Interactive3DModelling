package stacs.graphics.render;

import org.lwjgl.opengl.GL11;
import stacs.graphics.data.ResourceLoader;
import stacs.graphics.shader.ShaderProgram;

public class Renderer {

    private static final String MODEL_VIEW_MATRIX_NAME = "modelViewMatrix";
    private static final String PROJECTION_MATRIX_UNIFORM_NAME = "projectionMatrix";
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 100.f;
    private final String fragmentShaderResourceName;
    private final String vertexShaderResourceName;
    private final Transformation transformation;
    private ShaderProgram shaderProgram;

    public Renderer(String fragmentShaderResourceName, String vertexShaderResourceName) {
        this.fragmentShaderResourceName = fragmentShaderResourceName;
        this.vertexShaderResourceName = vertexShaderResourceName;
        this.transformation = new Transformation();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

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
        shaderProgram.createUniform(MODEL_VIEW_MATRIX_NAME);
    }

    public void render(Renderable renderable, Window window, Camera camera) {
        clear();

        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // update matrices
        var projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform(PROJECTION_MATRIX_UNIFORM_NAME, projectionMatrix);

        var viewMatrix = transformation.getViewMatrix(camera);
        var modelViewMatrix = transformation.getModelViewMatrix(renderable, viewMatrix);
        shaderProgram.setUniform(MODEL_VIEW_MATRIX_NAME, modelViewMatrix);

        // draw the vertices
        renderable.getMesh().render();

        shaderProgram.unbind();
    }
}
