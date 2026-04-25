package com.jplotx.chart.render;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.PlotContext;
import com.jplotx.chart.dataset.HeatmapDataset;
import com.jplotx.chart.style.PlotOptions;
import com.jplotx.chart.style.PlotTheme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

public final class HeatmapChartRenderer implements ChartRenderer<HeatmapDataset> {

    @Override
    public void render(Graphics2D g2, PlotContext context, ChartSpec spec, HeatmapDataset dataset, PlotOptions options) {
        Rectangle area = context.plotArea();
        PlotTheme theme = options.theme();
        GraphicsSupport.enableQuality(g2);
        GraphicsSupport.paintBackground(g2, context.width(), context.height(), theme);
        GraphicsSupport.paintCard(g2, area, theme);
        GraphicsSupport.drawTitle(g2, spec.title(), context.width(), theme);
        GraphicsSupport.drawAxes(g2, area, spec.xLabel(), spec.yLabel(), theme);

        int rows = dataset.rowLabels().size();
        int cols = dataset.columnLabels().size();
        int cellWidth = area.width / Math.max(1, cols);
        int cellHeight = area.height / Math.max(1, rows);

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (double[] row : dataset.values()) {
            for (double value : row) {
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
        }
        if (min == max) {
            max = min + 1;
        }

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics metrics = g2.getFontMetrics();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double value = dataset.values()[row][col];
                float ratio = (float) ((value - min) / (max - min));
                Color color = blend(theme.heatmapLowColor(), theme.heatmapHighColor(), ratio);
                int x = area.x + col * cellWidth;
                int y = area.y + row * cellHeight;
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Double(x + 2, y + 2, Math.max(8, cellWidth - 4), Math.max(8, cellHeight - 4), 12, 12));
                if (options.valueLabelsVisible()) {
                    g2.setColor(Color.WHITE);
                    String label = String.format("%.1f", value);
                    g2.drawString(label, x + (cellWidth - metrics.stringWidth(label)) / 2, y + cellHeight / 2);
                }
            }
        }

        g2.setColor(theme.textColor());
        for (int col = 0; col < cols; col++) {
            String label = dataset.columnLabels().get(col);
            int x = area.x + col * cellWidth + cellWidth / 2 - metrics.stringWidth(label) / 2;
            g2.drawString(label, x, area.y + area.height + 22);
        }
        for (int row = 0; row < rows; row++) {
            int y = area.y + row * cellHeight + cellHeight / 2;
            g2.drawString(dataset.rowLabels().get(row), area.x - 58, y);
        }
    }

    private Color blend(Color low, Color high, float ratio) {
        float bounded = Math.max(0f, Math.min(1f, ratio));
        int red = (int) (low.getRed() + (high.getRed() - low.getRed()) * bounded);
        int green = (int) (low.getGreen() + (high.getGreen() - low.getGreen()) * bounded);
        int blue = (int) (low.getBlue() + (high.getBlue() - low.getBlue()) * bounded);
        return new Color(red, green, blue);
    }
}
