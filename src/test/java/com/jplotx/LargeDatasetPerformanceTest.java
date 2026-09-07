package com.jplotx;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Not a correctness test in the usual sense — this renders large point counts
 * and prints timing so the library's practical limits are actually known
 * (rather than assumed), and fails only if rendering hangs or throws, or
 * takes drastically longer than expected (a regression guard, not a tight
 * performance benchmark).
 */
class LargeDatasetPerformanceTest {

    @Test
    void rendersTenThousandPointsInReasonableTime() {
        renderAndReport(10_000, 10_000);
    }

    @Test
    void rendersOneHundredThousandPointsInReasonableTime() {
        renderAndReport(100_000, 30_000);
    }

    private void renderAndReport(int pointCount, long maxMillisAllowed) {
        List<Double> xValues = new ArrayList<>(pointCount);
        List<Double> yValues = new ArrayList<>(pointCount);
        for (int i = 0; i < pointCount; i++) {
            xValues.add((double) i);
            yValues.add(Math.sin(i * 0.001) * 100 + (i % 37));
        }

        long start = System.nanoTime();
        BufferedImage image = JPlotX.scatter()
                .title("Performance Test: " + pointCount + " points")
                .values(xValues, yValues)
                .render();
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        assertNotNull(image);
        System.out.println("[perf] " + pointCount + " points rendered in " + elapsedMillis + " ms");

        assertTrue(elapsedMillis < maxMillisAllowed,
                "Rendering " + pointCount + " points took " + elapsedMillis
                        + " ms, exceeding the " + maxMillisAllowed + " ms regression guard.");
    }
}