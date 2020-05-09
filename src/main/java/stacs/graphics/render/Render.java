package stacs.graphics.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import stacs.graphics.data.ResourceLoader;
import stacs.graphics.shader.ShaderProgram;

public class Render {

    private ShaderProgram shaderProgram;
    private final String fragmentShaderResourceName;
    private final String vertexShaderResourceName;

    public Render(String fragmentShaderResourceName, String vertexShaderResourceName) {
        this.fragmentShaderResourceName = fragmentShaderResourceName;
        this.vertexShaderResourceName = vertexShaderResourceName;
    }

    public void cleanup() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }

    public void init() throws Exception {
        var resourceLoader = ResourceLoader.getInstance();
        var fragmentShader = resourceLoader.readAllToString(fragmentShaderResourceName);
        var vertexShader = resourceLoader.readAllToString(vertexShaderResourceName);

        shaderProgram = new ShaderProgram();
        shaderProgram.createFragmentShader(fragmentShader);
        shaderProgram.createVertexShader(vertexShader);
        shaderProgram.link();
    }

    public void render(Mesh mesh) {
        shaderProgram.bind();

        // bind to the VAO
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(Attribute.COORDINATES.getIndex());
        GL20.glEnableVertexAttribArray(Attribute.COLOUR.getIndex());

        // draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // restore state
        GL20.glDisableVertexAttribArray(Attribute.COORDINATES.getIndex());
        GL20.glDisableVertexAttribArray(Attribute.COLOUR.getIndex());
        GL30.glBindVertexArray(0);

        shaderProgram.unbind();
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }
}
