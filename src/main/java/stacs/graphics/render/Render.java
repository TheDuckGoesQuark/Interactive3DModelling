package stacs.graphics.render;

import org.lwjgl.opengl.GL11;
import stacs.graphics.data.ResourceLoader;
import stacs.graphics.shader.ShaderProgram;

public class Render {

    private static final String WORLD_MATRIX_UNIFORM_NAME = "worldMatrix";
    private static final String PROJECTION_MATRIX_UNIFORM_NAME = "projectionMatrix";
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private final String fragmentShaderResourceName;
    private final String vertexShaderResourceName;
    private final Transformation transformation;
    private ShaderProgram shaderProgram;

    public Render(String fragmentShaderResourceName, String vertexShaderResourceName) {
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

    public void init(Window window) throws Exception {
        // set up shaders
        var resourceLoader = ResourceLoader.getInstance();
        var fragmentShader = resourceLoader.readAllToString(fragmentShaderResourceName);
        var vertexShader = resourceLoader.readAllToString(vertexShaderResourceName);
        shaderProgram = new ShaderProgram();
        shaderProgram.createFragmentShader(fragmentShader);
        shaderProgram.createVertexShader(vertexShader);
        shaderProgram.link();

//        shaderProgram.createUniform(PROJECTION_MATRIX_UNIFORM_NAME);
        shaderProgram.createUniform(WORLD_MATRIX_UNIFORM_NAME);

        window.setClearColour(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void render(Renderable renderable, Window window) {
        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // update matrices
        var projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
//        shaderProgram.setUniform(PROJECTION_MATRIX_UNIFORM_NAME, projectionMatrix);

        // draw the vertices
        var worldMatrix = transformation.getWorldMatrix(
                renderable.getPosition(),
                renderable.getRotation(),
                renderable.getScale()
        );
        shaderProgram.setUniform(WORLD_MATRIX_UNIFORM_NAME, worldMatrix);
        renderable.getMesh().render();

        shaderProgram.unbind();
    }
}
