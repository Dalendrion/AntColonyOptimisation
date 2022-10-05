package nl.arjenvermaas;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.arjenvermaas.math.MyMath;
import nl.arjenvermaas.matrix.IntMatrix;

import java.text.MessageFormat;

public class MatrixTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MatrixTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MatrixTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testMatrixApp() {
        Integer[] elements = new Integer[] {
                1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12
        };
        IntMatrix matrix = new IntMatrix(3, 4, elements);


        for (int i = 0; i < elements.length; i++) {
            int row = i / 4;
            int col = i % 4;
            int expectedVal = elements[i];
            int val = matrix.get(row, col);
            System.out.println(MessageFormat.format("Element: ({0}, {1}) = {2}", row, col, val));
            assertEquals(expectedVal, val);
        }
    }

    public void testWeightedRandomSum() {
        double[] values = {10, 20, 30, 40};
        assertEquals(100.0, MyMath.sum(values));
    }
}
