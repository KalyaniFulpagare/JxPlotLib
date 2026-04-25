package com.jplotx.chart.render;

import com.jplotx.chart.ChartType;
import com.jplotx.chart.dataset.PlotDataset;

import java.util.EnumMap;
import java.util.Map;

public final class RendererRegistry {

    private final Map<ChartType, ChartRenderer<? extends PlotDataset>> renderers = new EnumMap<>(ChartType.class);

    public RendererRegistry() {
        renderers.put(ChartType.LINE, new LineChartRenderer());
        renderers.put(ChartType.AREA, new AreaChartRenderer());
        renderers.put(ChartType.BAR, new BarChartRenderer());
        renderers.put(ChartType.STACKED_BAR, new StackedBarChartRenderer());
        renderers.put(ChartType.SCATTER, new ScatterChartRenderer());
        renderers.put(ChartType.BUBBLE, new BubbleChartRenderer());
        renderers.put(ChartType.HISTOGRAM, new HistogramChartRenderer());
        renderers.put(ChartType.PIE, new PieChartRenderer());
        renderers.put(ChartType.HEATMAP, new HeatmapChartRenderer());
    }

    @SuppressWarnings("unchecked")
    public <T extends PlotDataset> ChartRenderer<T> get(ChartType chartType) {
        return (ChartRenderer<T>) renderers.get(chartType);
    }
}
