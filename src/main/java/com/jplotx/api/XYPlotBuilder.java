package com.jplotx.api;

import com.jplotx.JPlotX;
import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.ChartType;
import com.jplotx.chart.dataset.XYDataset;
import com.jplotx.chart.dataset.XYPoint;
import com.jplotx.chart.dataset.XYSeries;
import com.jplotx.chart.style.MarkerStyle;
import com.jplotx.data.table.DataRow;
import com.jplotx.data.table.DataTable;
import com.jplotx.service.ChartSession;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class XYPlotBuilder extends PlotBuilder<XYPlotBuilder> {

    private final ChartType chartType;
    private String seriesName = "Series 1";
    private final List<SeriesDefinition> seriesDefinitions = new ArrayList<>();
    private Boolean numericX;
    private List<String> categoryLabels = List.of();

    public XYPlotBuilder(JPlotX engine, ChartType chartType) {
        super(engine);
        this.chartType = chartType;
        if (chartType == ChartType.BAR) {
            this.valueLabelsVisible = true;
            this.fillMarkers = true;
        }
    }

    public XYPlotBuilder seriesName(String seriesName) {
        this.seriesName = seriesName;
        return this;
    }

    public XYPlotBuilder categories(List<String> labels, List<Double> yValues) {
        seriesDefinitions.clear();
        return addCategorySeries(seriesName, labels, yValues, color, markerStyle);
    }

    public XYPlotBuilder addCategorySeries(String name, List<String> labels, List<Double> yValues) {
        return addCategorySeries(name, labels, yValues, null, markerStyle);
    }

    public XYPlotBuilder addCategorySeries(String name, List<String> labels, List<Double> yValues, Color seriesColor, MarkerStyle seriesMarker) {
        validateEqualSize(labels, yValues, "labels", "yValues");
        ensureAxisMode(false);

        if (!categoryLabels.isEmpty() && !categoryLabels.equals(labels)) {
            throw new IllegalArgumentException("All category series must use the same labels and ordering.");
        }
        categoryLabels = List.copyOf(labels);

        List<XYPoint> points = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            points.add(new XYPoint(labels.get(i), i, yValues.get(i)));
        }
        seriesDefinitions.add(new SeriesDefinition(name, points, seriesColor, seriesMarker, strokeWidth, markersVisible));
        return this;
    }

    public XYPlotBuilder values(List<Double> xValues, List<Double> yValues) {
        seriesDefinitions.clear();
        return addSeries(seriesName, xValues, yValues, defaultLabels(xValues.size()), color, markerStyle);
    }

    public XYPlotBuilder values(List<Double> xValues, List<Double> yValues, List<String> labels) {
        seriesDefinitions.clear();
        return addSeries(seriesName, xValues, yValues, labels, color, markerStyle);
    }

    public XYPlotBuilder addSeries(String name, List<Double> xValues, List<Double> yValues) {
        return addSeries(name, xValues, yValues, defaultLabels(xValues.size()), null, markerStyle);
    }

    public XYPlotBuilder addSeries(String name, List<Double> xValues, List<Double> yValues, List<String> labels) {
        return addSeries(name, xValues, yValues, labels, null, markerStyle);
    }

    public XYPlotBuilder addSeries(String name, List<Double> xValues, List<Double> yValues, List<String> labels, Color seriesColor, MarkerStyle seriesMarker) {
        validateEqualSize(xValues, yValues, "xValues", "yValues");
        validateEqualSize(xValues, labels, "xValues", "labels");
        ensureAxisMode(true);

        List<XYPoint> points = new ArrayList<>();
        for (int i = 0; i < xValues.size(); i++) {
            points.add(new XYPoint(labels.get(i), xValues.get(i), yValues.get(i)));
        }
        seriesDefinitions.add(new SeriesDefinition(name, points, seriesColor, seriesMarker, strokeWidth, markersVisible));
        return this;
    }

    public XYPlotBuilder fromTable(DataTable table, String xColumn, String yColumn) {
        return fromTable(table, xColumn, yColumn, null, null);
    }

    public XYPlotBuilder fromTable(DataTable table, String xColumn, String yColumn, String seriesColumn) {
        return fromTable(table, xColumn, yColumn, seriesColumn, null);
    }

    public XYPlotBuilder fromTable(DataTable table, String xColumn, String yColumn, String seriesColumn, String labelColumn) {
        validateTable(table, xColumn, yColumn);
        seriesDefinitions.clear();

        boolean detectedNumericX = isNumericColumn(table, xColumn);
        ensureAxisMode(detectedNumericX);
        Map<String, List<DataRow>> groups = groupRows(table, seriesColumn);

        int colorIndex = 0;
        for (Map.Entry<String, List<DataRow>> entry : groups.entrySet()) {
            String name = entry.getKey();
            List<DataRow> rows = entry.getValue();
            if (detectedNumericX) {
                addNumericSeriesFromRows(name, rows, xColumn, yColumn, labelColumn, theme.paletteColor(colorIndex));
            } else {
                addCategorySeriesFromRows(name, rows, xColumn, yColumn, theme.paletteColor(colorIndex));
            }
            colorIndex++;
        }
        return this;
    }

    @Override
    protected ChartSession buildSession() {
        if (seriesDefinitions.isEmpty()) {
            throw new IllegalStateException("No data series supplied for the plot.");
        }

        List<XYSeries> series = new ArrayList<>();
        for (int i = 0; i < seriesDefinitions.size(); i++) {
            SeriesDefinition definition = seriesDefinitions.get(i);
            Color resolvedColor = definition.color() != null ? definition.color() : theme.paletteColor(i);
            series.add(new XYSeries(
                    definition.name(),
                    definition.points(),
                    resolvedColor,
                    definition.markerStyle(),
                    definition.strokeWidth(),
                    definition.markersVisible()
            ));
        }

        XYDataset dataset = new XYDataset(series, Boolean.TRUE.equals(numericX), categoryLabels);
        return new ChartSession(new ChartSpec(title, xLabel, yLabel, chartType), dataset, buildOptions());
    }

    @Override
    protected XYPlotBuilder self() {
        return this;
    }

    private void ensureAxisMode(boolean numeric) {
        if (numericX == null) {
            numericX = numeric;
            if (numeric) {
                categoryLabels = List.of();
            }
            return;
        }
        if (numericX != numeric) {
            throw new IllegalArgumentException("Cannot mix numeric and categorical series on the same plot.");
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

    private void validateTable(DataTable table, String xColumn, String yColumn) {
        if (table == null) {
            throw new IllegalArgumentException("Data table cannot be null.");
        }
        if (!table.hasColumn(xColumn)) {
            throw new IllegalArgumentException("X column '" + xColumn + "' does not exist. Available columns: " + table.columns());
        }
        if (!table.hasColumn(yColumn)) {
            throw new IllegalArgumentException("Y column '" + yColumn + "' does not exist. Available columns: " + table.columns());
        }
    }

    private boolean isNumericColumn(DataTable table, String column) {
        for (DataRow row : table.rows()) {
            Object value = row.get(column);
            if (value == null || String.valueOf(value).isBlank()) {
                continue;
            }
            if (value instanceof Number) {
                return true;
            }
            try {
                Double.parseDouble(String.valueOf(value));
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return false;
    }

    private Map<String, List<DataRow>> groupRows(DataTable table, String seriesColumn) {
        if (seriesColumn == null || seriesColumn.isBlank()) {
            return Map.of(seriesName, table.rows());
        }
        if (!table.hasColumn(seriesColumn)) {
            throw new IllegalArgumentException("Series column '" + seriesColumn + "' does not exist. Available columns: " + table.columns());
        }
        return table.groupBy(seriesColumn);
    }

    private void addNumericSeriesFromRows(String name, List<DataRow> rows, String xColumn, String yColumn, String labelColumn, Color seriesColor) {
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (DataRow row : rows) {
            xValues.add(row.getDouble(xColumn));
            yValues.add(row.getDouble(yColumn));
            labels.add(labelColumn != null && !labelColumn.isBlank() ? row.getString(labelColumn) : name);
        }
        addSeries(name, xValues, yValues, labels, seriesColor, markerStyle);
    }

    private void addCategorySeriesFromRows(String name, List<DataRow> rows, String xColumn, String yColumn, Color seriesColor) {
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (DataRow row : rows) {
            labels.add(row.getString(xColumn));
            values.add(row.getDouble(yColumn));
        }
        addCategorySeries(name, labels, values, seriesColor, markerStyle);
    }

    private record SeriesDefinition(
            String name,
            List<XYPoint> points,
            Color color,
            MarkerStyle markerStyle,
            float strokeWidth,
            boolean markersVisible
    ) {
    }
}
