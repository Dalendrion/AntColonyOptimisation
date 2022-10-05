package nl.arjenvermaas.matrix;

import java.util.List;

public class IntMatrix extends Matrix<Integer> {
    public IntMatrix(int rows, int cols) {
        super(rows, cols);
    }
    public IntMatrix(int rows, int cols, List<Integer> data) {
        super(rows, cols, data);
    }
    public IntMatrix(int rows, int cols, Integer... data) {
        super(rows, cols, List.of(data));
    }

    @Override
    protected Integer zero() {
        return 0;
    }

    public IntMatrix expand(int additionalRows, int additionalCols, Integer defaultVal) {
        IntMatrix matrix = new IntMatrix(getNumRows() + additionalRows, getNumCols() + additionalCols);
        for (int row = 0; row < matrix.getNumRows(); row++) {
            for (int col = 0; col < matrix.getNumCols(); col++) {
                if (row < getNumRows() && col < getNumCols()) {
                    matrix.set(row, col, get(row, col));
                } else {
                    matrix.set(row, col, defaultVal);
                }
            }
        }
        return matrix;
    }
}
