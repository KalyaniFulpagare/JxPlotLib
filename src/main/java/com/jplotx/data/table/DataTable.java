package com.jplotx.data.table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DataTable {

    private final List<String> columns;
    private final List<DataRow> rows;

    public DataTable(List<String> columns, List<DataRow> rows) {
        this.columns = List.copyOf(columns);
        this.rows = List.copyOf(rows);
        validate();
    }

    public static DataTable ofRows(List<Map<String, Object>> rows) {
        Set<String> columns = new LinkedHashSet<>();
        List<DataRow> dataRows = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            columns.addAll(row.keySet());
            dataRows.add(new DataRow(row));
        }
        return new DataTable(new ArrayList<>(columns), dataRows);
    }

    public List<String> columns() {
        return columns;
    }

    public List<DataRow> rows() {
        return rows;
    }

    public int rowCount() {
        return rows.size();
    }

    public boolean hasColumn(String column) {
        return columns.contains(column);
    }

    public List<String> stringColumn(String column) {
        ensureColumn(column);
        List<String> values = new ArrayList<>();
        for (DataRow row : rows) {
            values.add(row.getString(column));
        }
        return values;
    }

    public List<Double> numericColumn(String column) {
        ensureColumn(column);
        List<Double> values = new ArrayList<>();
        for (DataRow row : rows) {
            values.add(row.getDouble(column));
        }
        return values;
    }

    public Map<String, List<DataRow>> groupBy(String column) {
        ensureColumn(column);
        Map<String, List<DataRow>> grouped = new LinkedHashMap<>();
        for (DataRow row : rows) {
            grouped.computeIfAbsent(row.getString(column), key -> new ArrayList<>()).add(row);
        }
        return grouped;
    }

    public DataTable aggregateBy(String groupColumn, String valueColumn, Aggregation aggregation) {
        return aggregateBy(List.of(groupColumn), valueColumn, aggregation, defaultResultColumn(valueColumn, aggregation));
    }

    public DataTable aggregateBy(String groupColumn, String valueColumn, Aggregation aggregation, String resultColumn) {
        return aggregateBy(List.of(groupColumn), valueColumn, aggregation, resultColumn);
    }

    public DataTable aggregateBy(List<String> groupColumns, String valueColumn, Aggregation aggregation) {
        return aggregateBy(groupColumns, valueColumn, aggregation, defaultResultColumn(valueColumn, aggregation));
    }

    public DataTable aggregateBy(List<String> groupColumns, String valueColumn, Aggregation aggregation, String resultColumn) {
        validateAggregation(groupColumns, valueColumn, aggregation, resultColumn);

        if (rows.isEmpty()) {
            List<String> aggregatedColumns = new ArrayList<>(groupColumns);
            aggregatedColumns.add(resultColumn);
            return new DataTable(aggregatedColumns, List.of());
        }

        Map<GroupKey, AggregationBucket> grouped = new LinkedHashMap<>();
        for (DataRow row : rows) {
            List<Object> groupValues = new ArrayList<>();
            for (String groupColumn : groupColumns) {
                groupValues.add(row.get(groupColumn));
            }
            GroupKey key = new GroupKey(groupValues);
            grouped.computeIfAbsent(key, ignored -> new AggregationBucket(groupValues)).accept(row, valueColumn, aggregation);
        }

        List<Map<String, Object>> aggregatedRows = new ArrayList<>();
        for (AggregationBucket bucket : grouped.values()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 0; i < groupColumns.size(); i++) {
                row.put(groupColumns.get(i), bucket.groupValues().get(i));
            }
            row.put(resultColumn, bucket.result(aggregation));
            aggregatedRows.add(row);
        }
        return DataTable.ofRows(aggregatedRows);
    }

    private void validate() {
        for (DataRow row : rows) {
            for (String column : columns) {
                if (!row.hasColumn(column)) {
                    throw new IllegalArgumentException("Every row must provide all declared columns. Missing column: " + column);
                }
            }
        }
    }

    private void ensureColumn(String column) {
        if (!columns.contains(column)) {
            throw new IllegalArgumentException("Column '" + column + "' does not exist. Available columns: " + columns);
        }
    }

    private void validateAggregation(List<String> groupColumns, String valueColumn, Aggregation aggregation, String resultColumn) {
        Objects.requireNonNull(groupColumns, "groupColumns");
        Objects.requireNonNull(aggregation, "aggregation");
        if (groupColumns.isEmpty()) {
            throw new IllegalArgumentException("At least one group column is required for aggregation.");
        }
        for (String groupColumn : groupColumns) {
            ensureColumn(groupColumn);
        }
        ensureColumn(valueColumn);
        if (resultColumn == null || resultColumn.isBlank()) {
            throw new IllegalArgumentException("Result column name cannot be blank.");
        }
        if (groupColumns.contains(resultColumn)) {
            throw new IllegalArgumentException("Result column name must be different from the group columns.");
        }
    }

    private String defaultResultColumn(String valueColumn, Aggregation aggregation) {
        return aggregation.name().toLowerCase() + "_" + valueColumn;
    }

    private record GroupKey(List<Object> values) {

        private GroupKey {
            values = List.copyOf(values);
        }
    }

    private static final class AggregationBucket {

        private final List<Object> groupValues;
        private double sum;
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;
        private int count;

        private AggregationBucket(List<Object> groupValues) {
            this.groupValues = List.copyOf(groupValues);
        }

        private List<Object> groupValues() {
            return groupValues;
        }

        private void accept(DataRow row, String valueColumn, Aggregation aggregation) {
            count++;
            if (aggregation == Aggregation.COUNT) {
                return;
            }

            double value = row.getDouble(valueColumn);
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        private Number result(Aggregation aggregation) {
            return switch (aggregation) {
                case SUM -> sum;
                case AVG -> count == 0 ? 0d : sum / count;
                case MIN -> count == 0 ? 0d : min;
                case MAX -> count == 0 ? 0d : max;
                case COUNT -> count;
            };
        }
    }
}
