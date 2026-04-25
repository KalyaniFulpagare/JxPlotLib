package com.jplotx.data;

import com.jplotx.chart.dataset.BubbleDataset;
import com.jplotx.chart.dataset.BubblePoint;
import com.jplotx.chart.dataset.BubbleSeries;
import com.jplotx.chart.dataset.HeatmapDataset;
import com.jplotx.chart.dataset.HistogramDataset;
import com.jplotx.chart.dataset.PieDataset;
import com.jplotx.chart.dataset.PieSlice;
import com.jplotx.chart.dataset.PlotDataset;
import com.jplotx.chart.dataset.XYDataset;
import com.jplotx.chart.dataset.XYPoint;
import com.jplotx.chart.dataset.XYSeries;
import com.jplotx.chart.style.MarkerStyle;
import com.jplotx.data.table.DataRow;
import com.jplotx.data.table.DataTable;
import com.jplotx.service.ChartSession;
import com.jplotx.service.HistogramBuilder;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MySqlDataRepository {

    private final HistogramBuilder histogramBuilder = new HistogramBuilder();

    public PlotDataset load(DatabaseConfig config, MySqlChartRequest request) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(config.jdbcUrl(), config.username(), config.password());
             PreparedStatement statement = connection.prepareStatement(request.query());
            ResultSet resultSet = statement.executeQuery()) {

            return switch (request.chartType()) {
                case LINE, AREA, BAR, STACKED_BAR, SCATTER -> loadXYDataset(resultSet, request);
                case BUBBLE -> loadBubbleDataset(resultSet, request);
                case HISTOGRAM -> loadHistogramDataset(resultSet, request);
                case PIE -> loadPieDataset(resultSet, request);
                case HEATMAP -> loadHeatmapDataset(resultSet, request);
            };
        }
    }

    public DataTable loadTable(DatabaseConfig config, String query) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(config.jdbcUrl(), config.username(), config.password());
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            return toDataTable(resultSet);
        }
    }

    public ChartSession loadSession(DatabaseConfig config, MySqlChartRequest request, String title, String xLabel, String yLabel)
            throws SQLException, ClassNotFoundException {
        PlotDataset dataset = load(config, request);
        return new ChartSession(new com.jplotx.chart.ChartSpec(title, xLabel, yLabel, request.chartType()), dataset);
    }

    private XYDataset loadXYDataset(ResultSet resultSet, MySqlChartRequest request) throws SQLException {
        List<XYPoint> points = new ArrayList<>();
        boolean useNumericX = request.xColumn() != null && !request.xColumn().isBlank();
        List<String> categoryLabels = new ArrayList<>();
        int index = 0;
        while (resultSet.next()) {
            String label = readString(resultSet, request.labelColumn(), "P" + (index + 1));
            double x = useNumericX ? resultSet.getDouble(request.xColumn()) : index;
            double y = resultSet.getDouble(request.yColumn());
            points.add(new XYPoint(label, x, y));
            categoryLabels.add(label);
            index++;
        }
        XYSeries series = new XYSeries("MySQL Series", points, new Color(36, 122, 191), MarkerStyle.CIRCLE, 2.5f, true);
        return new XYDataset(List.of(series), useNumericX, useNumericX ? List.of() : categoryLabels);
    }

    private HistogramDataset loadHistogramDataset(ResultSet resultSet, MySqlChartRequest request) throws SQLException {
        List<Double> values = new ArrayList<>();
        while (resultSet.next()) {
            values.add(resultSet.getDouble(request.valueColumn()));
        }
        return histogramBuilder.build(values, Math.max(3, request.histogramBins()), "Distribution", new Color(211, 112, 62));
    }

    private PieDataset loadPieDataset(ResultSet resultSet, MySqlChartRequest request) throws SQLException {
        List<PieSlice> slices = new ArrayList<>();
        int index = 0;
        while (resultSet.next()) {
            String label = readString(resultSet, request.labelColumn(), "Slice " + (index + 1));
            double value = resultSet.getDouble(request.valueColumn());
            slices.add(new PieSlice(label, value, defaultPaletteColor(index)));
            index++;
        }
        return new PieDataset(slices);
    }

    private BubbleDataset loadBubbleDataset(ResultSet resultSet, MySqlChartRequest request) throws SQLException {
        List<BubblePoint> points = new ArrayList<>();
        int index = 0;
        while (resultSet.next()) {
            String label = readString(resultSet, request.labelColumn(), "P" + (index + 1));
            double x = resultSet.getDouble(request.xColumn());
            double y = resultSet.getDouble(request.yColumn());
            double size = resultSet.getDouble(request.valueColumn());
            points.add(new BubblePoint(label, x, y, size));
            index++;
        }
        BubbleSeries series = new BubbleSeries("MySQL Series", points, new Color(36, 122, 191), MarkerStyle.CIRCLE);
        return new BubbleDataset(List.of(series));
    }

    private HeatmapDataset loadHeatmapDataset(ResultSet resultSet, MySqlChartRequest request) throws SQLException {
        Map<String, Integer> rowIndex = new LinkedHashMap<>();
        Map<String, Integer> columnIndex = new LinkedHashMap<>();
        List<HeatmapRow> rows = new ArrayList<>();

        while (resultSet.next()) {
            String rowLabel = resultSet.getString(request.rowColumn());
            String columnLabel = resultSet.getString(request.columnColumn());
            double value = resultSet.getDouble(request.valueColumn());
            rowIndex.computeIfAbsent(rowLabel, key -> rowIndex.size());
            columnIndex.computeIfAbsent(columnLabel, key -> columnIndex.size());
            rows.add(new HeatmapRow(rowLabel, columnLabel, value));
        }

        double[][] matrix = new double[rowIndex.size()][columnIndex.size()];
        for (HeatmapRow row : rows) {
            matrix[rowIndex.get(row.rowLabel())][columnIndex.get(row.columnLabel())] = row.value();
        }

        return new HeatmapDataset(new ArrayList<>(rowIndex.keySet()), new ArrayList<>(columnIndex.keySet()), matrix);
    }

    private String readString(ResultSet resultSet, String column, String fallback) throws SQLException {
        if (column == null || column.isBlank()) {
            return fallback;
        }
        String value = resultSet.getString(column);
        return value == null || value.isBlank() ? fallback : value;
    }

    private record HeatmapRow(String rowLabel, String columnLabel, double value) {
    }

    private Color defaultPaletteColor(int index) {
        Color[] palette = new Color[]{
                new Color(44, 127, 184),
                new Color(240, 127, 90),
                new Color(72, 168, 104),
                new Color(158, 102, 204),
                new Color(220, 92, 51),
                new Color(33, 158, 188)
        };
        return palette[Math.floorMod(index, palette.length)];
    }

    private DataTable toDataTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> columns = new ArrayList<>();
        for (int index = 1; index <= metaData.getColumnCount(); index++) {
            columns.add(metaData.getColumnLabel(index));
        }

        List<DataRow> rows = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> values = new LinkedHashMap<>();
            for (String column : columns) {
                values.put(column, resultSet.getObject(column));
            }
            rows.add(new DataRow(values));
        }
        return new DataTable(columns, rows);
    }
}
