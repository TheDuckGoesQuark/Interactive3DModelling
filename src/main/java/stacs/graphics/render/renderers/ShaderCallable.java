package stacs.graphics.render.renderers;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.concurrent.Callable;

public class ShaderCallable implements Callable<Void> {

    private final float[] values;
    private final int start;
    private final int end;
    private final Matrix4f matrix;
    private final Vector4f current;

    public ShaderCallable(float[] values, int start, int end, Matrix4f matrix) {
        this.values = values;
        this.start = start;
        this.end = end;
        this.matrix = matrix;
        this.current = new Vector4f();
        this.current.w = 1;
    }

    @Override
    public Void call() {
        for (int i = start; i < end && i < values.length; i += 3) {
            // copy to vector
            current.x = values[i];
            current.y = values[i + 1];
            current.z = values[i + 2];

            // apply
            matrix.transform(current);

            // write result back
            values[i] = current.x;
            values[i + 1] = current.y;
            values[i + 2] = current.z;
        }

        return null;
    }

}
