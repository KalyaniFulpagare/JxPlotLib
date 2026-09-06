package com.jplotx.chart.render;

import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.PlotContext;
import com.jplotx.chart.dataset.PieDataset;
import com.jplotx.chart.dataset.PieSlice;
import com.jplotx.chart.style.MarkerStyle;
import com.jplotx.chart.style.PlotOptions;
import com.jplotx.chart.style.PlotTheme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;

public final class PieChartRenderer implements ChartRenderer<PieDataset> {

    @Override
    public void render(Graphics2D g2, PlotContext context, ChartSpec spec, PieDataset dataset, PlotOptions options) {
        Rectangle area = context.plotArea();
        PlotTheme theme = options.theme();
        GraphicsSupport.enableQuality(g2);
        GraphicsSupport.paintBackground(g2, context.width(), context.height(), theme);
        GraphicsSupport.paintCard(g2, area, theme);
        GraphicsSupport.drawTitle(g2, spec.title(), context.width(), theme);

        int margin = 40;
        int diameter = Math.min(area.width - margin * 2, area.height - margin);
        int pieX = area.x + (area.width - diameter) / 2;
        int pieY = area.y + Math.max(10, (area.height - diameter) / 2);

        // The legend is a small corner badge, not a reserved column, so we center
        // the pie in the full width above. Only if that centered position would
        // actually run into the legend's footprint do we nudge the pie left.
        if (options.legendVisible()) {
            int legendZoneLeft = area.x + area.width - 220;
            int pieRight = pieX + diameter;
            if (pieRight > legendZoneLeft) {
                pieX = Math.max(area.x + margin, legendZoneLeft - diameter - 12);
            }
        }

        double total = dataset.slices().stream().mapToDouble(PieSlice::value).sum();
        double startAngle = 90.0;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        List<GraphicsSupport.LegendEntry> legendEntries = new ArrayList<>();
        for (PieSlice slice : dataset.slices()) {
            legendEntries.add(new GraphicsSupport.LegendEntry(slice.label(), slice.color(), MarkerStyle.CIRCLE));
            double angle = total == 0 ? 0 : (slice.value() / total) * 360d;
            g2.setColor(slice.color());
            g2.fill(new Arc2D.Double(pieX, pieY, diameter, diameter, startAngle, -angle, Arc2D.PIE));
            g2.setColor(Color.WHITE);
            g2.draw(new Arc2D.Double(pieX, pieY, diameter, diameter, startAngle, -angle, Arc2D.PIE));

            if (options.valueLabelsVisible()) {
                double mid = Math.toRadians(startAngle - angle / 2d);
                int centerX = pieX + diameter / 2;
                int centerY = pieY + diameter / 2;
                int labelX = (int) Math.round(centerX + Math.cos(mid) * (diameter * 0.28));
                int labelY = (int) Math.round(centerY - Math.sin(mid) * (diameter * 0.28));
                String text = total == 0 ? "0%" : String.format("%.1f%%", (slice.value() / total) * 100d);
                g2.drawString(text, labelX - 12, labelY);
            }
            startAngle -= angle;
        }

        if (options.legendVisible()) {
            GraphicsSupport.drawLegend(g2, area, legendEntries, theme);
        }
    }
}
