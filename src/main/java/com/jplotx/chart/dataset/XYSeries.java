package com.jplotx.chart.dataset;

import com.jplotx.chart.style.MarkerStyle;

import java.awt.Color;
import java.util.List;

public record XYSeries(
        String name,
        List<XYPoint> points,
        Color color,
        MarkerStyle markerStyle,
        float strokeWidth,
        boolean markersVisible
) {

    public XYSeries {
        points = List.copyOf(points);
        if (markerStyle == null) {
            markerStyle = MarkerStyle.CIRCLE;
        }
        strokeWidth = Math.max(1f, strokeWidth);
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }
}
