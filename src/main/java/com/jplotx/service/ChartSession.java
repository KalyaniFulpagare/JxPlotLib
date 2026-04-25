package com.jplotx.service;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.dataset.PlotDataset;
import com.jplotx.chart.style.PlotOptions;

public record ChartSession(ChartSpec spec, PlotDataset dataset, PlotOptions options) {

    public ChartSession(ChartSpec spec, PlotDataset dataset) {
        this(spec, dataset, PlotOptions.defaults());
    }
}
