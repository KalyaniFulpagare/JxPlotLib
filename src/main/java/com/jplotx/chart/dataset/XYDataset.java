package com.jplotx.chart.dataset;

import java.util.List;

public record XYDataset(
        List<XYSeries> series,
        boolean useNumericX,
        List<String> categoryLabels
) implements PlotDataset {

    public XYDataset {
        series = List.copyOf(series);
        categoryLabels = categoryLabels == null ? List.of() : List.copyOf(categoryLabels);
    }

    @Override
    public boolean isEmpty() {
        return series.isEmpty() || series.stream().allMatch(XYSeries::isEmpty);
    }
}
