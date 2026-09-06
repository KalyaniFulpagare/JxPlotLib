package com.jplotx.data.table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An immutable, in-memory table of rows sharing a fixed set of columns.
 *
 * <p>Every row must provide a value for every declared column (see
 * {@link #DataTable(List, List)}); use {@link #ofRows(List)} to build a
 * table from loosely-typed maps without worrying about column ordering
 * or gaps, since it infers the column list from the union of all row keys.
 *
 * <p>{@link #aggregateBy} is the main way to reshape data for charting:
 * it groups rows by one or more columns and reduces a numeric column with
 * SUM, AVG, MIN, MAX, or COUNT, returning a new {@code DataTable} with one
 * row per group.
 */
public final class DataTable {

    private final List<String> columns;
    private final List<DataRow> rows;

    /**
     * Creates a table from an explicit column list and rows.
     *
     * @throws IllegalArgumentException if any row is missing a declared column
     */
    public DataTable(List<String> columns, List<DataRow> rows) {
        this.columns = List.copyOf(columns);
        this.rows = List.copyOf(rows);
        validate();
    }

    /**
     * Builds a table from a list of maps, inferring the column list from the
     * union of all keys across all rows (in first-seen order). Rows may omit
     * keys that other rows provide; missing values simply won't exist for
     * that row/column pair.
     */
    public static DataTable ofRows(List<Map<String, Object>> rows) {
        Set<String> columns = new LinkedHashSet<>();
        List<DataRow> dataRows = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            columns.addAll(row.keySet());
            dataRows.add(new DataRow(row));
        }
        return new DataTable(new ArrayList<>(columns), dataRows);
    }

    /** Returns the table's column names, in declaration order. */
    public List<String> columns() {
        return columns;
    }

    /** Returns all rows, in insertion order. */
    public List<DataRow> rows() {
        return rows;
    }

    /** Returns the number of rows in the table. */
    public int rowCount() {
        return rows.size();
    }

    /** Returns whether the table declares the given column. */
    public boolean hasColumn(String column) {
        return columns.contains(column);
    }

    /**
     * Returns the given column's values, in row order, coerced to strings.
     *
     * @throws IllegalArgumentException if the column doesn't exist
     */
    public List<String> stringColumn(String column) {
        ensureColumn(column);
        List<String> values = new ArrayList<>();
        for (DataRow row : rows) {
            values.add(row.getString(column));
        }
        return values;
    }

    /**
     * Returns the given column's values, in row order, coerced to doubles.
     *
     * @throws IllegalArgumentException if the column doesn't exist
     */
    public List<Double> numericColumn(String column) {
        ensureColumn(column);
        List<Double> values = new ArrayList<>();
        for (DataRow row : rows) {
            values.add(row.getDouble(column));
        }
        return values;
    }

    /**
     * Groups rows by the string value of {@code column}, preserving each
     * group's first-seen order.
     *
     * @throws IllegalArgumentException if the column doesn't exist
     */
    public Map<String, List<DataRow>> groupBy(String column) {
        ensureColumn(column);
        Map<String, List<DataRow>> grouped = new LinkedHashMap<>();
        for (DataRow row : rows) {
            grouped.computeIfAbsent(row.getString(column), key -> new ArrayList<>()).add(row);
        }
        return grouped;
    }

    /**
     * Groups rows by {@code groupColumn} and reduces {@code valueColumn} with
     * {@code aggregation}. The result column is named
     * {@code "<aggregation>_<valueColumn>"} (e.g. {@code "sum_sales"}).
     */
    public DataTable aggregateBy(String groupColumn, String valueColumn, Aggregation aggregation) {
        return aggregateBy(List.of(groupColumn), valueColumn, aggregation, defaultResultColumn(valueColumn, aggregation));
    }

    /** Like {@link #aggregateBy(String, String, Aggregation)}, but with a custom result column name. */
    public DataTable aggregateBy(String groupColumn, String valueColumn, Aggregation aggregation, String resultColumn) {
        return aggregateBy(List.of(groupColumn), valueColumn, aggregation, resultColumn);
    }

    /** Like {@link #aggregateBy(String, String, Aggregation)}, but grouping by multiple columns at once. */
    public DataTable aggregateBy(List<String> groupColumns, String valueColumn, Aggregation aggregation) {
        return aggregateBy(groupColumns, valueColumn, aggregation, defaultResultColumn(valueColumn, aggregation));
    }

    /**
     * Groups rows by {@code groupColumns} (in the order given) and reduces
     * {@code valueColumn} with {@code aggregation}, returning one row per
     * distinct combination of group values plus the aggregated result under
     * {@code resultColumn}. Groups are returned in first-seen order.
     *
     * @throws IllegalArgumentException if {@code groupColumns} is empty, any
     *         column doesn't exist, {@code resultColumn} is blank, or
     *         {@code resultColumn} clashes with a group column name
     */
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