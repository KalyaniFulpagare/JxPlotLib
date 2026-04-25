# JPlotX

[![GitHub repo](https://img.shields.io/badge/GitHub-KalyaniFulpagare%2FJxPlotLib-181717?logo=github)](https://github.com/KalyaniFulpagare/JxPlotLib)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kalyanifulpagare/jplotx)](https://central.sonatype.com/artifact/io.github.kalyanifulpagare/jplotx)
[![Java](https://img.shields.io/badge/Java-17+-ea7b1c)](https://www.oracle.com/java/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

JPlotX is a Java charting library for generating PNG charts from Java code, CSV files, and MySQL data.

## Install

```xml
<dependency>
    <groupId>io.github.kalyanifulpagare</groupId>
    <artifactId>jplotx</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

```java
import com.jplotx.JPlotX;
import java.nio.file.Path;

new JPlotX();
JPlotX.line()
    .title("Revenue Trend")
    .xLabel("Quarter")
    .yLabel("Revenue")
    .values(List.of(1d, 2d, 3d), List.of(10d, 25d, 18d))
    .export(Path.of("exports"), "revenue-trend");
```

More:

- Maven Central: https://central.sonatype.com/artifact/io.github.kalyanifulpagare/jplotx
- Publishing guide: [docs/maven-central.md](docs/maven-central.md)
- GitHub Packages: [docs/github-packages.md](docs/github-packages.md)
