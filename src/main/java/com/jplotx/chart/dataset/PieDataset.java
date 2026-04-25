package com.jplotx.chart.dataset;

import java.util.List;

public record PieDataset(List<PieSlice> slices) implements PlotDataset {

    public PieDataset {
        slices = List.copyOf(slices);
    }

    @Override
    public boolean isEmpty() {
        return slices.isEmpty();
    }
}
