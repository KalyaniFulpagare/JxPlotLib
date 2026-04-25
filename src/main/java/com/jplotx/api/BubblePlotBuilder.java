package com.jplotx.api;

import com.jplotx.JPlotX;
import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.ChartType;
import com.jplotx.chart.dataset.BubbleDataset;
import com.jplotx.chart.dataset.BubblePoint;
import com.jplotx.chart.dataset.BubbleSeries;
import com.jplotx.chart.style.MarkerStyle;
import com.jplotx.data.table.DataRow;
import com.jplotx.data.table.DataTable;
import com.jplotx.service.ChartSession;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BubblePlotBuilder extends PlotBuilder<BubblePlotBuilder> {

    private final List<SeriesDefinition> seriesDefinitions = new ArrayList<>();

    public BubblePlotBuilder(JPlotX engine) {
        super(engine);
        this.legendVisible = true;
        this.fillMarkers = true;
    }

    public BubblePlotBuilder series(String name, List<Double> xValues, List<Double> yValues, List<Double> sizeValues) {
        return series(name, xValues, yValues, sizeValues, defaultLabels(xValues.size()), null, markerStyle);
    }

    public BubblePlotBuilder series(String name, List<Double> xValues, List<Double> yValues, List<Double> sizeValues, List<String> labels) {
        return series(name, xValues, yValues, sizeValues, labels, null, markerStyle);
    }

    public BubblePlotBuilder series(String name, List<Double> xValues, List<Double> yValues, List<Double> sizeValues, List<String> labels, Color seriesColor, MarkerStyle seriesMarker) {
        validateEqualSize(xValues, yValues, "xValues", "yValues");
        validateEqualSize(xValues, sizeValues, "xValues", "sizeValues");
        validateEqualSize(xValues, labels, "xValues", "labels");

        List<BubblePoint> points = new ArrayList<>();
        for (int i = 0; i < xValues.size(); i++) {
            points.add(new BubblePoint(labels.get(i), xValues.get(i), yValues.get(i), sizeValues.get(i)));
        }
        seriesDefinitions.add(new SeriesDefinition(name, points, seriesColor, seriesMarker));
        return this;
    }

    public BubblePlotBuilder fromTable(DataTable table, String xColumn, String yColumn, String sizeColumn) {
        return fromTable(table, xColumn, yColumn, sizeColumn, null, null);
    }

    public BubblePlotBuilder fromTable(DataTable table, String xColumn, String yColumn, String sizeColumn, String seriesColumn, String labelColumn) {
        if (table == null) {
            throw new IllegalArgumentException("Data table cannot be null.");
        }
        ensureColumns(table, xColumn, yColumn, sizeColumn);
        if (seriesColumn != null && !seriesColumn.isBlank() && !table.hasColumn(seriesColumn)) {
            throw new IllegalArgumentException("Series column '" + seriesColumn + "' does not exist. Available columns: " + table.columns());
        }
        if (labelColumn != null && !labelColumn.isBlank() && !table.hasColumn(labelColumn)) {
            throw new IllegalArgumentException("Label column '" + labelColumn + "' does not exist. Available columns: " + table.columns());
        }

        seriesDefinitions.clear();
        Map<String, List<DataRow>> groups = (seriesColumn == null || seriesColumn.isBlank())
                ? Map.of("Series 1", table.rows())
                : table.groupBy(seriesColumn);

        int colorIndex = 0;
        for (Map.Entry<String, List<DataRow>> entry : groups.entrySet()) {
            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();
            List<Double> sizeValues = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            for (DataRow row : entry.getValue()) {
                xValues.add(row.getDouble(xColumn));
                yValues.add(row.getDouble(yColumn));
                sizeValues.add(row.getDouble(sizeColumn));
                labels.add(labelColumn != null && !labelColumn.isBlank() ? row.getString(labelColumn) : entry.getKey());
            }
            series(entry.getKey(), xValues, yValues, sizeValues, labels, theme.paletteColor(colorIndex), markerStyle);
            colorIndex++;
        }
        return this;
    }

    @Override
    protected ChartSession buildSession() {
        if (seriesDefinitions.isEmpty()) {
            throw new IllegalStateException("No bubble series supplied.");
        }
        List<BubbleSeries> series = new ArrayList<>();
        for (int i = 0; i < seriesDefinitions.size(); i++) {
            SeriesDefinition definition = seriesDefinitions.get(i);
            Color resolvedColor = definition.color() != null ? definition.color() : theme.paletteColor(i);
            series.add(new BubbleSeries(definition.name(), definition.points(), resolvedColor, definition.markerStyle()));
        }
        return new ChartSession(new ChartSpec(title, xLabel, yLabel, ChartType.BUBBLE), new BubbleDataset(series), buildOptions());
    }

    @Override
    protected BubblePlotBuilder self() {
        return this;
    }

    private void ensureColumns(DataTable table, String... columns) {
        for (String column : columns) {
            if (!table.hasColumn(column)) {
                throw new IllegalArgumentException("Column '" + column + "' does not exist. Available columns: " + table.columns());
            }
        }
    }

    private List<String> defaultLabels(int size) {
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            labels.add("P" + (i + 1));
        }
        return labels;
    }

    private void validateEqualSize(List<?> left, List<?> right, String leftName, String rightName) {
        if (left == null || right == null || left.size() != right.size()) {
            throw new IllegalArgumentException(leftName + " and " + rightName + " must be non-null and the same size.");
        }
    }

    private record SeriesDefinition(String name, List<BubblePoint> points, Color color, MarkerStyle markerStyle) {
    }
}
