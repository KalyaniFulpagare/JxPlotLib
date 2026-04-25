# JPlotX

[![GitHub repo](https://img.shields.io/badge/GitHub-KalyaniFulpagare%2FJxPlotLib-181717?logo=github)](https://github.com/KalyaniFulpagare/JxPlotLib)
[![Java](https://img.shields.io/badge/Java-17+-ea7b1c)](https://www.oracle.com/java/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

JPlotX is a Java-based data visualization library inspired by Matplotlib. It renders line, area, bar, stacked bar, histogram, scatter, bubble, pie, and heatmap charts through a modular `Graphics2D` pipeline and exports them as PNG images for analytical reporting, dashboards, and backend workflows.

## Why JPlotX

- Built as a reusable library, not just a one-off visualization project
- Designed for developers who need dynamic chart generation from uploaded or queried data
- Supports runtime column mapping instead of fixed schemas
- Ready for Maven-style distribution and GitHub Packages publishing

## Features

- Modular chart engine with dedicated renderers for line, area, bar, stacked bar, histogram, scatter, bubble, pie, and heatmap plots
- Headless rendering API that works directly from Java code inside VS Code or any JVM project
- Developer-friendly fluent API with `line()`, `area()`, `bar()`, `stackedBar()`, `scatter()`, `bubble()`, `histogram()`, `pie()`, and `heatmap()` builders
- Multiple series support for line, area, scatter, bubble, grouped bar, and stacked bar charts
- Theme, legend, marker, margin, label, and stroke customization for developer-controlled output
- Dynamic table-driven plotting from uploaded CSV files or database query results
- Optional `show()` preview for a Matplotlib-style developer workflow during local development
- More accurate numeric scaling with padded ranges, readable tick generation, and consistent axis mapping
- Automatic PNG export to the `exports/` folder or any custom output path
- JDBC-based MySQL integration for converting query results into chart datasets
- Sample datasets included for immediate demo and testing without a database

## Project Structure

- `src/main/java/com/jplotx/chart`: chart types, plot context, datasets, and renderers
- `src/main/java/com/jplotx/data`: MySQL request/config models, repository, and sample data
- `src/main/java/com/jplotx/service`: headless image rendering, export, and dataset transformation services
- `src/main/java/com/jplotx/JPlotX.java`: public library entry point for render and export calls
- `src/main/java/com/jplotx/api`: fluent developer-facing builders
- `src/main/java/com/jplotx/preview`: optional local preview window support

## Run

If Maven is available:

```bash
mvn compile
mvn exec:java
mvn package
```

If you prefer `javac` directly:

```bash
javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.jplotx.JPlotXApplication
```

This generates sample chart images into `exports/` and prints the created file paths in the terminal.

To make the library available to other local Maven projects:

```bash
mvn install
```

## Use As A Library

```java
import com.jplotx.JPlotX;
import com.jplotx.chart.style.PlotThemes;
import com.jplotx.data.table.DataTable;

import java.nio.file.Path;

JPlotX jPlotX = new JPlotX();
DataTable uploaded = jPlotX.loadCsv(Path.of("uploads", "sales.csv"));

Path output = JPlotX.line()
    .title("Revenue Trend By Product")
    .xLabel("Quarter")
    .yLabel("Revenue")
    .theme(PlotThemes.graphite())
    .legend(true)
    .fromTable(uploaded, "quarter", "revenue", "product")
    .export(Path.of("exports"), "revenue-trend");
```

The library does not require fixed column names. Developers map uploaded columns at runtime based on the file or query they receive.

For a local developer preview, similar to a plotting workflow:

```java
JPlotX.line()
    .title("Revenue Trend")
    .xLabel("Quarter")
    .yLabel("Revenue")
    .fromTable(jPlotX.loadCsv(Path.of("uploads", "sales.csv")), "quarter", "revenue", "product")
    .show();
```

## Dynamic Uploaded Data

CSV upload flow:

```java
JPlotX jPlotX = new JPlotX();
DataTable table = jPlotX.loadCsv(Path.of("uploads", "report.csv"));

JPlotX.bar()
    .title("Sales By Region")
    .xLabel("Region")
    .yLabel("Sales")
    .fromTable(table, "region", "sales", "year")
    .show();
```

Heatmap from uploaded tabular data:

```java
DataTable table = jPlotX.loadCsv(Path.of("uploads", "utilization.csv"));

JPlotX.heatmap()
    .title("Utilization Heatmap")
    .xLabel("Team")
    .yLabel("Month")
    .fromTable(table, "month", "team", "utilization")
    .show();
```

Bubble chart from uploaded data:

```java
DataTable table = jPlotX.loadCsv(Path.of("uploads", "campaigns.csv"));

JPlotX.bubble()
    .title("Campaign Performance")
    .xLabel("Spend")
    .yLabel("Leads")
    .fromTable(table, "spend", "leads", "conversions", "campaign", "label")
    .show();
```

## Tuning And Themes

```java
JPlotX.scatter()
    .title("Spend vs Leads")
    .xLabel("Ad Spend")
    .yLabel("Leads")
    .theme(PlotThemes.sunset())
    .pointLabels(true)
    .markerSize(14)
    .strokeWidth(3f)
    .margins(120, 70, 90, 120)
    .addSeries("Campaign A", List.of(12d, 18d, 25d), List.of(21d, 29d, 38d))
    .addSeries("Campaign B", List.of(10d, 15d, 22d), List.of(18d, 24d, 31d))
    .show();
```

Themes are immutable, so developers can create their own variants:

```java
var customTheme = PlotThemes.aurora()
    .withPalette(List.of(new Color(10, 90, 160), new Color(215, 110, 60)))
    .withHeatmapColors(new Color(30, 90, 180), new Color(240, 110, 70));
```

You can also render into memory:

```java
BufferedImage image = JPlotX.scatter()
    .title("Spend vs Leads")
    .xLabel("Ad Spend")
    .yLabel("Leads")
    .values(List.of(12d, 18d, 25d), List.of(21d, 29d, 38d))
    .render();
```

## MySQL Usage

JPlotX uses JDBC for MySQL imports. At runtime, make sure MySQL Connector/J is on the classpath so `com.mysql.cj.jdbc.Driver` is available.

Example table for line, bar, or scatter charts:

```sql
CREATE TABLE chart_data (
    label VARCHAR(50),
    x_value DOUBLE,
    y_value DOUBLE
);
```

Example table for heatmaps:

```sql
CREATE TABLE heatmap_data (
    row_key VARCHAR(50),
    column_key VARCHAR(50),
    value DOUBLE
);
```

## MySQL Usage From Code

```java
DatabaseConfig config = new DatabaseConfig("jdbc:mysql://localhost:3306/analytics", "root", "password");
DataTable table = new MySqlDataRepository().loadTable(
    config,
    "SELECT quarter, product, revenue FROM sales_view"
);

JPlotX.line()
    .title("MySQL Revenue Trend")
    .xLabel("Quarter")
    .yLabel("Revenue")
    .fromTable(table, "quarter", "revenue", "product")
    .export(Path.of("exports"), "mysql-revenue");
```

## Maven Dependency

After packaging or installing locally, other Java projects can consume JPlotX using:

```xml
<dependency>
    <groupId>com.jplotx</groupId>
    <artifactId>jplotx</artifactId>
    <version>1.0.0</version>
</dependency>
```

## GitHub Packages Publishing

JPlotX is prepared for GitHub Packages publishing.

High-level flow:

1. Push this project to a GitHub repository.
2. Publish with the included GitHub Actions workflow at `.github/workflows/publish-github-packages.yml`, or deploy locally with Maven.
3. In another project, add the GitHub Packages repository plus the `com.jplotx:jplotx:1.0.0` dependency.

Detailed setup steps are in [docs/github-packages.md](/C:/Users/Kalyani/Desktop/JxPlotLib/docs/github-packages.md).

Example consumer Maven config:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/OWNER/REPO</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.jplotx</groupId>
        <artifactId>jplotx</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Repo Assets

- License: [LICENSE](/C:/Users/Kalyani/Desktop/JxPlotLib/LICENSE)
- Changelog: [CHANGELOG.md](/C:/Users/Kalyani/Desktop/JxPlotLib/CHANGELOG.md)
- Contribution guide: [CONTRIBUTING.md](/C:/Users/Kalyani/Desktop/JxPlotLib/CONTRIBUTING.md)
- GitHub Packages guide: [docs/github-packages.md](/C:/Users/Kalyani/Desktop/JxPlotLib/docs/github-packages.md)

## Resume-Style Summary

Developed JPlotX, a reusable Java-based data visualization library inspired by Python Matplotlib, enabling dynamic rendering of line, area, bar, stacked bar, scatter, bubble, histogram, pie, and heatmap charts from CSV uploads and MySQL-sourced data. Implemented a scalable, object-oriented rendering architecture with customizable themes, multi-series support, runtime column mapping, and Maven-ready distribution for third-party developer integration.
