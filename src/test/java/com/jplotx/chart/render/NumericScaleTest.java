package com.jplotx.chart.render;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumericScaleTest {

    private static final double DELTA = 1e-9;

    @Test
    void computesNiceRoundedBoundsAndTicks() {
        NumericScale scale = NumericScale.of(0, 50, true, 6);

        assertEquals(0.0, scale.lowerBound(), DELTA);
        assertEquals(50.0, scale.upperBound(), DELTA);
        assertEquals(List.of(0.0, 10.0, 20.0, 30.0, 40.0, 50.0), scale.ticks());
    }

    @Test
    void mapReservesAnEdgeMarginSoExtremesAreNotFlushWithThePixelBounds() {
        NumericScale scale = NumericScale.of(0, 50, true, 6);
        int pixels = 1000;

        double atMin = scale.map(0, pixels);
        double atMax = scale.map(50, pixels);

        assertEquals(6.0, atMin, DELTA);
        assertEquals(994.0, atMax, DELTA);
        assertTrue(atMin > 0);
        assertTrue(atMax < pixels);
    }

    @Test
    void ofWithTopHeadroomExtendsTheUpperBoundBeyondTheRawDataMax() {
        NumericScale plain = NumericScale.of(18, 55, false, 6);
        NumericScale withHeadroom = NumericScale.ofWithTopHeadroom(18, 55, false, 6, 0.20);

        assertTrue(withHeadroom.upperBound() > plain.upperBound());
        assertTrue(withHeadroom.upperBound() - 55 >= 10.0);
    }
}