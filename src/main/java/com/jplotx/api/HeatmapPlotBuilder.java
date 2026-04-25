package com.jplotx.api;

import com.jplotx.JPlotX;
import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.ChartType;
import com.jplotx.chart.dataset.HeatmapDataset;
import com.jplotx.data.table.DataRow;
import com.jplotx.data.table.DataTable;
import com.jplotx.service.ChartSession;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HeatmapPlotBuilder extends PlotBuilder<HeatmapPlotBuilder> {

    private List<String> rowLabels = List.of();
    private List<String> columnLabels = List.of();
    private double[][] values = new double[0][0];

    public HeatmapPlotBuilder(JPlotX engine) {
        super(engine);
    }

    public HeatmapPlotBuilder rows(List<String> rowLabels) {
        this.rowLabels = new ArrayList<>(rowLabels);
        return this;
    }

    public HeatmapPlotBuilder columns(List<String> columnLabels) {
        this.columnLabels = new ArrayList<>(columnLabels);
        return this;
    }

    public HeatmapPlotBuilder values(double[][] values) {
        this.values = values;
        return this;
    }

    public HeatmapPlotBuilder fromTable(DataTable table, String rowColumn, String columnColumn, String valueColumn) {
        if (table == null) {
            throw new IllegalArgumentException("Data table cannot be null.");
        }
        if (!table.hasColumn(rowColumn) || !table.hasColumn(columnColumn) || !table.hasColumn(valueColumn)) {
            throw new IllegalArgumentException("Requested heatmap columns must exist. Available columns: " + table.columns());
        }

        Map<String, Integer> rowIndex = new LinkedHashMap<>();
        Map<String, Integer> columnIndex = new LinkedHashMap<>();
        List<DataRow> rows = table.rows();
        for (DataRow row : rows) {
            rowIndex.computeIfAbsent(row.getString(rowColumn), key -> rowIndex.size());
            columnIndex.computeIfAbsent(row.getString(columnColumn), key -> columnIndex.size());
        }

        double[][] matrix = new double[rowIndex.size()][columnIndex.size()];
        for (DataRow row : rows) {
            matrix[rowIndex.get(row.getString(rowColumn))][columnIndex.get(row.getString(columnColumn))] = row.getDouble(valueColumn);
        }

        this.rowLabels = new ArrayList<>(rowIndex.keySet());
        this.columnLabels = new ArrayList<>(columnIndex.keySet());
        this.values = matrix;
        return this;
    }

    @Override
    protected ChartSession buildSession() {
        if (rowLabels.isEmpty() || columnLabels.isEmpty() || values.length == 0) {
            throw new IllegalStateException("Heatmap rows, columns, and values must all be provided.");
        }
        if (values.length != rowLabels.size()) {
            throw new IllegalStateException("Heatmap row count must match the value matrix height.");
        }
        for (double[] row : values) {
            if (row.length != columnLabels.size()) {
                throw new IllegalStateException("Each heatmap row must match the number of column labels.");
            }
        }
        HeatmapDataset dataset = new HeatmapDataset(List.copyOf(rowLabels), List.copyOf(columnLabels), values);
        return new ChartSession(new ChartSpec(title, xLabel, yLabel, ChartType.HEATMAP), dataset, buildOptions());
    }

    @Override
    protected HeatmapPlotBuilder self() {
        return this;
    }
}
