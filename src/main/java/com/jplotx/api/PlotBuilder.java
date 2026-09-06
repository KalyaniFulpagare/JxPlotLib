package com.jplotx.api;

import com.jplotx.JPlotX;
import com.jplotx.chart.style.MarkerStyle;
import com.jplotx.chart.style.PlotOptions;
import com.jplotx.chart.style.PlotTheme;
import com.jplotx.chart.style.PlotThemes;
import com.jplotx.service.ChartSession;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Base class for JPlotX's fluent chart builders (e.g. the builders returned
 * by {@link JPlotX#line()}, {@link JPlotX#bar()}, {@link JPlotX#pie()}).
 *
 * <p>Each concrete subclass configures a specific dataset shape and chart
 * type, but all of them share this common set of styling, sizing, and
 * export options. Every setter returns {@code T} (the concrete builder
 * type) so calls can be chained, e.g.:
 *
 * <pre>{@code
 * JPlotX.line()
 *     .title("Revenue Trend")
 *     .xLabel("Quarter")
 *     .yLabel("Revenue")
 *     .values(quarters, revenue)
 *     .export(Path.of("exports"), "revenue-trend");
 * }</pre>
 *
 * @param <T> the concrete builder subtype, used so chained calls keep the
 *            subclass's own type instead of widening to {@code PlotBuilder}
 */
public abstract class PlotBuilder<T extends PlotBuilder<T>> {

    protected final JPlotX engine;
    protected String title = "JPlotX Chart";
    protected String xLabel = "X Axis";
    protected String yLabel = "Y Axis";
    protected Color color = new Color(44, 127, 184);
    protected PlotTheme theme = PlotThemes.defaultTheme();
    protected int width = 1280;
    protected int height = 720;
    protected boolean legendVisible = true;
    protected boolean pointLabelsVisible = false;
    protected boolean valueLabelsVisible = false;
    protected boolean markersVisible = true;
    protected boolean fillMarkers = false;
    protected int markerSize = 10;
    protected float strokeWidth = 2.5f;
    protected MarkerStyle markerStyle = MarkerStyle.CIRCLE;
    protected int leftMargin = 110;
    protected int rightMargin = 60;
    protected int topMargin = 80;
    protected int bottomMargin = 110;

    protected PlotBuilder(JPlotX engine) {
        this.engine = engine;
    }

    /** Sets the chart's title, shown centered above the plot area. */
    public T title(String title) {
        this.title = title;
        return self();
    }

    /** Sets the label drawn under the x-axis. */
    public T xLabel(String xLabel) {
        this.xLabel = xLabel;
        return self();
    }

    /** Sets the label drawn beside the y-axis. */
    public T yLabel(String yLabel) {
        this.yLabel = yLabel;
        return self();
    }

    /** Sets the primary series color used where the chart type doesn't derive colors from the theme. */
    public T color(Color color) {
        this.color = color;
        return self();
    }

    /** Sets the color theme (palette, background, and text colors) applied to the chart. */
    public T theme(PlotTheme theme) {
        this.theme = theme;
        return self();
    }

    /** Shows or hides the legend box. Defaults to visible. */
    public T legend(boolean legendVisible) {
        this.legendVisible = legendVisible;
        return self();
    }

    /** Shows or hides labels drawn next to individual data points. Defaults to hidden. */
    public T pointLabels(boolean pointLabelsVisible) {
        this.pointLabelsVisible = pointLabelsVisible;
        return self();
    }

    /** Shows or hides value labels (e.g. bar heights, pie percentages). Defaults to hidden. */
    public T valueLabels(boolean valueLabelsVisible) {
        this.valueLabelsVisible = valueLabelsVisible;
        return self();
    }

    /** Shows or hides point markers on line/scatter-style charts. Defaults to visible. */
    public T markers(boolean markersVisible) {
        this.markersVisible = markersVisible;
        return self();
    }

    /** Sets whether markers are drawn filled (solid) or as outlines. Defaults to outlined. */
    public T fillMarkers(boolean fillMarkers) {
        this.fillMarkers = fillMarkers;
        return self();
    }

    /** Sets the marker diameter, in pixels. Defaults to 10. */
    public T markerSize(int markerSize) {
        this.markerSize = markerSize;
        return self();
    }

    /** Sets the marker shape (circle, square, diamond, triangle, cross, or none). */
    public T markerStyle(MarkerStyle markerStyle) {
        this.markerStyle = markerStyle;
        return self();
    }

    /** Sets the line/border stroke width, in pixels. Defaults to 2.5. */
    public T strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        return self();
    }

    /** Sets the output image size, in pixels. Defaults to 1280x720. */
    public T size(int width, int height) {
        this.width = width;
        this.height = height;
        return self();
    }

    /** Sets the margins reserved around the plot area for axes, labels, and title, in pixels. */
    public T margins(int left, int right, int top, int bottom) {
        this.leftMargin = left;
        this.rightMargin = right;
        this.topMargin = top;
        this.bottomMargin = bottom;
        return self();
    }

    /** Renders the chart in memory without writing it to disk. */
    public BufferedImage render() {
        return engine.render(buildSession(), width, height);
    }

    /** Renders the chart and writes it to {@code outputFile} as a PNG, inferring nothing from the file extension. */
    public Path save(Path outputFile) throws IOException {
        return engine.write(buildSession(), width, height, outputFile);
    }

    /**
     * Renders the chart and writes it to {@code outputFile} in the given format.
     *
     * @param format "png", "jpg", or "jpeg" (case-insensitive). JPEG output has no
     *               alpha channel, so any transparency is flattened onto a white background.
     */
    public Path save(Path outputFile, String format) throws IOException {
        return engine.write(buildSession(), width, height, outputFile, format);
    }

    /**
     * Renders the chart and writes it as a PNG into {@code outputDirectory}, generating
     * a timestamped filename of the form {@code <filePrefix>-<yyyyMMdd-HHmmss>.png}.
     */
    public Path export(Path outputDirectory, String filePrefix) throws IOException {
        return engine.export(buildSession(), width, height, outputDirectory, filePrefix);
    }

    /**
     * Renders the chart and writes it into {@code outputDirectory} with a timestamped
     * filename, in the given format.
     *
     * @param format "png", "jpg", or "jpeg" (case-insensitive)
     */
    public Path export(Path outputDirectory, String filePrefix, String format) throws IOException {
        return engine.export(buildSession(), width, height, outputDirectory, filePrefix, format);
    }

    /** Renders the chart and opens it in a preview window. */
    public void show() {
        engine.show(buildSession(), width, height);
    }

    protected PlotOptions buildOptions() {
        return new PlotOptions(
                theme,
                legendVisible,
                pointLabelsVisible,
                valueLabelsVisible,
                markersVisible,
                fillMarkers,
                markerSize,
                strokeWidth,
                false,
                leftMargin,
                rightMargin,
                topMargin,
                bottomMargin
        );
    }

    protected abstract ChartSession buildSession();

    protected abstract T self();
}