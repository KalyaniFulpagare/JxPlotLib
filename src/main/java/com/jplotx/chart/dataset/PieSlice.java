package com.jplotx.chart.dataset;

import java.awt.Color;

public record PieSlice(
        String label,
        double value,
        Color color
) {
}
