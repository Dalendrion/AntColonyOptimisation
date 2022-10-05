package nl.arjenvermaas.math;

import java.awt.*;

public class MyMath {
    public static int weightedRandomIndex(double... weights) {
        if (weights.length == 1) {
            return 0;
        }
        double sum = sum(weights);
        if (sum == 0) {
            return (int) (Math.random() * weights.length);
        }
        double rand = java.lang.Math.random() * sum;
        float total = 0;
        int index = 0;
        for (double weight : weights) {
            if (rand < total + weight) {
                return index;
            }
            total += weight;
            index++;
        }
        return -1;
    }

    public static double sum(double... values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum;
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) java.lang.Math.sqrt(dx * dx + dy * dy);
    }

    public static float map(float num, float low1, float high1, float low2, float high2) {
        float d1 = (high1 - low1);
        float d2 = (high2 - low2);
        if (d1 == 0) {
            throw new IllegalArgumentException("Division by zero.");
        }
        float ratio = d2 / d1;
        return (num - low1) * ratio + low2;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static Color lerp(Color a, Color b, float t) {
        return new Color(
                (int) lerp(a.getRed(), b.getRed(), t),
                (int) lerp(a.getGreen(), b.getGreen(), t),
                (int) lerp(a.getBlue(), b.getBlue(), t),
                (int) lerp(a.getAlpha(), b.getAlpha(), t)
        );
    }

    public static float constrain(float num, float min, float max) {
        if (num < min) {
            return min;
        }
        return java.lang.Math.min(num, max);
    }
}
