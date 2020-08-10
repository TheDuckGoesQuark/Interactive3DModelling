package stacs.graphics.render.renderers;

import org.joml.Vector3f;

public class Util {
    public static float[] quicksortByZValue(float[] maxZs, int from, int to, int[] indices) {
        if (maxZs.length <= 1 || to - from <= 0) {
            return maxZs;
        }

        // Sets pivot value to rightmost value in list
        var pivot = maxZs[to];

        // Compares values on either side of pivot until one is greater than pivot value
        int i = from;
        int j = to;
        while (i <= j) {
            while (pivot > maxZs[i]) {
                i++;
            }
            while (pivot < maxZs[j]) {
                j--;
            }
            if (i <= j) {
                // swap z max values
                var temp = maxZs[i];
                maxZs[i] = maxZs[j];
                maxZs[j] = temp;

                // swap indices
                var iIndex = i * 3;
                var jIndex = j * 3;
                var tempA = indices[iIndex];
                var tempB = indices[iIndex + 1];
                var tempC = indices[iIndex + 2];
                indices[iIndex] = indices[jIndex];
                indices[iIndex + 1] = indices[jIndex + 1];
                indices[iIndex + 2] = indices[jIndex + 2];
                indices[jIndex] = tempA;
                indices[jIndex + 1] = tempB;
                indices[jIndex + 2] = tempC;

                i++;
                j--;
            }
        }
        if (from < j) {
            maxZs = quicksortByZValue(maxZs, from, j, indices);
        }
        if (i < to) {
            maxZs = quicksortByZValue(maxZs, i, to, indices);
        }
        return maxZs;
    }

    public static float[] getMaxZs(int[] indices, float[] coordinates) {
        var maxZ = new float[(indices.length / 3)];

        for (int i = 0; i < indices.length; i += 3) {
            var zA = coordinates[indices[i] * 3 + 2];
            var zB = coordinates[indices[i + 1] * 3 + 2];
            var zC = coordinates[indices[i + 2] * 3 + 2];
            var max = Math.max(Math.max(zA, zB), zC);
            maxZ[i / 3] = max;
        }

        return maxZ;
    }

    public static float[] vertexNormals(float[] vertices, int[] indices, float[] vertexNormals) {
        // for each face, compute normal
        for (int i = 0; i < indices.length; i += 3) {
            var indexA = indices[i];
            var indexB = indices[i + 1];
            var indexC = indices[i + 2];
            var A = new Vector3f(vertices[indexA], vertices[indexA + 1], vertices[indexA + 2]);
            var B = new Vector3f(vertices[indexB], vertices[indexB + 1], vertices[indexB + 2]);
            var C = new Vector3f(vertices[indexC], vertices[indexC + 1], vertices[indexC + 2]);

            var AB = B.sub(A);
            var AC = C.sub(A);

            var planeNormal = AB.cross(AC);

            // accumulate normals
            vertexNormals[indexA] += planeNormal.x;
            vertexNormals[indexA + 1] += planeNormal.y;
            vertexNormals[indexA + 2] += planeNormal.z;

            vertexNormals[indexB] += planeNormal.x;
            vertexNormals[indexB + 1] += planeNormal.y;
            vertexNormals[indexB + 2] += planeNormal.z;

            vertexNormals[indexC] += planeNormal.x;
            vertexNormals[indexC + 1] += planeNormal.y;
            vertexNormals[indexC + 2] += planeNormal.z;
        }

        // normalise
        for (int i = 0; i < vertices.length; i += 3) {
            var normal = new Vector3f(vertexNormals[i], vertexNormals[i + 1], vertexNormals[i + 2]);
            normal.normalize();
            vertexNormals[i] = normal.x;
            vertexNormals[i + 1] = normal.y;
            vertexNormals[i + 2] = normal.z;
        }

        return vertexNormals;
    }
}
