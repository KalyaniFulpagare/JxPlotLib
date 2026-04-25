package com.jplotx.chart.dataset;

import java.util.List;

public record BubbleDataset(List<BubbleSeries> series) implements PlotDataset {

    public BubbleDataset {
        series = List.copyOf(series);
    }

    @Override
    public boolean isEmpty() {
        return series.isEmpty() || series.stream().allMatch(BubbleSeries::isEmpty);
    }
}
