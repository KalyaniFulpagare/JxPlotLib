package com.jplotx.chart.dataset;

import com.jplotx.chart.style.MarkerStyle;

import java.awt.Color;
import java.util.List;

public record BubbleSeries(
        String name,
        List<BubblePoint> points,
        Color color,
        MarkerStyle markerStyle
) {

    public BubbleSeries {
        points = List.copyOf(points);
        if (markerStyle == null) {
            markerStyle = MarkerStyle.CIRCLE;
        }
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }
}
