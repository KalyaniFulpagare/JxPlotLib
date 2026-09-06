package com.jplotx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportServiceTest {

    private final ExportService exportService = new ExportService();

    private BufferedImage transparentImage() {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        // Leave every pixel fully transparent (alpha = 0) to exercise the
        // JPEG flattening path, which must not throw and must not leave
        // the transparent area black in the encoded output.
        return image;
    }

    @Test
    void writesPngByDefault(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("chart.png");
        Path written = exportService.writeChart(transparentImage(), file);

        assertEquals(file, written);
        assertTrue(written.toFile().length() > 0);
    }

    @Test
    void writesJpgWithFlattenedWhiteBackground(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("chart.jpg");
        Path written = exportService.writeChart(transparentImage(), file, "jpg");

        assertTrue(written.toFile().length() > 0);

        BufferedImage readBack = javax.imageio.ImageIO.read(written.toFile());
        int rgb = readBack.getRGB(5, 5);
        Color color = new Color(rgb, false);

        // A fully-transparent source pixel should flatten to white, not black.
        assertTrue(color.getRed() > 200 && color.getGreen() > 200 && color.getBlue() > 200);
    }

    @Test
    void exportChartAppendsCorrectExtensionForJpeg(@TempDir Path tempDir) throws IOException {
        Path exported = exportService.exportChart(transparentImage(), tempDir, "sample", "jpeg");
        assertTrue(exported.getFileName().toString().endsWith(".jpg"));
    }

    @Test
    void rejectsUnsupportedFormat(@TempDir Path tempDir) {
        Path file = tempDir.resolve("chart.bmp");
        assertThrows(IllegalArgumentException.class,
                () -> exportService.writeChart(transparentImage(), file, "bmp"));
    }
}