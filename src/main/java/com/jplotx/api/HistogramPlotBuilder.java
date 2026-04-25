package com.jplotx.api;

import com.jplotx.JPlotX;
import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.ChartType;
import com.jplotx.chart.dataset.HistogramDataset;
import com.jplotx.data.table.DataTable;
import com.jplotx.service.ChartSession;
import com.jplotx.service.HistogramBuilder;

import java.util.ArrayList;
import java.util.List;

public final class HistogramPlotBuilder extends PlotBuilder<HistogramPlotBuilder> {

    private final HistogramBuilder histogramBuilder = new HistogramBuilder();
    private String seriesName = "Distribution";
    private List<Double> values = List.of();
    private int bins = 6;

    public HistogramPlotBuilder(JPlotX engine) {
        super(engine);
        this.yLabel = "Frequency";
    }

    public HistogramPlotBuilder seriesName(String seriesName) {
        this.seriesName = seriesName;
        return this;
    }

    public HistogramPlotBuilder values(List<Double> values) {
        this.values = new ArrayList<>(values);
        return this;
    }

    public HistogramPlotBuilder bins(int bins) {
        this.bins = bins;
        return this;
    }

    public HistogramPlotBuilder fromTable(DataTable table, String valueColumn) {
        if (table == null) {
            throw new IllegalArgumentException("Data table cannot be null.");
        }
        if (!table.hasColumn(valueColumn)) {
            throw new IllegalArgumentException("Value column '" + valueColumn + "' does not exist. Available columns: " + table.columns());
        }
        this.values = table.numericColumn(valueColumn);
        return this;
    }

    @Override
    protected ChartSession buildSession() {
        if (values == null || values.isEmpty()) {
            throw new IllegalStateException("No histogram values supplied.");
        }
        HistogramDataset dataset = histogramBuilder.build(values, bins, seriesName, color);
        return new ChartSession(new ChartSpec(title, xLabel, yLabel, ChartType.HISTOGRAM), dataset, buildOptions());
    }

    @Override
    protected HistogramPlotBuilder self() {
        return this;
    }
}
