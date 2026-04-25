package com.jplotx.chart.style;

public record PlotOptions(
        PlotTheme theme,
        boolean legendVisible,
        boolean pointLabelsVisible,
        boolean valueLabelsVisible,
        boolean markersVisible,
        boolean fillMarkers,
        int markerSize,
        float strokeWidth,
        int leftMargin,
        int rightMargin,
        int topMargin,
        int bottomMargin
) {

    public PlotOptions {
        if (theme == null) {
            theme = PlotThemes.defaultTheme();
        }
        markerSize = Math.max(4, markerSize);
        strokeWidth = Math.max(1f, strokeWidth);
        leftMargin = Math.max(40, leftMargin);
        rightMargin = Math.max(30, rightMargin);
        topMargin = Math.max(40, topMargin);
        bottomMargin = Math.max(50, bottomMargin);
    }

    public static PlotOptions defaults() {
        return new PlotOptions(PlotThemes.defaultTheme(), true, false, false, true, false, 10, 2.5f, 110, 60, 80, 110);
    }
}
