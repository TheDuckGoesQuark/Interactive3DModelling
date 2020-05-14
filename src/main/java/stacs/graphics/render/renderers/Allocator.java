package stacs.graphics.render.renderers;

import org.joml.Matrix4d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Allocator {

    public static List<ShaderCallable> allocate(float[] vertices, Matrix4f matrix, int maxThreads) {
        var callables = new ArrayList<ShaderCallable>();
        var increments = vertices.length / maxThreads;

        // allocate section of float array to each thread
        var start = 0;
        var end = 1;
        for (int i = 0; i < maxThreads; i++) {
            start = end - 1;
            end = start + increments;
            if (end > vertices.length) {
                end = vertices.length;
            }
            // ensure end allows for multiple of 3 i.e. coordinates arent split between jobs
            var length = end - start;
            var remainder = length % 3;
            if (length % 3 != 0) {
                end += remainder;
            }
            callables.add(new ShaderCallable(vertices, start, end, matrix));
        }

        return callables;
    }

}
