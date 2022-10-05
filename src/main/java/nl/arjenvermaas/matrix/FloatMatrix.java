package nl.arjenvermaas.matrix;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FloatMatrix extends Matrix<Float> {

    public FloatMatrix(int rows, int cols) {
        super(rows, cols);
    }
    public FloatMatrix(int rows, int cols, List<Float> data) {
        super(rows, cols, data);
    }
    public FloatMatrix(int rows, int cols, Float... data) {
        super(rows, cols, List.of(data));
    }

    @Override
    protected Float zero() {
        return 0F;
    }

    @Override
    public String toString() {
        return IntStream.range(0, getNumRows())
                .mapToObj(this::getRow)
                .map(row -> row.stream().map(f -> String.format("%.3f", f)).collect(Collectors.joining("\t")))
                .collect(Collectors.joining("\n"));
    }
}
