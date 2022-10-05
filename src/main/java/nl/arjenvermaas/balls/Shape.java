package nl.arjenvermaas.balls;

import java.awt.*;

public class Shape {
    private final int x;
    private int y;
    private final int size;
    private final Color color;
    private int speed;

    public Shape(int x, int y, int size, int speed, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.color = color;
    }

    public void drawShape(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, size, size);
    }

    public void move(long delta_time) {
        if (y <= 50) {
            speed = Math.abs(speed);
        }

        y += speed * delta_time / 1000;
    }
}
