package nl.arjenvermaas.towns;

import java.awt.*;

public final class Road {
    private final Town from;
    private final Town to;
    private final float length;
    private float pheromone;

    public Road(Town from, Town to, float length, float pheromone) {
        this.from = from;
        this.to = to;
        this.length = length;
        this.pheromone = pheromone;
    }

    public Town getFrom() {
        return from;
    }

    public Town getTo() {
        return to;
    }

    public float getLength() {
        return length;
    }

    public float getPheromone() {
        return pheromone;
    }

    public void setPheromone(float pheromone) {
        this.pheromone = pheromone;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        Town from = getFrom();
        Town to = getTo();
        g.drawLine(Math.round(from.x()), Math.round(from.y()), Math.round(to.x()), Math.round(to.y()));
    }
}
