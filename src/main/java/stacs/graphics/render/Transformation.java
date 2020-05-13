package stacs.graphics.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f projectionMatrix;
    private final Matrix4f worldMatrix;
    private final Matrix4f viewMatrix;

    public Transformation() {
        this.projectionMatrix = new Matrix4f();
        this.worldMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
    }

    public final Matrix4f getPerspectiveProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        return projectionMatrix
                .setPerspective(fov, width / height, zNear, zFar);
    }

//    public final Matrix4f getOrthographicProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
//        return projectionMatrix
//                .identity()
//                .setOrtho(0.0f, width, height, 5.0f, zNear, zFar);
//    }

    public Matrix4f getViewMatrix(Camera camera) {
        var cameraPos = camera.getPosition();
        var rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public Matrix4f getWorldMatrix(Renderable renderable) {
        Vector3f rotation = renderable.getRotation();
        worldMatrix.identity().translate(renderable.getPosition()).
                rotateX((float) Math.toRadians(-rotation.x)).
                rotateY((float) Math.toRadians(-rotation.y)).
                rotateZ((float) Math.toRadians(-rotation.z)).
                scale(renderable.getScale());

        return worldMatrix;
    }
}
