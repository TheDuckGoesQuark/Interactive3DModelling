package stacs.graphics.render.renderers;

public class Util {


    public static float[] quicksortByZValue(float[] maxZs, int from, int to, int[] indices) {
        if (from < to) {
            var partitionIndex = partition(maxZs, from, to, indices);

            quicksortByZValue(maxZs, from, partitionIndex - 1, indices);
            quicksortByZValue(maxZs, partitionIndex + 1, to, indices);
        }

        return maxZs;
    }

    private static int partition(float[] maxZs, int from, int to, int[] indices) {
        // Sets pivot value to rightmost value in list
        var pivot = maxZs[to];
        var i = (from - 1);
        for (int j = from; j < to; j++) {
            if (maxZs[j] <= pivot) {
                i++;
                swap(i, j, maxZs, indices);
            }
        }

        swap(i + 1, to, maxZs, indices);

        return i + 1;
    }

    private static void swap(int i, int j, float[] maxZs, int[] indices) {
        var swapTemp = maxZs[i];
        maxZs[i] = maxZs[j];
        maxZs[j] = swapTemp;

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
