package com.jplotx.data.table;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataTableAggregationTest {

    private DataTable regionalSales() {
        return DataTable.ofRows(List.of(
                Map.of("region", "North", "sales", 10.0),
                Map.of("region", "North", "sales", 20.0),
                Map.of("region", "South", "sales", 5.0)
        ));
    }

    @Test
    void sumsValuesPerGroup() {
        DataTable result = regionalSales().aggregateBy("region", "sales", Aggregation.SUM);

        assertEquals(2, result.rowCount());
        assertEquals("North", result.rows().get(0).getString("region"));
        assertEquals(30.0, result.rows().get(0).getDouble("sum_sales"));
        assertEquals("South", result.rows().get(1).getString("region"));
        assertEquals(5.0, result.rows().get(1).getDouble("sum_sales"));
    }

    @Test
    void averagesValuesPerGroup() {
        DataTable result = regionalSales().aggregateBy("region", "sales", Aggregation.AVG);

        assertEquals(15.0, result.rows().get(0).getDouble("avg_sales"));
        assertEquals(5.0, result.rows().get(1).getDouble("avg_sales"));
    }

    @Test
    void minMaxAndCountAreComputedCorrectly() {
        DataTable min = regionalSales().aggregateBy("region", "sales", Aggregation.MIN);
        DataTable max = regionalSales().aggregateBy("region", "sales", Aggregation.MAX);
        DataTable count = regionalSales().aggregateBy("region", "sales", Aggregation.COUNT);

        assertEquals(10.0, min.rows().get(0).getDouble("min_sales"));
        assertEquals(20.0, max.rows().get(0).getDouble("max_sales"));
        assertEquals(2.0, count.rows().get(0).getDouble("count_sales"));
    }

    @Test
    void supportsMultipleGroupColumns() {
        DataTable table = DataTable.ofRows(List.of(
                Map.of("region", "North", "quarter", "Q1", "sales", 10.0),
                Map.of("region", "North", "quarter", "Q1", "sales", 5.0),
                Map.of("region", "North", "quarter", "Q2", "sales", 8.0)
        ));

        DataTable result = table.aggregateBy(List.of("region", "quarter"), "sales", Aggregation.SUM);

        assertEquals(2, result.rowCount());
        assertEquals("Q1", result.rows().get(0).getString("quarter"));
        assertEquals(15.0, result.rows().get(0).getDouble("sum_sales"));
        assertEquals("Q2", result.rows().get(1).getString("quarter"));
        assertEquals(8.0, result.rows().get(1).getDouble("sum_sales"));
    }

    @Test
    void supportsCustomResultColumnName() {
        DataTable result = regionalSales().aggregateBy("region", "sales", Aggregation.SUM, "total");

        assertEquals(30.0, result.rows().get(0).getDouble("total"));
    }

    @Test
    void rejectsEmptyGroupColumns() {
        DataTable table = regionalSales();
        assertThrows(IllegalArgumentException.class,
                () -> table.aggregateBy(List.of(), "sales", Aggregation.SUM));
    }

    @Test
    void rejectsUnknownColumn() {
        DataTable table = regionalSales();
        assertThrows(IllegalArgumentException.class,
                () -> table.aggregateBy("region", "does_not_exist", Aggregation.SUM));
    }

    @Test
    void rejectsResultColumnClashingWithGroupColumn() {
        DataTable table = regionalSales();
        assertThrows(IllegalArgumentException.class,
                () -> table.aggregateBy("region", "sales", Aggregation.SUM, "region"));
    }
}