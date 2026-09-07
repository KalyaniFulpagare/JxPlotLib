package com.jplotx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SvgExportTest {

    @Test
    void writesValidSvgFile(@TempDir Path tempDir) throws IOException {
        Path svgFile = tempDir.resolve("chart.svg");

        JPlotX.line()
                .title("SVG Export Test")
                .values(List.of(1d, 2d, 3d, 4d), List.of(10d, 25d, 18d, 30d))
                .save(svgFile, "svg");

        assertTrue(Files.exists(svgFile));
        assertTrue(Files.size(svgFile) > 0);

        String content = Files.readString(svgFile);
        assertTrue(content.contains("<svg"));
        assertTrue(content.contains("</svg>"));
    }

    @Test
    void exportGeneratesTimestampedSvgFile(@TempDir Path tempDir) throws IOException {
        Path exported = JPlotX.bar()
                .title("SVG Export Bar Test")
                .values(List.of(1d, 2d, 3d), List.of(5d, 8d, 3d))
                .export(tempDir, "sample-bar", "svg");

        assertTrue(exported.getFileName().toString().endsWith(".svg"));
        assertTrue(Files.exists(exported));
        assertTrue(Files.size(exported) > 0);
    }
}