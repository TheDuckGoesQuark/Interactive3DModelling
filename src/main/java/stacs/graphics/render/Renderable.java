package stacs.graphics.render;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class Renderable {

    private final List<Renderable> children;
    private final Mesh mesh;
    private final Vector3f position;
    private final Vector3f rotation;
    private float scale;

    public Renderable(Mesh mesh) {
        this.mesh = mesh;
        this.position = new Vector3f(0, 0, 0);
        this.scale = 1;
        this.rotation = new Vector3f(0, 0, 0);
        this.children = new ArrayList<>();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public List<Renderable> getChildren() {
        return children;
    }

    public void addChild(Renderable renderable) {
        children.add(renderable);
    }

    public void removeChild(Renderable renderable) {
        children.remove(renderable);
    }
}
