package com.jplotx.chart.render;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.PlotContext;
import com.jplotx.chart.dataset.XYDataset;
import com.jplotx.chart.dataset.XYPoint;
import com.jplotx.chart.dataset.XYSeries;
import com.jplotx.chart.style.PlotOptions;
import com.jplotx.chart.style.PlotTheme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public final class LineChartRenderer implements ChartRenderer<XYDataset> {

    @Override
    public void render(Graphics2D g2, PlotContext context, ChartSpec spec, XYDataset dataset, PlotOptions options) {
        Rectangle area = context.plotArea();
        PlotTheme theme = options.theme();
        GraphicsSupport.enableQuality(g2);
        GraphicsSupport.paintBackground(g2, context.width(), context.height(), theme);
        GraphicsSupport.paintCard(g2, area, theme);
        GraphicsSupport.drawTitle(g2, spec.title(), context.width(), theme);

        List<XYSeries> seriesList = dataset.series();
        NumericScale yScale = NumericScale.ofWithTopHeadroom(minY(seriesList), maxY(seriesList), false, 6, 0.20);
        NumericScale xScale = dataset.useNumericX() ? NumericScale.of(minX(seriesList), maxX(seriesList), false, 6) : null;
        List<String> categories = categoryLabels(dataset, seriesList);

        GraphicsSupport.drawGrid(
                g2,
                area,
                fractions(yScale.ticks(), yScale.lowerBound(), yScale.upperBound()),
                dataset.useNumericX() ? fractions(xScale.ticks(), xScale.lowerBound(), xScale.upperBound()) : categoryFractions(categories.size(), false),
                theme
        );
        GraphicsSupport.drawAxes(g2, area, spec.xLabel(), spec.yLabel(), theme);
        GraphicsSupport.drawYAxisTicks(g2, area, yScale, theme);
        if (dataset.useNumericX()) {
            GraphicsSupport.drawNumericXAxisTicks(g2, area, xScale, theme);
        } else {
            GraphicsSupport.drawCategoryXAxisTicks(g2, area, categories, false, theme);
        }

        List<GraphicsSupport.LegendEntry> legendEntries = new ArrayList<>();
        List<GraphicsSupport.InfoEntry> infoEntries = new ArrayList<>();
        for (XYSeries series : seriesList) {
            legendEntries.add(new GraphicsSupport.LegendEntry(series.name(), series.color(), series.markerStyle()));
            g2.setColor(series.color());
            g2.setStroke(new BasicStroke(series.strokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < series.points().size() - 1; i++) {
                XYPoint a = series.points().get(i);
                XYPoint b = series.points().get(i + 1);
                double ax = area.x + mapX(i, a, dataset, xScale, categories.size(), area.width, false);
                double ay = area.y + area.height - yScale.map(a.y(), area.height);
                double bx = area.x + mapX(i + 1, b, dataset, xScale, categories.size(), area.width, false);
                double by = area.y + area.height - yScale.map(b.y(), area.height);
                g2.draw(new Line2D.Double(ax, ay, bx, by));
            }

            if (options.trendLineVisible() && dataset.useNumericX()) {
                RegressionSupport.TrendLine trendLine = RegressionSupport.fit(series.points());
                if (trendLine != null) {
                    Stroke previousStroke = g2.getStroke();
                    Color trendColor = translucent(series.color(), 190);
                    double startX = minX(series);
                    double endX = maxX(series);
                    double startY = trendLine.yAt(startX);
                    double endY = trendLine.yAt(endX);
                    g2.setColor(trendColor);
                    g2.setStroke(new BasicStroke(
                            Math.max(1.5f, series.strokeWidth()),
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND,
                            10f,
                            new float[]{10f, 8f},
                            0f
                    ));
                    g2.draw(new Line2D.Double(
                            area.x + xScale.map(startX, area.width),
                            area.y + area.height - yScale.map(startY, area.height),
                            area.x + xScale.map(endX, area.width),
                            area.y + area.height - yScale.map(endY, area.height)
                    ));
                    g2.setStroke(previousStroke);
                    infoEntries.add(new GraphicsSupport.InfoEntry(series.name() + ": " + trendLine.summary(), trendColor));
                }
            }

            if (options.markersVisible() && series.markersVisible()) {
                for (int i = 0; i < series.points().size(); i++) {
                    XYPoint point = series.points().get(i);
                    double x = area.x + mapX(i, point, dataset, xScale, categories.size(), area.width, false);
                    double y = area.y + area.height - yScale.map(point.y(), area.height);
                    GraphicsSupport.drawMarker(
                            g2,
                            series.markerStyle(),
                            x,
                            y,
                            options.markerSize(),
                            options.fillMarkers(),
                            series.color(),
                            options.fillMarkers() ? series.color() : Color.WHITE
                    );
                    if (options.pointLabelsVisible() && point.label() != null && !point.label().isBlank()) {
                        g2.setColor(theme.textColor());
                        g2.drawString(point.label(), (int) x + 6, (int) y - 8);
                    }
                }
            }
        }

        if (options.legendVisible() && seriesList.size() > 1) {
            GraphicsSupport.drawLegend(g2, area, legendEntries, theme);
        }
        if (!infoEntries.isEmpty()) {
            GraphicsSupport.drawInfoBox(g2, area, infoEntries, theme);
        }
    }

    private double minX(List<XYSeries> series) {
        double min = Double.POSITIVE_INFINITY;
        for (XYSeries item : series) {
            for (XYPoint point : item.points()) {
                min = Math.min(min, point.x());
            }
        }
        return Double.isFinite(min) ? min : 0;
    }

    private double minX(XYSeries series) {
        double min = Double.POSITIVE_INFINITY;
        for (XYPoint point : series.points()) {
            min = Math.min(min, point.x());
        }
        return Double.isFinite(min) ? min : 0;
    }

    private double maxX(List<XYSeries> series) {
        double max = Double.NEGATIVE_INFINITY;
        for (XYSeries item : series) {
            for (XYPoint point : item.points()) {
                max = Math.max(max, point.x());
            }
        }
        return Double.isFinite(max) ? max : 1;
    }

    private double maxX(XYSeries series) {
        double max = Double.NEGATIVE_INFINITY;
        for (XYPoint point : series.points()) {
            max = Math.max(max, point.x());
        }
        return Double.isFinite(max) ? max : 1;
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

    private List<String> categoryLabels(XYDataset dataset, List<XYSeries> series) {
        if (!dataset.categoryLabels().isEmpty()) {
            return dataset.categoryLabels();
        }
        return series.isEmpty() ? List.of() : series.get(0).points().stream().map(XYPoint::label).toList();
    }

    private double mapX(int index, XYPoint point, XYDataset dataset, NumericScale xScale, int categoryCount, int width, boolean centered) {
        if (dataset.useNumericX()) {
            return xScale.map(point.x(), width);
        }
        return width * GraphicsSupport.categoryFraction(index, categoryCount, centered);
    }

    private List<Double> fractions(List<Double> ticks, double min, double max) {
        List<Double> fractions = new ArrayList<>();
        for (double tick : ticks) {
            fractions.add((tick - min) / (max - min));
        }
        return fractions;
    }

    private List<Double> categoryFractions(int size, boolean centered) {
        List<Double> fractions = new ArrayList<>();
        for (int i = 0; i < Math.max(1, size); i++) {
            fractions.add(GraphicsSupport.categoryFraction(i, Math.max(1, size), centered));
        }
        return fractions;
    }

    private Color translucent(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, alpha)));
    }
}
