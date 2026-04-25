package com.jplotx.chart.render;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.PlotContext;
import com.jplotx.chart.dataset.PlotDataset;
import com.jplotx.chart.style.PlotOptions;

import java.awt.Graphics2D;

public interface ChartRenderer<T extends PlotDataset> {

    void render(Graphics2D graphics, PlotContext context, ChartSpec spec, T dataset, PlotOptions options);
}
