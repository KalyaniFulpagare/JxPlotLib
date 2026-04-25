package com.jplotx.data.table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CsvDataLoader {

    public DataTable load(Path csvPath) throws IOException {
        return load(csvPath, ',');
    }

    public DataTable load(Path csvPath, char delimiter) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        if (lines.isEmpty()) {
            return new DataTable(List.of(), List.of());
        }

        List<String> headers = splitLine(lines.get(0), delimiter);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            List<String> values = splitLine(lines.get(i), delimiter);
            Map<String, Object> row = new LinkedHashMap<>();
            for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                String raw = columnIndex < values.size() ? values.get(columnIndex) : "";
                row.put(headers.get(columnIndex), parseValue(raw));
            }
            rows.add(row);
        }
        if (rows.isEmpty()) {
            return new DataTable(headers, List.of());
        }
        return DataTable.ofRows(rows);
    }

    private List<String> splitLine(String line, char delimiter) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == delimiter && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString().trim());
        return values;
    }

    private Object parseValue(String raw) {
        String cleaned = raw == null ? "" : raw.trim();
        if (cleaned.isEmpty()) {
            return "";
        }
        try {
            if (cleaned.contains(".")) {
                return Double.parseDouble(cleaned);
            }
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException ignored) {
            return cleaned;
        }
    }
}
