package com.jplotx.chart.render;

import com.jplotx.chart.dataset.XYPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegressionSupportTest {

    private static final double DELTA = 1e-9;

    @Test
    void returnsNullForNullInput() {
        assertNull(RegressionSupport.fit(null));
    }

    @Test
    void returnsNullForFewerThanTwoPoints() {
        assertNull(RegressionSupport.fit(List.of(new XYPoint("a", 1, 1))));
    }

    @Test
    void fitsAPerfectLineExactly() {
        List<XYPoint> points = List.of(
                new XYPoint("p1", 1, 3),
                new XYPoint("p2", 2, 5),
                new XYPoint("p3", 3, 7),
                new XYPoint("p4", 4, 9)
        );

        RegressionSupport.TrendLine trend = RegressionSupport.fit(points);

        assertEquals(2.0, trend.slope(), DELTA);
        assertEquals(1.0, trend.intercept(), DELTA);
        assertEquals(1.0, trend.rSquared(), DELTA);
        assertEquals(11.0, trend.yAt(5), DELTA);
    }

    @Test
    void returnsNullWhenAllPointsShareTheSameX() {
        List<XYPoint> points = List.of(
                new XYPoint("p1", 5, 1),
                new XYPoint("p2", 5, 10),
                new XYPoint("p3", 5, 20)
        );

        assertNull(RegressionSupport.fit(points));
    }

    @Test
    void ignoresNonFinitePointsAndStillFits() {
        List<XYPoint> points = List.of(
                new XYPoint("p1", 1, 3),
                new XYPoint("bad", Double.NaN, 100),
                new XYPoint("p2", 2, 5),
                new XYPoint("p3", 3, 7),
                new XYPoint("p4", 4, 9)
        );

        RegressionSupport.TrendLine trend = RegressionSupport.fit(points);

        assertEquals(2.0, trend.slope(), DELTA);
        assertEquals(1.0, trend.intercept(), DELTA);
    }

    @Test
    void noisyDataProducesRSquaredBetweenZeroAndOne() {
        List<XYPoint> points = List.of(
                new XYPoint("p1", 1, 2),
                new XYPoint("p2", 2, 3.5),
                new XYPoint("p3", 3, 3),
                new XYPoint("p4", 4, 6)
        );

        RegressionSupport.TrendLine trend = RegressionSupport.fit(points);

        assertTrue(trend.rSquared() > 0.0 && trend.rSquared() < 1.0);
    }
}