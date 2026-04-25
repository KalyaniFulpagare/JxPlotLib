package com.jplotx.service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ExportService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public Path exportChart(BufferedImage image, Path outputDirectory, String filePrefix) throws IOException {
        Files.createDirectories(outputDirectory);
        String fileName = filePrefix + "-" + LocalDateTime.now().format(FORMATTER) + ".png";
        Path target = outputDirectory.resolve(fileName);
        return writeChart(image, target);
    }

    public Path writeChart(BufferedImage image, Path outputFile) throws IOException {
        Path parent = outputFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        ImageIO.write(image, "png", outputFile.toFile());
        return outputFile;
    }
}
