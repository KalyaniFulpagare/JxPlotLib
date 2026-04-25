package com.jplotx.chart.render;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.PlotContext;
import com.jplotx.chart.dataset.HistogramBin;
import com.jplotx.chart.dataset.HistogramDataset;
import com.jplotx.chart.style.PlotOptions;
import com.jplotx.chart.style.PlotTheme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class HistogramChartRenderer implements ChartRenderer<HistogramDataset> {

    @Override
    public void render(Graphics2D g2, PlotContext context, ChartSpec spec, HistogramDataset dataset, PlotOptions options) {
        Rectangle area = context.plotArea();
        PlotTheme theme = options.theme();
        GraphicsSupport.enableQuality(g2);
        GraphicsSupport.paintBackground(g2, context.width(), context.height(), theme);
        GraphicsSupport.paintCard(g2, area, theme);
        GraphicsSupport.drawTitle(g2, spec.title(), context.width(), theme);

        List<HistogramBin> bins = dataset.bins();
        int minCount = bins.stream().mapToInt(HistogramBin::count).min().orElse(0);
        int maxCount = bins.stream().mapToInt(HistogramBin::count).max().orElse(1);
        double minX = bins.stream().mapToDouble(HistogramBin::start).min().orElse(0);
        double maxX = bins.stream().mapToDouble(HistogramBin::end).max().orElse(1);
        NumericScale xScale = NumericScale.of(minX, maxX, false, 6);
        NumericScale yScale = NumericScale.of(minCount, maxCount, true, 6);

        GraphicsSupport.drawGrid(g2, area,
                fractions(yScale.ticks(), yScale.lowerBound(), yScale.upperBound()),
                fractions(xScale.ticks(), xScale.lowerBound(), xScale.upperBound()),
                theme);
        GraphicsSupport.drawAxes(g2, area, spec.xLabel(), spec.yLabel(), theme);
        GraphicsSupport.drawYAxisTicks(g2, area, yScale, theme);
        GraphicsSupport.drawNumericXAxisTicks(g2, area, xScale, theme);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        for (int i = 0; i < bins.size(); i++) {
            HistogramBin bin = bins.get(i);
            int x = (int) Math.round(area.x + xScale.map(bin.start(), area.width));
            int nextX = (int) Math.round(area.x + xScale.map(bin.end(), area.width));
            int width = Math.max(8, nextX - x - 2);
            int height = (int) Math.round(yScale.map(bin.count(), area.height));
            int y = area.y + area.height - height;

            g2.setColor(dataset.color());
            g2.fill(new Rectangle2D.Double(x + 1, y, width, height));
            if (options.valueLabelsVisible()) {
                g2.setColor(theme.textColor());
                g2.drawString(String.valueOf(bin.count()), x + 2, y - 6);
            }
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
