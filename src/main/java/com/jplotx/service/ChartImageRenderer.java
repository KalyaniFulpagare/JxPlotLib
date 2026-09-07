package com.jplotx.service;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.PlotContext;
import com.jplotx.chart.dataset.PlotDataset;
import com.jplotx.chart.render.ChartRenderer;
import com.jplotx.chart.render.RendererRegistry;

import org.freehep.graphicsio.svg.SVGGraphics2D;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ChartImageRenderer {

    private final RendererRegistry rendererRegistry = new RendererRegistry();

    public BufferedImage render(ChartSession chartSession, int width, int height) {
        if (chartSession == null) {
            throw new IllegalArgumentException("Chart session cannot be null.");
        }
        if (width < 320 || height < 240) {
            throw new IllegalArgumentException("Chart dimensions must be at least 320x240.");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            PlotContext context = new PlotContext(
                    width,
                    height,
                    chartSession.options().leftMargin(),
                    chartSession.options().rightMargin(),
                    chartSession.options().topMargin(),
                    chartSession.options().bottomMargin()
            );
            renderChart(g2, context, chartSession.spec(), chartSession.dataset(), chartSession.options());
            return image;
        } finally {
            g2.dispose();
        }
    }

    // Renders the same chart session as vector SVG instead of a raster image.
    // Since every JPlotX renderer draws exclusively through the standard
    // Graphics2D API, swapping in FreeHEP's SVGGraphics2D here is enough to
    // get vector output for all chart types with no renderer-specific code.
    public void renderSvg(ChartSession chartSession, int width, int height, Path outputFile) throws IOException {
        if (chartSession == null) {
            throw new IllegalArgumentException("Chart session cannot be null.");
        }
        if (width < 320 || height < 240) {
            throw new IllegalArgumentException("Chart dimensions must be at least 320x240.");
        }

        Path parent = outputFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        SVGGraphics2D g2 = new SVGGraphics2D(outputFile.toFile(), new Dimension(width, height));
        g2.startExport();
        try {
            PlotContext context = new PlotContext(
                    width,
                    height,
                    chartSession.options().leftMargin(),
                    chartSession.options().rightMargin(),
                    chartSession.options().topMargin(),
                    chartSession.options().bottomMargin()
            );
            renderChart(g2, context, chartSession.spec(), chartSession.dataset(), chartSession.options());
        } finally {
            g2.endExport();
            g2.dispose();
        }
    }

    private <T extends PlotDataset> void renderChart(Graphics2D g2, PlotContext context, ChartSpec spec, T dataset, com.jplotx.chart.style.PlotOptions options) {
        ChartRenderer<T> renderer = rendererRegistry.get(spec.chartType());
        renderer.render(g2, context, spec, dataset, options);
    }
}