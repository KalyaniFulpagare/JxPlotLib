package com.jplotx.data;

import com.jplotx.chart.ChartType;

public record MySqlChartRequest(
        ChartType chartType,
        String query,
        String labelColumn,
        String xColumn,
        String yColumn,
        String rowColumn,
        String columnColumn,
        String valueColumn,
        int histogramBins
) {
}
