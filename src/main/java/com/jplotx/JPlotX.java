package com.jplotx;

import com.jplotx.api.BubblePlotBuilder;
import com.jplotx.api.HeatmapPlotBuilder;
import com.jplotx.api.HistogramPlotBuilder;
import com.jplotx.api.PiePlotBuilder;
import com.jplotx.api.XYPlotBuilder;
import com.jplotx.chart.ChartType;
import com.jplotx.data.table.CsvDataLoader;
import com.jplotx.data.table.DataTable;
import com.jplotx.preview.PlotPreviewer;
import com.jplotx.service.ChartImageRenderer;
import com.jplotx.service.ChartSession;
import com.jplotx.service.ExportService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public final class JPlotX {

    private static final JPlotX DEFAULT = new JPlotX();

    private final ChartImageRenderer imageRenderer;
    private final ExportService exportService;
    private final CsvDataLoader csvDataLoader;

    public JPlotX() {
        this.imageRenderer = new ChartImageRenderer();
        this.exportService = new ExportService();
        this.csvDataLoader = new CsvDataLoader();
    }

    public static XYPlotBuilder line() {
        return new XYPlotBuilder(DEFAULT, ChartType.LINE);
    }

    public static XYPlotBuilder area() {
        return new XYPlotBuilder(DEFAULT, ChartType.AREA);
    }

    public static XYPlotBuilder bar() {
        return new XYPlotBuilder(DEFAULT, ChartType.BAR);
    }

    public static XYPlotBuilder stackedBar() {
        return new XYPlotBuilder(DEFAULT, ChartType.STACKED_BAR);
    }

    public static XYPlotBuilder scatter() {
        return new XYPlotBuilder(DEFAULT, ChartType.SCATTER);
    }

    public static BubblePlotBuilder bubble() {
        return new BubblePlotBuilder(DEFAULT);
    }

    public static HistogramPlotBuilder histogram() {
        return new HistogramPlotBuilder(DEFAULT);
    }

    public static PiePlotBuilder pie() {
        return new PiePlotBuilder(DEFAULT);
    }

    public static HeatmapPlotBuilder heatmap() {
        return new HeatmapPlotBuilder(DEFAULT);
    }

    public DataTable loadCsv(Path csvPath) throws IOException {
        return csvDataLoader.load(csvPath);
    }

    public DataTable loadCsv(Path csvPath, char delimiter) throws IOException {
        return csvDataLoader.load(csvPath, delimiter);
    }

    public BufferedImage render(ChartSession chartSession, int width, int height) {
        return imageRenderer.render(chartSession, width, height);
    }

    public Path write(ChartSession chartSession, int width, int height, Path outputFile) throws IOException {
        BufferedImage image = render(chartSession, width, height);
        return exportService.writeChart(image, outputFile);
    }

    public Path export(ChartSession chartSession, int width, int height, Path outputDirectory, String filePrefix) throws IOException {
        BufferedImage image = render(chartSession, width, height);
        return exportService.exportChart(image, outputDirectory, filePrefix);
    }

    public void show(ChartSession chartSession, int width, int height) {
        BufferedImage image = render(chartSession, width, height);
        PlotPreviewer.show(image, chartSession.spec().title());
    }
}
