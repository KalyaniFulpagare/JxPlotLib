package com.jplotx.chart.dataset;

import java.util.List;

public record HeatmapDataset(
        List<String> rowLabels,
        List<String> columnLabels,
        double[][] values
) implements PlotDataset {

    @Override
    public boolean isEmpty() {
        return values == null || values.length == 0 || columnLabels == null || columnLabels.isEmpty();
    }
}
