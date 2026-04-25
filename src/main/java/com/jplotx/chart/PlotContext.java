package com.jplotx.chart;

import java.awt.Rectangle;

public record PlotContext(
        int width,
        int height,
        int leftMargin,
        int rightMargin,
        int topMargin,
        int bottomMargin
) {

    public Rectangle plotArea() {
        return new Rectangle(
                leftMargin,
                topMargin,
                Math.max(1, width - leftMargin - rightMargin),
                Math.max(1, height - topMargin - bottomMargin)
        );
    }
}
