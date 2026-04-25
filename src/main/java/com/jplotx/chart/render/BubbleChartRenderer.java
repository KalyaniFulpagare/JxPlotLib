package com.jplotx.chart.render;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.PlotContext;
import com.jplotx.chart.dataset.BubbleDataset;
import com.jplotx.chart.dataset.BubblePoint;
import com.jplotx.chart.dataset.BubbleSeries;
import com.jplotx.chart.style.PlotOptions;
import com.jplotx.chart.style.PlotTheme;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public final class BubbleChartRenderer implements ChartRenderer<BubbleDataset> {

    @Override
    public void render(Graphics2D g2, PlotContext context, ChartSpec spec, BubbleDataset dataset, PlotOptions options) {
        Rectangle area = context.plotArea();
        PlotTheme theme = options.theme();
        GraphicsSupport.enableQuality(g2);
        GraphicsSupport.paintBackground(g2, context.width(), context.height(), theme);
        GraphicsSupport.paintCard(g2, area, theme);
        GraphicsSupport.drawTitle(g2, spec.title(), context.width(), theme);

        List<BubbleSeries> seriesList = dataset.series();
        NumericScale xScale = NumericScale.of(minX(seriesList), maxX(seriesList), false, 6);
        NumericScale yScale = NumericScale.of(minY(seriesList), maxY(seriesList), false, 6);
        double minSize = minSize(seriesList);
        double maxSize = maxSize(seriesList);

        GraphicsSupport.drawGrid(g2, area,
                fractions(yScale.ticks(), yScale.lowerBound(), yScale.upperBound()),
                fractions(xScale.ticks(), xScale.lowerBound(), xScale.upperBound()),
                theme);
        GraphicsSupport.drawAxes(g2, area, spec.xLabel(), spec.yLabel(), theme);
        GraphicsSupport.drawYAxisTicks(g2, area, yScale, theme);
        GraphicsSupport.drawNumericXAxisTicks(g2, area, xScale, theme);

        List<GraphicsSupport.LegendEntry> legendEntries = new ArrayList<>();
        for (BubbleSeries series : seriesList) {
            legendEntries.add(new GraphicsSupport.LegendEntry(series.name(), series.color(), series.markerStyle()));
            for (BubblePoint point : series.points()) {
                double x = area.x + xScale.map(point.x(), area.width);
                double y = area.y + area.height - yScale.map(point.y(), area.height);
                int bubbleSize = scaleBubble(point.size(), minSize, maxSize);
                Color fill = new Color(series.color().getRed(), series.color().getGreen(), series.color().getBlue(), 110);
                GraphicsSupport.drawMarker(g2, series.markerStyle(), x, y, bubbleSize, true, series.color(), fill);
                if (options.pointLabelsVisible() && point.label() != null && !point.label().isBlank()) {
                    g2.setColor(theme.textColor());
                    g2.drawString(point.label(), (int) x + bubbleSize / 2, (int) y - 6);
                }
            }
        }

        if (options.legendVisible() && seriesList.size() > 1) {
            GraphicsSupport.drawLegend(g2, area, legendEntries, theme);
        }
    }

    private int scaleBubble(double value, double min, double max) {
        if (max == min) {
            return 20;
        }
        double fraction = (value - min) / (max - min);
        return (int) Math.round(10 + fraction * 30);
    }

    private double minX(List<BubbleSeries> series) {
        double min = Double.POSITIVE_INFINITY;
        for (BubbleSeries item : series) {
            for (BubblePoint point : item.points()) {
                min = Math.min(min, point.x());
            }
        }
        return Double.isFinite(min) ? min : 0;
    }

    private double maxX(List<BubbleSeries> series) {
        double max = Double.NEGATIVE_INFINITY;
        for (BubbleSeries item : series) {
            for (BubblePoint point : item.points()) {
                max = Math.max(max, point.x());
            }
        }
        return Double.isFinite(max) ? max : 1;
    }

    private double minY(List<BubbleSeries> series) {
        double min = Double.POSITIVE_INFINITY;
        for (BubbleSeries item : series) {
            for (BubblePoint point : item.points()) {
                min = Math.min(min, point.y());
            }
        }
        return Double.isFinite(min) ? min : 0;
    }

    private double maxY(List<BubbleSeries> series) {
        double max = Double.NEGATIVE_INFINITY;
        for (BubbleSeries item : series) {
            for (BubblePoint point : item.points()) {
                max = Math.max(max, point.y());
            }
        }
        return Double.isFinite(max) ? max : 1;
    }

    private double minSize(List<BubbleSeries> series) {
        double min = Double.POSITIVE_INFINITY;
        for (BubbleSeries item : series) {
            for (BubblePoint point : item.points()) {
                min = Math.min(min, point.size());
            }
        }
        return Double.isFinite(min) ? min : 1;
    }

    private double maxSize(List<BubbleSeries> series) {
        double max = Double.NEGATIVE_INFINITY;
        for (BubbleSeries item : series) {
            for (BubblePoint point : item.points()) {
                max = Math.max(max, point.size());
            }
        }
        return Double.isFinite(max) ? max : 1;
    }

    private List<Double> fractions(List<Double> ticks, double min, double max) {
        List<Double> fractions = new ArrayList<>();
        for (double tick : ticks) {
            fractions.add((tick - min) / (max - min));
        }
        return fractions;
    }
}
