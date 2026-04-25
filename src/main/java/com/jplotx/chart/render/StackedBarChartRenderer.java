package com.jplotx.chart.render;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.PlotContext;
import com.jplotx.chart.dataset.XYDataset;
import com.jplotx.chart.dataset.XYPoint;
import com.jplotx.chart.dataset.XYSeries;
import com.jplotx.chart.style.PlotOptions;
import com.jplotx.chart.style.PlotTheme;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class StackedBarChartRenderer implements ChartRenderer<XYDataset> {

    @Override
    public void render(Graphics2D g2, PlotContext context, ChartSpec spec, XYDataset dataset, PlotOptions options) {
        Rectangle area = context.plotArea();
        PlotTheme theme = options.theme();
        GraphicsSupport.enableQuality(g2);
        GraphicsSupport.paintBackground(g2, context.width(), context.height(), theme);
        GraphicsSupport.paintCard(g2, area, theme);
        GraphicsSupport.drawTitle(g2, spec.title(), context.width(), theme);

        List<XYSeries> seriesList = dataset.series();
        List<String> categories = dataset.categoryLabels().isEmpty()
                ? seriesList.get(0).points().stream().map(XYPoint::label).toList()
                : dataset.categoryLabels();
        double[] positiveTotals = new double[categories.size()];
        double[] negativeTotals = new double[categories.size()];
        for (XYSeries series : seriesList) {
            for (int i = 0; i < categories.size(); i++) {
                double value = series.points().get(i).y();
                if (value >= 0) {
                    positiveTotals[i] += value;
                } else {
                    negativeTotals[i] += value;
                }
            }
        }

        double minY = 0;
        double maxY = 0;
        for (int i = 0; i < categories.size(); i++) {
            minY = Math.min(minY, negativeTotals[i]);
            maxY = Math.max(maxY, positiveTotals[i]);
        }
        NumericScale yScale = NumericScale.of(minY, maxY, true, 6);

        GraphicsSupport.drawGrid(g2, area, fractions(yScale.ticks(), yScale.lowerBound(), yScale.upperBound()), List.of(), theme);
        GraphicsSupport.drawAxes(g2, area, spec.xLabel(), spec.yLabel(), theme);
        GraphicsSupport.drawYAxisTicks(g2, area, yScale, theme);
        GraphicsSupport.drawCategoryXAxisTicks(g2, area, categories, true, theme);

        double baseline = area.y + area.height - yScale.map(0, area.height);
        double slotWidth = area.width / (double) Math.max(1, categories.size());
        double barWidth = Math.max(18d, slotWidth * 0.68);

        List<GraphicsSupport.LegendEntry> legendEntries = new ArrayList<>();
        for (XYSeries series : seriesList) {
            legendEntries.add(new GraphicsSupport.LegendEntry(series.name(), series.color(), series.markerStyle()));
        }

        for (int categoryIndex = 0; categoryIndex < categories.size(); categoryIndex++) {
            double x = area.x + categoryIndex * slotWidth + (slotWidth - barWidth) / 2d;
            double positiveBase = baseline;
            double negativeBase = baseline;
            for (XYSeries series : seriesList) {
                double value = series.points().get(categoryIndex).y();
                double mapped = yScale.map(value, area.height);
                double height = Math.abs(mapped);
                double y;
                if (value >= 0) {
                    y = positiveBase - height;
                    positiveBase = y;
                } else {
                    y = negativeBase;
                    negativeBase += height;
                }
                g2.setColor(series.color());
                g2.fill(new RoundRectangle2D.Double(x, y, barWidth, height, 10, 10));
                g2.setColor(series.color().darker());
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(x, y, barWidth, height, 10, 10));
            }
        }

        if (options.legendVisible() && seriesList.size() > 1) {
            GraphicsSupport.drawLegend(g2, area, legendEntries, theme);
        }
    }

    private List<Double> fractions(List<Double> ticks, double min, double max) {
        List<Double> fractions = new ArrayList<>();
        for (double tick : ticks) {
            fractions.add((tick - min) / (max - min));
        }
        return fractions;
    }
}
