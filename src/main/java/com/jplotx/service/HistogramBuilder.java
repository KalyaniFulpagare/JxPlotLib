package com.jplotx.service;

import com.jplotx.chart.dataset.HistogramBin;
import com.jplotx.chart.dataset.HistogramDataset;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HistogramBuilder {

    public HistogramDataset build(List<Double> values, int binCount, String seriesName, Color color) {
        if (values == null || values.isEmpty()) {
            return new HistogramDataset(seriesName, List.of(), color);
        }

        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        double min = sorted.get(0);
        double max = sorted.get(sorted.size() - 1);
        double width = (max - min) / Math.max(1, binCount);
        if (width == 0) {
            width = 1;
        }

        List<HistogramBin> bins = new ArrayList<>();
        for (int i = 0; i < binCount; i++) {
            double start = min + i * width;
            double end = start + width;
            bins.add(new HistogramBin(start, end, 0));
        }

        int[] counts = new int[binCount];
        for (double value : sorted) {
            int index = (int) ((value - min) / width);
            if (index >= binCount) {
                index = binCount - 1;
            }
            counts[index]++;
        }

        List<HistogramBin> finalBins = new ArrayList<>();
        for (int i = 0; i < binCount; i++) {
            HistogramBin bin = bins.get(i);
            finalBins.add(new HistogramBin(bin.start(), bin.end(), counts[i]));
        }
        return new HistogramDataset(seriesName, finalBins, color);
    }
}
