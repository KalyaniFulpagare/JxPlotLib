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

    public T title(String title) {
        this.title = title;
        return self();
    }

    public T xLabel(String xLabel) {
        this.xLabel = xLabel;
        return self();
    }

    public T yLabel(String yLabel) {
        this.yLabel = yLabel;
        return self();
    }

    public T color(Color color) {
        this.color = color;
        return self();
    }

    public T theme(PlotTheme theme) {
        this.theme = theme;
        return self();
    }

    public T legend(boolean legendVisible) {
        this.legendVisible = legendVisible;
        return self();
    }

    public T pointLabels(boolean pointLabelsVisible) {
        this.pointLabelsVisible = pointLabelsVisible;
        return self();
    }

    public T valueLabels(boolean valueLabelsVisible) {
        this.valueLabelsVisible = valueLabelsVisible;
        return self();
    }

    public T markers(boolean markersVisible) {
        this.markersVisible = markersVisible;
        return self();
    }

    public T fillMarkers(boolean fillMarkers) {
        this.fillMarkers = fillMarkers;
        return self();
    }

    public T markerSize(int markerSize) {
        this.markerSize = markerSize;
        return self();
    }

    public T markerStyle(MarkerStyle markerStyle) {
        this.markerStyle = markerStyle;
        return self();
    }

    public T strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        return self();
    }

    public T size(int width, int height) {
        this.width = width;
        this.height = height;
        return self();
    }

    public T margins(int left, int right, int top, int bottom) {
        this.leftMargin = left;
        this.rightMargin = right;
        this.topMargin = top;
        this.bottomMargin = bottom;
        return self();
    }

    public BufferedImage render() {
        return engine.render(buildSession(), width, height);
    }

    public Path save(Path outputFile) throws IOException {
        return engine.write(buildSession(), width, height, outputFile);
    }

    public Path save(Path outputFile, String format) throws IOException {
        return engine.write(buildSession(), width, height, outputFile, format);
    }

    public Path export(Path outputDirectory, String filePrefix) throws IOException {
        return engine.export(buildSession(), width, height, outputDirectory, filePrefix);
    }

    public Path export(Path outputDirectory, String filePrefix, String format) throws IOException {
        return engine.export(buildSession(), width, height, outputDirectory, filePrefix, format);
    }
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
