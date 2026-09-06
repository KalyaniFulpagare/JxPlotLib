package com.jplotx.chart.render;

import java.util.ArrayList;
import java.util.List;

public final class NumericScale {

    private final double lowerBound;
    private final double upperBound;
    private final double tickSpacing;
    private final List<Double> ticks;

    private NumericScale(double lowerBound, double upperBound, double tickSpacing, List<Double> ticks) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.tickSpacing = tickSpacing;
        this.ticks = ticks;
    }

    // Like of(), but pads the max end of the range before computing "nice"
    // bounds, so data points don't render flush against the top of the plot
    // area (where a legend box commonly sits for XY-style charts).
    public static NumericScale ofWithTopHeadroom(double min, double max, boolean includeZero, int desiredTickCount, double headroomFraction) {
        double safeMin = Double.isFinite(min) ? min : 0;
        double safeMax = Double.isFinite(max) ? max : 1;
        double range = safeMax - safeMin;
        double paddedMax = safeMax + (range == 0 ? Math.abs(safeMax == 0 ? 1.0 : safeMax) * headroomFraction : range * headroomFraction);
        return of(min, paddedMax, includeZero, desiredTickCount);
    }

    public static NumericScale of(double min, double max, boolean includeZero, int desiredTickCount) {
        if (!Double.isFinite(min) || !Double.isFinite(max)) {
            min = 0;
            max = 1;
        }

        if (min == max) {
            double padding = min == 0 ? 1.0 : Math.abs(min) * 0.1;
            min -= padding;
            max += padding;
        }

        if (includeZero) {
            min = Math.min(min, 0);
            max = Math.max(max, 0);
        }

        double rawRange = max - min;
        double niceRange = niceNumber(rawRange, false);
        double spacing = niceNumber(niceRange / Math.max(2, desiredTickCount - 1), true);
        double niceMin = Math.floor(min / spacing) * spacing;
        double niceMax = Math.ceil(max / spacing) * spacing;

        List<Double> tickValues = new ArrayList<>();
        double value = niceMin;
        int guard = 0;
        while (value <= niceMax + (spacing * 0.5) && guard < 100) {
            tickValues.add(roundTo12Digits(value));
            value += spacing;
            guard++;
        }

        if (tickValues.size() < 2) {
            tickValues.add(niceMax);
        }

        return new NumericScale(niceMin, niceMax, spacing, tickValues);
    }

    public double lowerBound() {
        return lowerBound;
    }

    public double upperBound() {
        return upperBound;
    }

    public List<Double> ticks() {
        return ticks;
    }

    // Keeps markers/points off the plot edges by reserving a small pixel
    // margin on both ends of the scale, instead of mapping exactly to [0, pixels].
    private static final double EDGE_MARGIN_PX = 6.0;

    public double map(double value, int pixels) {
        double usablePixels = Math.max(1.0, pixels - 2 * EDGE_MARGIN_PX);
        return EDGE_MARGIN_PX + ((value - lowerBound) / (upperBound - lowerBound)) * usablePixels;
    }

    public String format(double value) {
        double abs = Math.abs(value);
        if (abs >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000d);
        }
        if (abs >= 1_000) {
            return String.format("%.1fk", value / 1_000d);
        }
        if (tickSpacing >= 1) {
            return String.format("%.0f", value);
        }
        if (tickSpacing >= 0.1) {
            return String.format("%.1f", value);
        }
        return String.format("%.2f", value);
    }

    private static double niceNumber(double range, boolean round) {
        double exponent = Math.floor(Math.log10(range));
        double fraction = range / Math.pow(10, exponent);
        double niceFraction;

        if (round) {
            if (fraction < 1.5) {
                niceFraction = 1;
            } else if (fraction < 3) {
                niceFraction = 2;
            } else if (fraction < 7) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (fraction <= 1) {
                niceFraction = 1;
            } else if (fraction <= 2) {
                niceFraction = 2;
            } else if (fraction <= 5) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    private static double roundTo12Digits(double value) {
        return Math.round(value * 1_000_000_000_000d) / 1_000_000_000_000d;
    }
}
