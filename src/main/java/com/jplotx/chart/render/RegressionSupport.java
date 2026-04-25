package com.jplotx.chart.render;

import com.jplotx.chart.dataset.XYPoint;

import java.util.ArrayList;
import java.util.List;

final class RegressionSupport {

    private static final double EPSILON = 1.0e-9;

    private RegressionSupport() {
    }

    static TrendLine fit(List<XYPoint> points) {
        if (points == null || points.size() < 2) {
            return null;
        }

        List<XYPoint> finitePoints = new ArrayList<>();
        for (XYPoint point : points) {
            if (Double.isFinite(point.x()) && Double.isFinite(point.y())) {
                finitePoints.add(point);
            }
        }
        if (finitePoints.size() < 2) {
            return null;
        }

        double sumX = 0;
        double sumY = 0;
        for (XYPoint point : finitePoints) {
            sumX += point.x();
            sumY += point.y();
        }

        double meanX = sumX / finitePoints.size();
        double meanY = sumY / finitePoints.size();
        double covariance = 0;
        double varianceX = 0;
        for (XYPoint point : finitePoints) {
            double dx = point.x() - meanX;
            covariance += dx * (point.y() - meanY);
            varianceX += dx * dx;
        }
        if (Math.abs(varianceX) < EPSILON) {
            return null;
        }

        double slope = covariance / varianceX;
        double intercept = meanY - (slope * meanX);
        double residualSum = 0;
        double totalSum = 0;
        for (XYPoint point : finitePoints) {
            double predicted = (slope * point.x()) + intercept;
            residualSum += Math.pow(point.y() - predicted, 2);
            totalSum += Math.pow(point.y() - meanY, 2);
        }

        double rSquared = totalSum < EPSILON ? 1d : Math.max(0d, 1d - (residualSum / totalSum));
        return new TrendLine(slope, intercept, rSquared);
    }

    record TrendLine(double slope, double intercept, double rSquared) {
        double yAt(double x) {
            return (slope * x) + intercept;
        }

        String summary() {
            return String.format("y = %.2fx %s %.2f, R² = %.3f",
                    slope,
                    intercept >= 0 ? "+" : "-",
                    Math.abs(intercept),
                    rSquared);
        }
    }
}
