package nl.arjenvermaas.matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Matrix<T extends Number> {
    protected final int rows;
    protected final int cols;
    protected List<T> elements;

    protected Matrix(int rows, int cols) {
        this(rows, cols, new ArrayList<>());
    }

    protected Matrix(int rows, int cols, List<T> values) {
        this.rows = rows;
        this.cols = cols;
        this.elements = new ArrayList<>(values.size());
        for(int i = 0; i < rows * cols; i++) {
            if (i < values.size()) {
                elements.add(values.get(i));
            } else {
                elements.add(zero());
            }
        }
    }

    protected abstract T zero();

    public void set(int row, int col, T value) {
        elements.set(row * cols + col, value);
    }
    public T get(int row, int col) {
        return elements.get(row * cols + col);
    }

    public List<T> getRow(int row) {
        return elements.subList((row) * cols, (row + 1) * cols);
    }

    public int getNumRows() {
        return rows;
    }

    public int getNumCols() {
        return cols;
    }

    public int length() {
        return rows * cols;
    }

    public List<T> getData() {
        return Collections.unmodifiableList(elements);
    }

    public void apply(java.util.function.UnaryOperator<T> operator) {
        elements.replaceAll(operator);
    }
}
