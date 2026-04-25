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

public final class BarChartRenderer implements ChartRenderer<XYDataset> {

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
        NumericScale yScale = NumericScale.of(minY(seriesList), maxY(seriesList), true, 6);

        GraphicsSupport.drawGrid(g2, area, fractions(yScale.ticks(), yScale.lowerBound(), yScale.upperBound()), List.of(), theme);
        GraphicsSupport.drawAxes(g2, area, spec.xLabel(), spec.yLabel(), theme);
        GraphicsSupport.drawYAxisTicks(g2, area, yScale, theme);
        GraphicsSupport.drawCategoryXAxisTicks(g2, area, categories, true, theme);

        double baseline = area.y + area.height - yScale.map(0, area.height);
        double slotWidth = area.width / (double) Math.max(1, categories.size());
        double groupWidth = slotWidth * 0.82;
        double barGap = Math.max(4d, groupWidth * 0.06);
        double barWidth = Math.max(12d, (groupWidth - barGap * (seriesList.size() - 1)) / Math.max(1, seriesList.size()));

        List<GraphicsSupport.LegendEntry> legendEntries = new ArrayList<>();
        for (int seriesIndex = 0; seriesIndex < seriesList.size(); seriesIndex++) {
            XYSeries series = seriesList.get(seriesIndex);
            legendEntries.add(new GraphicsSupport.LegendEntry(series.name(), series.color(), series.markerStyle()));

            for (int categoryIndex = 0; categoryIndex < categories.size(); categoryIndex++) {
                XYPoint point = series.points().get(categoryIndex);
                double groupStart = area.x + categoryIndex * slotWidth + (slotWidth - groupWidth) / 2d;
                double x = groupStart + seriesIndex * (barWidth + barGap);
                double valueY = area.y + area.height - yScale.map(point.y(), area.height);
                double y = Math.min(baseline, valueY);
                double height = Math.abs(baseline - valueY);

                g2.setColor(series.color());
                g2.fill(new RoundRectangle2D.Double(x, y, barWidth, height, 12, 12));
                g2.setColor(series.color().darker());
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(x, y, barWidth, height, 12, 12));

                if (options.valueLabelsVisible()) {
                    g2.setColor(theme.textColor());
                    g2.drawString(yScale.format(point.y()), (int) x, (int) y - 6);
                }
            }
        }

        if (options.legendVisible() && seriesList.size() > 1) {
            GraphicsSupport.drawLegend(g2, area, legendEntries, theme);
        }
    }

    private double minY(List<XYSeries> series) {
        double min = Double.POSITIVE_INFINITY;
        for (XYSeries item : series) {
            for (XYPoint point : item.points()) {
                min = Math.min(min, point.y());
            }
        }
        return Double.isFinite(min) ? min : 0;
    }

    private double maxY(List<XYSeries> series) {
        double max = Double.NEGATIVE_INFINITY;
        for (XYSeries item : series) {
            for (XYPoint point : item.points()) {
                max = Math.max(max, point.y());
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
