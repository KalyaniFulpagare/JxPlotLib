package com.jplotx.chart.render;

import com.jplotx.chart.style.MarkerStyle;
import com.jplotx.chart.style.PlotTheme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

final class GraphicsSupport {

    private GraphicsSupport() {
    }

    static void enableQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    static void paintBackground(Graphics2D g2, int width, int height, PlotTheme theme) {
        g2.setPaint(new GradientPaint(0, 0, theme.backgroundStart(), width, height, theme.backgroundEnd()));
        g2.fillRect(0, 0, width, height);
    }

    static void paintCard(Graphics2D g2, Rectangle area, PlotTheme theme) {
        g2.setColor(theme.cardColor());
        g2.fill(new RoundRectangle2D.Double(area.x - 24, area.y - 20, area.width + 48, area.height + 50, 28, 28));
    }

    static void drawTitle(Graphics2D g2, String title, int width, PlotTheme theme) {
        Font previous = g2.getFont();
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        FontMetrics metrics = g2.getFontMetrics();
        int x = (width - metrics.stringWidth(title)) / 2;
        g2.setColor(theme.titleColor());
        g2.drawString(title, Math.max(20, x), 38);
        g2.setFont(previous);
    }

    static void drawAxes(Graphics2D g2, Rectangle area, String xLabel, String yLabel, PlotTheme theme) {
        g2.setColor(theme.axisColor());
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new Line2D.Double(area.x, area.y, area.x, area.y + area.height));
        g2.draw(new Line2D.Double(area.x, area.y + area.height, area.x + area.width, area.y + area.height));

        Font previous = g2.getFont();
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(xLabel, area.x + area.width / 2 - 20, area.y + area.height + 36);

