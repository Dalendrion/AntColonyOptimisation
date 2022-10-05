package nl.arjenvermaas.animation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.sql.Timestamp;
import java.util.*;

public class AnimationPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(AnimationPanel.class);

    private final Timer timer;
    private final CanvasPanel canvas;
    private final JPanel buttonPanel;
    private final Map<String, Runnable> actions = new HashMap<>();
    private Timestamp newTimestamp;

    public AnimationPanel(CanvasPanel canvas) {
        this.canvas = canvas;
        int delay = 1000 / canvas.getFrameRate();

        timer = new Timer(delay, e -> performAction("Update"));
        JButton start = createButton("Start", "Start");
        JButton reset = createButton("Reset", "Reset");

        setActions();
        canvas.reset();

        buttonPanel = new JPanel();
        buttonPanel.add(start);
        buttonPanel.add(reset);
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.PAGE_START);

        add(canvas, BorderLayout.CENTER);
    }

    private JButton createButton(String text, String name) {
        JButton button = new JButton(text);
        button.setName(name);
        button.addActionListener(e -> performAction(button.getName()));
        return button;
    }

    private void setActions() {
        actions.put("Start", timer::start);
        actions.put("Reset", () -> {
            canvas.reset();
            timer.restart();
        });
        actions.put("Update", () -> {
            if (newTimestamp == null) {
                newTimestamp = new Timestamp(System.currentTimeMillis());
            }
            Timestamp oldTimestamp = newTimestamp;
            newTimestamp = new Timestamp(System.currentTimeMillis());
            canvas.update(newTimestamp.getTime() - oldTimestamp.getTime());
            canvas.repaint();
        });
    }

    public void addAction(String text, String name, Runnable action) {
        actions.put(name, action);
        buttonPanel.add(createButton(text, name));
    }

    private void performAction(final String action) {
        actions.getOrDefault(action, () -> logger.error("Could not find action {}", action)).run();
    }
}