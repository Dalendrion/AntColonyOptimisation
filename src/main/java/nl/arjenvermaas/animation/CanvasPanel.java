package nl.arjenvermaas.animation;

import javax.swing.*;
import java.awt.*;

public abstract class CanvasPanel extends JPanel {
    private final int frameRate;
    private final int dHeight;
    private final int dWidth;

    protected CanvasPanel(int width, int height, int frameRate) {
        this.frameRate = frameRate;
        this.dWidth = width;
        this.dHeight = height;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(dWidth, dHeight);
    }

    public int getCanvasWidth() {
        return dWidth;
    }

    public int getCanvasHeight() {
        return dHeight;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public abstract void reset();

    protected abstract void update(long delta_time);

    protected abstract void draw(Graphics g);
}
