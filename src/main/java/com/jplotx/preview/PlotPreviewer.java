package com.jplotx.preview;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

public final class PlotPreviewer {

    private PlotPreviewer() {
    }

    public static void show(BufferedImage image, String title) {
        if (GraphicsEnvironment.isHeadless()) {
            throw new IllegalStateException("Preview is unavailable in a headless environment. Use save(...) or render() instead.");
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title == null || title.isBlank() ? "JPlotX Preview" : title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new JScrollPane(new JLabel(new ImageIcon(image))));
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }
}
