# Changelog

All notable changes to JPlotX will be documented in this file.

## 1.0.2 - 2026-09-07

- Fixed markers/points on line, scatter, and bubble charts getting clipped at the plot's edge when a value sat exactly at the axis's min/max bound
- Fixed the legend overlapping the last data point on charts with a rising trend, by giving the y-axis headroom above the actual data max
- Fixed the pie chart rendering flush against the left edge instead of centered in the available space
- Added SVG (vector) export via FreeHEP VectorGraphics (LGPL), alongside existing PNG output
- Added JPEG/JPG export support
- Added a JUnit 5 test suite (25 tests) covering data aggregation, trend-line regression, axis scaling, image/SVG export, and rendering performance on large datasets
- Added a GitHub Actions workflow that runs the full test suite on every push and pull request
- Added javadoc to the public API (`JPlotX`, `PlotBuilder`, `DataTable`)
- Added a chart gallery to the README

## 1.0.1 - 2026-04-26

- Added `trendLine()` support for numeric line and scatter charts with regression equation and `R^2` overlay
- Added `DataTable.aggregateBy(...)` with `SUM`, `AVG`, `MIN`, `MAX`, and `COUNT` for turning raw CSV data into chart-ready summaries
- Added aggregated usage examples for preparing monthly and grouped analytics from raw datasets
- Prepared release metadata and docs for the `1.0.1` Maven Central publication

## 1.0.0 - 2026-04-25

- Initial public release of JPlotX as a reusable Java plotting library
- Added dynamic plotting for line, area, bar, stacked bar, scatter, bubble, histogram, pie, and heatmap charts
- Added fluent builder APIs for developer-friendly chart creation
- Added dynamic table-driven chart mapping from CSV uploads and MySQL query results
- Added themes, legends, marker customization, margins, labels, and multi-series support
- Added GitHub Packages publishing workflow and Maven package metadata
