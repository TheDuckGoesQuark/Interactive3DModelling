package stacs.graphics.render.renderers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class UtilTest {

    private static final float[] POSITIONS = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
    };

    private static final int[] INDICES = {
            0, 1, 2, 3, 4, 5
    };

    @Test
    public void testSorter() {
        var maxZs = Util.getMaxZs(INDICES, POSITIONS);
        assertEquals(2, maxZs.length);
        assertEquals(0, maxZs[0], 0.0f);
        assertEquals(-0.5, maxZs[1], 0.0f);

        var copy = Arrays.copyOf(INDICES, INDICES.length);
        Util.quicksortByZValue(maxZs, 0, maxZs.length - 1, copy);

        // sorting works
        assertEquals(-0.5, maxZs[0], 0);
        assertEquals(0.0, maxZs[1], 0);

        // index sorting also works
        assertEquals(3, copy[0]);
        assertEquals(4, copy[1]);
        assertEquals(5, copy[2]);
        assertEquals(0, copy[3]);
        assertEquals(1, copy[4]);
        assertEquals(2, copy[5]);
    }
}