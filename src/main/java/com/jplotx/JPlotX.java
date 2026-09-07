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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entry point for JPlotX. Use the static factory methods (e.g. {@link #line()},
 * {@link #bar()}, {@link #pie()}) to start building a chart with a fluent,
 * chainable API, and use the instance methods to load data or render/export
 * a chart directly from an already-built {@link ChartSession}.
 *
 * <p>Typical usage:
 * <pre>{@code
 * JPlotX.line()
 *     .title("Revenue Trend")
 *     .values(List.of(1d, 2d, 3d), List.of(10d, 25d, 18d))
 *     .export(Path.of("exports"), "revenue-trend");
 * }</pre>
 *
 * <p>Charts can be exported as PNG, JPEG, or SVG (vector) by passing a format
 * string to {@link #write} / {@link #export} (or the corresponding
 * {@code PlotBuilder.save}/{@code export} overloads). SVG export is powered by
 * <a href="https://github.com/freehep/freehep-vectorgraphics">FreeHEP VectorGraphics</a>
 * (LGPL) via its {@code SVGGraphics2D}, which every JPlotX renderer draws
 * into transparently since they only use the standard {@code Graphics2D} API.
 */
public final class JPlotX {

    private static final JPlotX DEFAULT = new JPlotX();
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final ChartImageRenderer imageRenderer;
    private final ExportService exportService;
    private final CsvDataLoader csvDataLoader;

    public JPlotX() {
        this.imageRenderer = new ChartImageRenderer();
        this.exportService = new ExportService();
        this.csvDataLoader = new CsvDataLoader();
    }

    /** Starts building a line chart. */
    public static XYPlotBuilder line() {
        return new XYPlotBuilder(DEFAULT, ChartType.LINE);
    }

    /** Starts building a filled area chart. */
    public static XYPlotBuilder area() {
        return new XYPlotBuilder(DEFAULT, ChartType.AREA);
    }

    /** Starts building a (grouped) bar chart. */
    public static XYPlotBuilder bar() {
        return new XYPlotBuilder(DEFAULT, ChartType.BAR);
    }

    /** Starts building a stacked bar chart. */
    public static XYPlotBuilder stackedBar() {
        return new XYPlotBuilder(DEFAULT, ChartType.STACKED_BAR);
    }

    /** Starts building a scatter chart, optionally with a fitted trend line. */
    public static XYPlotBuilder scatter() {
        return new XYPlotBuilder(DEFAULT, ChartType.SCATTER);
    }

    /** Starts building a bubble chart, where a third numeric value controls point size. */
    public static BubblePlotBuilder bubble() {
        return new BubblePlotBuilder(DEFAULT);
    }

    /** Starts building a histogram over a single numeric column. */
    public static HistogramPlotBuilder histogram() {
        return new HistogramPlotBuilder(DEFAULT);
    }

    /** Starts building a pie chart. */
    public static PiePlotBuilder pie() {
        return new PiePlotBuilder(DEFAULT);
    }

    /** Starts building a heatmap over a (row, column, value) dataset. */
    public static HeatmapPlotBuilder heatmap() {
        return new HeatmapPlotBuilder(DEFAULT);
    }

    /** Loads a comma-delimited CSV file into a {@link DataTable}. */
    public DataTable loadCsv(Path csvPath) throws IOException {
        return csvDataLoader.load(csvPath);
    }

    /** Loads a delimited CSV/TSV file into a {@link DataTable}, using the given field delimiter. */
    public DataTable loadCsv(Path csvPath, char delimiter) throws IOException {
        return csvDataLoader.load(csvPath, delimiter);
    }

    /** Renders a chart session to an in-memory image without writing it to disk. */
    public BufferedImage render(ChartSession chartSession, int width, int height) {
        return imageRenderer.render(chartSession, width, height);
    }

    /** Renders a chart session and writes it to {@code outputFile} as a PNG. */
    public Path write(ChartSession chartSession, int width, int height, Path outputFile) throws IOException {
        BufferedImage image = render(chartSession, width, height);
        return exportService.writeChart(image, outputFile);
    }

    /**
     * Renders a chart session and writes it to {@code outputFile} in the given format.
     *
     * @param format "png", "jpg"/"jpeg", or "svg" (case-insensitive). SVG output is
     *               vector and re-renders the chart directly rather than converting
     *               an existing raster image.
     */
    public Path write(ChartSession chartSession, int width, int height, Path outputFile, String format) throws IOException {
        if (isSvg(format)) {
            imageRenderer.renderSvg(chartSession, width, height, outputFile);
            return outputFile;
        }
        BufferedImage image = render(chartSession, width, height);
        return exportService.writeChart(image, outputFile, format);
    }

    /**
     * Renders a chart session and writes it as a PNG into {@code outputDirectory},
     * generating a timestamped filename of the form {@code <filePrefix>-<yyyyMMdd-HHmmss>.png}.
     */
    public Path export(ChartSession chartSession, int width, int height, Path outputDirectory, String filePrefix) throws IOException {
        BufferedImage image = render(chartSession, width, height);
        return exportService.exportChart(image, outputDirectory, filePrefix);
    }

    /**
     * Renders a chart session and writes it into {@code outputDirectory} with a
     * timestamped filename, in the given format.
     *
     * @param format "png", "jpg"/"jpeg", or "svg" (case-insensitive)
     */
    public Path export(ChartSession chartSession, int width, int height, Path outputDirectory, String filePrefix, String format) throws IOException {
        if (isSvg(format)) {
            Files.createDirectories(outputDirectory);
            String fileName = filePrefix + "-" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".svg";
            Path outputFile = outputDirectory.resolve(fileName);
            imageRenderer.renderSvg(chartSession, width, height, outputFile);
            return outputFile;
        }
        BufferedImage image = render(chartSession, width, height);
        return exportService.exportChart(image, outputDirectory, filePrefix, format);
    }

    /** Renders a chart session and opens it in a preview window. */
    public void show(ChartSession chartSession, int width, int height) {
        BufferedImage image = render(chartSession, width, height);
        PlotPreviewer.show(image, chartSession.spec().title());
    }

    private static boolean isSvg(String format) {
        return format != null && "svg".equalsIgnoreCase(format.trim());
    }
}