        g2.rotate(-Math.PI / 2);
        g2.drawString(yLabel, -(area.y + area.height / 2 + metrics.stringWidth(yLabel) / 2), area.x - 52);
        g2.rotate(Math.PI / 2);
        g2.setFont(previous);
    }

    static void drawGrid(Graphics2D g2, Rectangle area, List<Double> yFractions, List<Double> xFractions, PlotTheme theme) {
        g2.setColor(theme.gridColor());
        g2.setStroke(new BasicStroke(1f));
        for (double fraction : yFractions) {
            double y = area.y + area.height - (area.height * fraction);
            g2.draw(new Line2D.Double(area.x, y, area.x + area.width, y));
        }
        for (double fraction : xFractions) {
            double x = area.x + (area.width * fraction);
            g2.draw(new Line2D.Double(x, area.y, x, area.y + area.height));
        }
    }

    static void drawYAxisTicks(Graphics2D g2, Rectangle area, NumericScale scale, PlotTheme theme) {
        Font previous = g2.getFont();
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(theme.axisColor());
        FontMetrics metrics = g2.getFontMetrics();

        for (double tick : scale.ticks()) {
            double fraction = scale.map(tick, area.height) / area.height;
            int y = (int) Math.round(area.y + area.height - (area.height * fraction));
            g2.draw(new Line2D.Double(area.x - 6, y, area.x, y));
            String label = scale.format(tick);
            g2.drawString(label, area.x - 14 - metrics.stringWidth(label), y + 4);
        }
        g2.setFont(previous);
    }

    static void drawNumericXAxisTicks(Graphics2D g2, Rectangle area, NumericScale scale, PlotTheme theme) {
        Font previous = g2.getFont();
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(theme.axisColor());
        FontMetrics metrics = g2.getFontMetrics();

        for (double tick : scale.ticks()) {
            double fraction = scale.map(tick, area.width) / area.width;
            int x = (int) Math.round(area.x + area.width * fraction);
            g2.draw(new Line2D.Double(x, area.y + area.height, x, area.y + area.height + 6));
            String label = scale.format(tick);
            g2.drawString(label, x - metrics.stringWidth(label) / 2, area.y + area.height + 22);
        }
        g2.setFont(previous);
    }

    static void drawCategoryXAxisTicks(Graphics2D g2, Rectangle area, List<String> labels, boolean centered, PlotTheme theme) {
        if (labels.isEmpty()) {
            return;
        }
        Font previous = g2.getFont();
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(theme.axisColor());
        FontMetrics metrics = g2.getFontMetrics();

        for (int i = 0; i < labels.size(); i++) {
            double fraction = categoryFraction(i, labels.size(), centered);
            int x = (int) Math.round(area.x + area.width * fraction);
            g2.draw(new Line2D.Double(x, area.y + area.height, x, area.y + area.height + 6));
            String label = labels.get(i);
            g2.drawString(label, x - metrics.stringWidth(label) / 2, area.y + area.height + 22);
        }
        g2.setFont(previous);
    }

    static void drawLegend(Graphics2D g2, Rectangle area, List<LegendEntry> entries, PlotTheme theme) {
        if (entries.isEmpty()) {
            return;
        }
        Font previous = g2.getFont();
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics metrics = g2.getFontMetrics();

        int maxWidth = 0;
        for (LegendEntry entry : entries) {
            maxWidth = Math.max(maxWidth, metrics.stringWidth(entry.label()));
        }
        int boxWidth = Math.max(120, maxWidth + 48);
        int boxHeight = entries.size() * 22 + 16;
        int x = area.x + area.width - boxWidth - 12;
        int y = area.y + 12;

        g2.setColor(theme.legendBackgroundColor());
        g2.fill(new RoundRectangle2D.Double(x, y, boxWidth, boxHeight, 16, 16));
        g2.setColor(theme.legendBorderColor());
        g2.draw(new RoundRectangle2D.Double(x, y, boxWidth, boxHeight, 16, 16));

        int currentY = y + 18;
        for (LegendEntry entry : entries) {
            drawMarker(g2, entry.markerStyle(), x + 14, currentY - 4, 10, true, entry.color(), entry.color());
            g2.setColor(theme.textColor());
            g2.drawString(entry.label(), x + 28, currentY);
            currentY += 22;
        }
        g2.setFont(previous);
    }

    static void drawMarker(Graphics2D g2, MarkerStyle markerStyle, double centerX, double centerY, int size, boolean fill, Color strokeColor, Color fillColor) {
        MarkerStyle style = markerStyle == null ? MarkerStyle.CIRCLE : markerStyle;
        if (style == MarkerStyle.NONE) {
            return;
        }

        double half = size / 2d;
        g2.setColor(fillColor);
        switch (style) {
            case CIRCLE -> {
                Ellipse2D.Double shape = new Ellipse2D.Double(centerX - half, centerY - half, size, size);
                if (fill) {
                    g2.fill(shape);
                }
                g2.setColor(strokeColor);
                g2.draw(shape);
            }
            case SQUARE -> {
                Rectangle2D.Double shape = new Rectangle2D.Double(centerX - half, centerY - half, size, size);
                if (fill) {
                    g2.fill(shape);
                }
                g2.setColor(strokeColor);
                g2.draw(shape);
            }
            case DIAMOND -> {
                Path2D.Double shape = new Path2D.Double();
                shape.moveTo(centerX, centerY - half);
                shape.lineTo(centerX + half, centerY);
                shape.lineTo(centerX, centerY + half);
                shape.lineTo(centerX - half, centerY);
                shape.closePath();
                if (fill) {
                    g2.fill(shape);
                }
                g2.setColor(strokeColor);
                g2.draw(shape);
            }
            case TRIANGLE -> {
                Path2D.Double shape = new Path2D.Double();
                shape.moveTo(centerX, centerY - half);
                shape.lineTo(centerX + half, centerY + half);
                shape.lineTo(centerX - half, centerY + half);
                shape.closePath();
                if (fill) {
                    g2.fill(shape);
                }
                g2.setColor(strokeColor);
                g2.draw(shape);
            }
            case CROSS -> {
                g2.setColor(strokeColor);
                g2.draw(new Line2D.Double(centerX - half, centerY - half, centerX + half, centerY + half));
                g2.draw(new Line2D.Double(centerX - half, centerY + half, centerX + half, centerY - half));
            }
            default -> {
            }
        }
    }

    static double categoryFraction(int index, int count, boolean centered) {
        if (count <= 1) {
            return 0.5;
        }
        if (centered) {
            return (index + 0.5) / count;
        }
        return (double) index / (count - 1);
    }

    record LegendEntry(String label, Color color, MarkerStyle markerStyle) {
    }
}
