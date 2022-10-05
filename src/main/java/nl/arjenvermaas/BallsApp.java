package nl.arjenvermaas;

import nl.arjenvermaas.animation.AnimationPanel;
import nl.arjenvermaas.balls.BallsCanvas;

import javax.swing.*;

public class BallsApp {
    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            BallsCanvas canvas = new BallsCanvas(400, 400);
            frame.add(new AnimationPanel(canvas));
            frame.setResizable(false);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
