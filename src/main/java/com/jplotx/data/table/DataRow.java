package com.jplotx.data.table;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class DataRow {

    private final Map<String, Object> values;

    public DataRow(Map<String, Object> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public Set<String> columns() {
        return values.keySet();
    }

    public Object get(String column) {
        ensureColumn(column);
        return values.get(column);
    }

    public String getString(String column) {
        Object value = get(column);
        return value == null ? null : String.valueOf(value);
    }

    public double getDouble(String column) {
        Object value = get(column);
        if (value == null) {
            throw new IllegalArgumentException("Column '" + column + "' contains null and cannot be used as a numeric value.");
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Column '" + column + "' value '" + value + "' is not numeric.", ex);
        }
    }

    public boolean hasColumn(String column) {
        return values.containsKey(column);
    }

    public Map<String, Object> asMap() {
        return Map.copyOf(values);
    }

    private void ensureColumn(String column) {
        if (!values.containsKey(column)) {
            throw new IllegalArgumentException("Column '" + column + "' does not exist. Available columns: " + values.keySet());
        }
    }
}
