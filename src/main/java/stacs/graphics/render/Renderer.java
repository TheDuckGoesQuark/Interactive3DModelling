package stacs.graphics.render;

import org.lwjgl.opengl.GL11;

public abstract class Renderer {
    public static final String WORLD_MATRIX_NAME = "worldMatrix";
    public static final String PROJECTION_MATRIX_UNIFORM_NAME = "projectionMatrix";
    public static final String VIEW_MATRIX_NAME = "viewMatrix";
    public static final float FOV = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 100.f;

    public void cleanup() {

    }

    public void init() throws Exception {

    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public abstract void render(Renderable sceneRoot, Window window, Camera camera);

}
