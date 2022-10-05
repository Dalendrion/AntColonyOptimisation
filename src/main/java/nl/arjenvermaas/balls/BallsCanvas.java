package nl.arjenvermaas.balls;

import nl.arjenvermaas.animation.CanvasPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BallsCanvas extends CanvasPanel {
    private final java.util.List<Color> colors;
    private java.util.List<Shape> shapes;

    public BallsCanvas(int width, int height) {
        this(width, height, 60);
    }

    public BallsCanvas(int width, int height, int frameRate) {
        super(width, height, frameRate);
        this.colors = createColorList();
    }

    @Override
    public void reset() {
        shapes = createShapeList();
    }

    @Override
    protected void update(long delta_time) {
        for (Shape shape : shapes) {
            shape.move(delta_time);
        }
    }

    @Override
    protected void draw(Graphics g) {
        for (Shape shape : shapes) {
            shape.drawShape(g);
        }
    }

    private java.util.List<Shape> createShapeList() {
        java.util.List<Shape> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int randXLoc = random.nextInt(getCanvasWidth());
            int randomDelayedStart = random.nextInt(100);
            int colorIndex = random.nextInt(colors.size());
            Color color = colors.get(colorIndex);
            list.add(new Shape(randXLoc, getCanvasWidth() + randomDelayedStart, 30, -100, color));
        }

        return list;
    }

    private java.util.List<Color> createColorList() {
        List<Color> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.ORANGE);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(Color.PINK);
        return colors;
    }
}
