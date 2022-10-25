package nl.arjenvermaas;

import nl.arjenvermaas.animation.AnimationPanel;
import nl.arjenvermaas.towns.AcoConfig;
import nl.arjenvermaas.towns.TownsCanvas;

import javax.swing.*;

public class TownsApp {
    // TODO: https://www.theprojectspot.com/tutorial-post/ant-colony-optimization-for-hackers/10
    // Look at this!
    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            AcoConfig config = new AcoConfig.AcoConfigBuilder()
                    .withNumTowns(100)
                    .withNumAnts(20)
                    .build();
            TownsCanvas canvas = new TownsCanvas(700, 440, config);
            AnimationPanel animationPanel = new AnimationPanel(canvas);
            animationPanel.addAction("Erase Pheromones", "ResetPheromones", canvas::resetPathsWhenReady);
            animationPanel.addAction("Hide Paths", "HidePaths", canvas::hidePaths);
            frame.add(animationPanel);
            frame.setResizable(false);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
