package com.jplotx.chart.dataset;

public sealed interface PlotDataset permits XYDataset, HistogramDataset, HeatmapDataset, PieDataset, BubbleDataset {

    boolean isEmpty();
}
