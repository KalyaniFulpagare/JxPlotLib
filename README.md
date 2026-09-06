# JPlotX

[![GitHub repo](https://img.shields.io/badge/GitHub-KalyaniFulpagare%2FJxPlotLib-181717?logo=github)](https://github.com/KalyaniFulpagare/JxPlotLib)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kalyanifulpagare/jplotx)](https://central.sonatype.com/artifact/io.github.kalyanifulpagare/jplotx)
[![Java](https://img.shields.io/badge/Java-17+-ea7b1c)](https://www.oracle.com/java/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

JPlotX is a Java charting library for generating PNG charts from Java code, CSV files, and MySQL data.

## Gallery

| | |
|---|---|
| ![Line chart with trend line](assets/gallery/line.png) | ![Grouped bar chart](assets/gallery/bar.png) |
| ![Stacked bar chart](assets/gallery/stacked-bar.png) | ![Scatter chart with regression lines](assets/gallery/scatter.png) |
| ![Pie chart](assets/gallery/pie.png) | ![Heatmap](assets/gallery/heatmap.png) |

## Install

```xml
<dependency>
    <groupId>io.github.kalyanifulpagare</groupId>
    <artifactId>jplotx</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Quick Start

```java
import com.jplotx.JPlotX;
import java.util.List;
import java.nio.file.Path;

new JPlotX();
JPlotX.line()
    .title("Revenue Trend")
    .xLabel("Quarter")
    .yLabel("Revenue")
    .values(List.of(1d, 2d, 3d), List.of(10d, 25d, 18d))
    .trendLine()
    .export(Path.of("exports"), "revenue-trend");
```

## Plain Java Project

For a plain Java project, add `jplotx-1.0.1.jar` to the build path and use Java 17 or newer.

Direct jar:
https://repo1.maven.org/maven2/io/github/kalyanifulpagare/jplotx/1.0.1/jplotx-1.0.1.jar

For MySQL features, also add MySQL Connector/J.

## Aggregate Raw Data

```java
import com.jplotx.JPlotX;
import com.jplotx.data.table.Aggregation;
import com.jplotx.data.table.DataTable;
import java.nio.file.Path;

JPlotX jPlotX = new JPlotX();
DataTable raw = jPlotX.loadCsv(Path.of("data", "movie_ticket_dataset.csv"));
DataTable monthly = raw.aggregateBy("month", "total_revenue", Aggregation.SUM, "monthly_revenue");

JPlotX.line()
    .title("Monthly Revenue")
    .xLabel("Month")
    .yLabel("Revenue")
    .fromTable(monthly, "month", "monthly_revenue")
    .trendLine()
    .export(Path.of("exports"), "monthly-revenue");
```

More:

- Maven Central: https://central.sonatype.com/artifact/io.github.kalyanifulpagare/jplotx

