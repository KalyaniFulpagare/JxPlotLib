package com.jplotx.chart;

public record ChartSpec(
        String title,
        String xLabel,
        String yLabel,
        ChartType chartType
) {
}
