package com.jplotx.service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;

public final class ExportService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Set<String> SUPPORTED_FORMATS = Set.of("png", "jpg", "jpeg");

    public Path exportChart(BufferedImage image, Path outputDirectory, String filePrefix) throws IOException {
        return exportChart(image, outputDirectory, filePrefix, "png");
    }

    public Path exportChart(BufferedImage image, Path outputDirectory, String filePrefix, String format) throws IOException {
        String normalized = normalizeFormat(format);
        Files.createDirectories(outputDirectory);
        String extension = normalized.equals("jpeg") ? "jpg" : normalized;
        String fileName = filePrefix + "-" + LocalDateTime.now().format(FORMATTER) + "." + extension;
        Path target = outputDirectory.resolve(fileName);
        return writeChart(image, target, normalized);
    }

    public Path writeChart(BufferedImage image, Path outputFile) throws IOException {
        return writeChart(image, outputFile, "png");
    }

    public Path writeChart(BufferedImage image, Path outputFile, String format) throws IOException {
        String normalized = normalizeFormat(format);
        Path parent = outputFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        BufferedImage toWrite = normalized.equals("png") ? image : flattenToOpaque(image);
        String imageIoFormat = normalized.equals("jpeg") ? "jpg" : normalized;
        ImageIO.write(toWrite, imageIoFormat, outputFile.toFile());
        return outputFile;
    }

    private String normalizeFormat(String format) {
        if (format == null || format.isBlank()) {
            return "png";
        }
        String normalized = format.trim().toLowerCase(Locale.ROOT);
        if (!SUPPORTED_FORMATS.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Unsupported export format '" + format + "'. Supported formats: " + SUPPORTED_FORMATS);
        }
        return normalized;
    }

    // JPEG has no alpha channel. Painting the chart onto an opaque white
    // background before encoding avoids ImageIO rejecting the ARGB source
    // and avoids the transparent areas turning solid black in the output.
    private BufferedImage flattenToOpaque(BufferedImage source) {
        BufferedImage opaque = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = opaque.createGraphics();
        try {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, source.getWidth(), source.getHeight());
            g2.drawImage(source, 0, 0, null);
        } finally {
            g2.dispose();
        }
        return opaque;
    }
}