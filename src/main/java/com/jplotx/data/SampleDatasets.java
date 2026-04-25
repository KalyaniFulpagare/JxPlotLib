package com.jplotx.data;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.ChartType;
import com.jplotx.chart.dataset.HeatmapDataset;
import com.jplotx.chart.dataset.XYDataset;
import com.jplotx.chart.dataset.XYPoint;
import com.jplotx.chart.dataset.XYSeries;
import com.jplotx.chart.style.MarkerStyle;
import com.jplotx.service.ChartSession;
import com.jplotx.service.HistogramBuilder;

import java.awt.Color;
import java.util.List;

public final class SampleDatasets {

    private final HistogramBuilder histogramBuilder = new HistogramBuilder();

    public ChartSession lineChart() {
        XYDataset dataset = new XYDataset(
                List.of(
                        new XYSeries(
                                "Quarterly Revenue",
                                List.of(
                                        new XYPoint("Q1", 1, 18),
                                        new XYPoint("Q2", 2, 26),
                                        new XYPoint("Q3", 3, 23),
                                        new XYPoint("Q4", 4, 34)
                                ),
                                new Color(32, 120, 199),
                                MarkerStyle.CIRCLE,
                                2.5f,
                                true
                        )
                ),
                true,
                List.of()
        );
        return new ChartSession(new ChartSpec("Revenue Trend", "Quarter", "Revenue (L)", ChartType.LINE), dataset);
    }

    public ChartSession barChart() {
        XYDataset dataset = new XYDataset(
                List.of(
                        new XYSeries(
                                "Region Sales",
                                List.of(
                                        new XYPoint("North", 0, 54),
                                        new XYPoint("South", 1, 42),
                                        new XYPoint("East", 2, 61),
                                        new XYPoint("West", 3, 48)
                                ),
                                new Color(72, 168, 104),
                                MarkerStyle.SQUARE,
                                2.5f,
                                false
                        )
                ),
                false,
                List.of("North", "South", "East", "West")
        );
        return new ChartSession(new ChartSpec("Regional Sales", "Region", "Sales", ChartType.BAR), dataset);
    }

    public ChartSession scatterChart() {
        XYDataset dataset = new XYDataset(
                List.of(
                        new XYSeries(
                                "Ad Spend vs Leads",
                                List.of(
                                        new XYPoint("A", 12, 21),
                                        new XYPoint("B", 18, 29),
                                        new XYPoint("C", 25, 38),
                                        new XYPoint("D", 31, 41),
                                        new XYPoint("E", 40, 56)
                                ),
                                new Color(220, 92, 51),
                                MarkerStyle.DIAMOND,
                                2.5f,
                                true
                        )
                ),
                true,
                List.of()
        );
        return new ChartSession(new ChartSpec("Spend vs Leads", "Ad Spend", "Leads", ChartType.SCATTER), dataset);
    }

    public ChartSession histogramChart() {
        return new ChartSession(
                new ChartSpec("Delivery Time Distribution", "Minutes", "Frequency", ChartType.HISTOGRAM),
                histogramBuilder.build(List.of(18d, 22d, 26d, 17d, 24d, 27d, 31d, 21d, 19d, 23d, 28d, 25d), 6, "Delivery Time", new Color(201, 119, 46))
        );
    }

    public ChartSession heatmapChart() {
        HeatmapDataset dataset = new HeatmapDataset(
                List.of("Jan", "Feb", "Mar", "Apr"),
                List.of("North", "South", "East", "West"),
                new double[][]{
                        {22, 28, 31, 17},
                        {26, 30, 34, 21},
                        {29, 35, 40, 24},
                        {32, 37, 43, 27}
                }
        );
        return new ChartSession(new ChartSpec("Monthly Regional Heatmap", "Region", "Month", ChartType.HEATMAP), dataset);
    }
}
