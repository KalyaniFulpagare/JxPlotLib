package com.jplotx.chart.style;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

public record PlotTheme(
        String name,
        Color backgroundStart,
        Color backgroundEnd,
        Color cardColor,
        Color titleColor,
        Color axisColor,
        Color gridColor,
        Color textColor,
        Color legendBackgroundColor,
        Color legendBorderColor,
        List<Color> palette,
        Color heatmapLowColor,
        Color heatmapHighColor
) {

    public PlotTheme {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(backgroundStart, "backgroundStart");
        Objects.requireNonNull(backgroundEnd, "backgroundEnd");
        Objects.requireNonNull(cardColor, "cardColor");
        Objects.requireNonNull(titleColor, "titleColor");
        Objects.requireNonNull(axisColor, "axisColor");
        Objects.requireNonNull(gridColor, "gridColor");
        Objects.requireNonNull(textColor, "textColor");
        Objects.requireNonNull(legendBackgroundColor, "legendBackgroundColor");
        Objects.requireNonNull(legendBorderColor, "legendBorderColor");
        Objects.requireNonNull(palette, "palette");
        Objects.requireNonNull(heatmapLowColor, "heatmapLowColor");
        Objects.requireNonNull(heatmapHighColor, "heatmapHighColor");
        palette = List.copyOf(palette);
        if (palette.isEmpty()) {
            throw new IllegalArgumentException("Palette cannot be empty.");
        }
    }

    public Color paletteColor(int index) {
        return palette.get(Math.floorMod(index, palette.size()));
    }

    public PlotTheme withPalette(List<Color> newPalette) {
        return new PlotTheme(
                name,
                backgroundStart,
                backgroundEnd,
                cardColor,
                titleColor,
                axisColor,
                gridColor,
                textColor,
                legendBackgroundColor,
                legendBorderColor,
                newPalette,
                heatmapLowColor,
                heatmapHighColor
        );
    }

    public PlotTheme withBackground(Color start, Color end) {
        return new PlotTheme(
                name,
                start,
                end,
                cardColor,
                titleColor,
                axisColor,
                gridColor,
                textColor,
                legendBackgroundColor,
                legendBorderColor,
                palette,
                heatmapLowColor,
                heatmapHighColor
        );
    }

    public PlotTheme withCardColor(Color newCardColor) {
        return new PlotTheme(
                name,
                backgroundStart,
                backgroundEnd,
                newCardColor,
                titleColor,
                axisColor,
                gridColor,
                textColor,
                legendBackgroundColor,
                legendBorderColor,
                palette,
                heatmapLowColor,
                heatmapHighColor
        );
    }

    public PlotTheme withAxisColors(Color newAxisColor, Color newGridColor, Color newTextColor) {
        return new PlotTheme(
                name,
                backgroundStart,
                backgroundEnd,
                cardColor,
                titleColor,
                newAxisColor,
                newGridColor,
                newTextColor,
                legendBackgroundColor,
                legendBorderColor,
                palette,
                heatmapLowColor,
                heatmapHighColor
        );
    }

    public PlotTheme withHeatmapColors(Color low, Color high) {
        return new PlotTheme(
                name,
                backgroundStart,
                backgroundEnd,
                cardColor,
                titleColor,
                axisColor,
                gridColor,
                textColor,
                legendBackgroundColor,
                legendBorderColor,
                palette,
                low,
                high
        );
    }
}
