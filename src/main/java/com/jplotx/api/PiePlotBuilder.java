package com.jplotx.api;

import com.jplotx.JPlotX;
import com.jplotx.chart.ChartSpec;
import com.jplotx.chart.ChartType;
import com.jplotx.chart.dataset.PieDataset;
import com.jplotx.chart.dataset.PieSlice;
import com.jplotx.data.table.DataTable;
import com.jplotx.service.ChartSession;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class PiePlotBuilder extends PlotBuilder<PiePlotBuilder> {

    private final List<SliceDefinition> sliceDefinitions = new ArrayList<>();

    public PiePlotBuilder(JPlotX engine) {
        super(engine);
        this.legendVisible = true;
        this.valueLabelsVisible = true;
    }

    public PiePlotBuilder slice(String label, double value) {
        return slice(label, value, null);
    }

    public PiePlotBuilder slice(String label, double value, Color color) {
        sliceDefinitions.add(new SliceDefinition(label, value, color));
        return this;
    }

    public PiePlotBuilder slices(List<String> labels, List<Double> values) {
        return slices(labels, values, List.of());
    }

    public PiePlotBuilder slices(List<String> labels, List<Double> values, List<Color> colors) {
        validateEqualSize(labels, values, "labels", "values");
        sliceDefinitions.clear();
        for (int i = 0; i < labels.size(); i++) {
            Color color = i < colors.size() ? colors.get(i) : null;
            sliceDefinitions.add(new SliceDefinition(labels.get(i), values.get(i), color));
        }
        return this;
    }

    public PiePlotBuilder fromTable(DataTable table, String labelColumn, String valueColumn) {
        if (table == null) {
            throw new IllegalArgumentException("Data table cannot be null.");
        }
        if (!table.hasColumn(labelColumn) || !table.hasColumn(valueColumn)) {
            throw new IllegalArgumentException("Requested pie columns must exist. Available columns: " + table.columns());
        }
        return slices(table.stringColumn(labelColumn), table.numericColumn(valueColumn));
    }

    @Override
    protected ChartSession buildSession() {
        if (sliceDefinitions.isEmpty()) {
            throw new IllegalStateException("No pie slices supplied.");
        }
        List<PieSlice> slices = new ArrayList<>();
        for (int i = 0; i < sliceDefinitions.size(); i++) {
            SliceDefinition definition = sliceDefinitions.get(i);
            Color resolvedColor = definition.color() != null ? definition.color() : theme.paletteColor(i);
            slices.add(new PieSlice(definition.label(), definition.value(), resolvedColor));
        }
        return new ChartSession(new ChartSpec(title, xLabel, yLabel, ChartType.PIE), new PieDataset(slices), buildOptions());
    }

    @Override
    protected PiePlotBuilder self() {
        return this;
    }

    private void validateEqualSize(List<?> left, List<?> right, String leftName, String rightName) {
        if (left == null || right == null || left.size() != right.size()) {
            throw new IllegalArgumentException(leftName + " and " + rightName + " must be non-null and the same size.");
        }
    }

    private record SliceDefinition(String label, double value, Color color) {
    }
}
