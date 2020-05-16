package stacs.graphics.render.renderers;

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
}
