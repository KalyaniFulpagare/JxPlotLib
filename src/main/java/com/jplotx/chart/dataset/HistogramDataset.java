package com.jplotx.chart.dataset;

import java.awt.Color;
import java.util.List;

public record HistogramDataset(
        String seriesName,
        List<HistogramBin> bins,
        Color color
) implements PlotDataset {

    @Override
    public boolean isEmpty() {
        return bins == null || bins.isEmpty();
    }
}
