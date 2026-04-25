package com.jplotx.data.table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
}
