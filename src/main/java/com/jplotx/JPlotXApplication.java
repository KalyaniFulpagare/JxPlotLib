package com.jplotx;

import com.jplotx.chart.style.MarkerStyle;
import com.jplotx.chart.style.PlotThemes;
import com.jplotx.data.table.Aggregation;
import com.jplotx.data.table.DataTable;

import java.nio.file.Path;

public final class JPlotXApplication {

    private JPlotXApplication() {
    }

    public static void main(String[] args) throws Exception {
        JPlotX jPlotX = new JPlotX();
        DataTable salesComparison = jPlotX.loadCsv(Path.of("samples", "sales-comparison.csv"));
        DataTable areaRevenue = jPlotX.loadCsv(Path.of("samples", "area-revenue.csv"));
        DataTable regionalSales = jPlotX.loadCsv(Path.of("samples", "regional-sales.csv"));
        DataTable stackedExpense = jPlotX.loadCsv(Path.of("samples", "stacked-expense.csv"));
        DataTable leadScatter = jPlotX.loadCsv(Path.of("samples", "lead-scatter.csv"));
        DataTable bubbleCampaigns = jPlotX.loadCsv(Path.of("samples", "bubble-campaigns.csv"));
        DataTable histogram = jPlotX.loadCsv(Path.of("samples", "histogram.csv"));
        DataTable pieMarketShare = jPlotX.loadCsv(Path.of("samples", "pie-market-share.csv"));
        DataTable heatmap = jPlotX.loadCsv(Path.of("samples", "heatmap.csv"));
        DataTable averageRegionalSales = regionalSales.aggregateBy("region", "sales", Aggregation.AVG, "avg_sales");

        Path line = JPlotX.line()
                .title("Revenue Trend By Product")
                .xLabel("Quarter")
                .yLabel("Revenue (L)")
                .theme(PlotThemes.graphite())
                .legend(true)
                .markerStyle(MarkerStyle.DIAMOND)
                .strokeWidth(3f)
                .fromTable(salesComparison, "quarter", "revenue", "product")
                .export(Path.of("exports"), "line");

        Path area = JPlotX.area()
                .title("Segment Revenue Area")
                .xLabel("Month")
                .yLabel("Revenue")
                .theme(PlotThemes.aurora())
                .legend(true)
                .fromTable(areaRevenue, "month", "revenue", "segment")
                .export(Path.of("exports"), "area");

        Path bar = JPlotX.bar()
                .title("Regional Sales Comparison")
                .xLabel("Region")
                .yLabel("Sales")
                .theme(PlotThemes.sunset())
                .legend(true)
                .valueLabels(true)
                .fromTable(regionalSales, "region", "sales", "year")
                .export(Path.of("exports"), "bar");

        Path aggregatedBar = JPlotX.bar()
                .title("Average Regional Sales")
                .xLabel("Region")
                .yLabel("Average Sales")
                .theme(PlotThemes.aurora())
                .valueLabels(true)
                .fromTable(averageRegionalSales, "region", "avg_sales")
                .export(Path.of("exports"), "aggregated-bar");

        Path stackedBar = JPlotX.stackedBar()
                .title("Quarterly Expense Mix")
                .xLabel("Quarter")
                .yLabel("Amount")
                .theme(PlotThemes.graphite())
                .legend(true)
                .fromTable(stackedExpense, "quarter", "amount", "category")
                .export(Path.of("exports"), "stacked-bar");

        Path scatter = JPlotX.scatter()
                .title("Spend vs Leads By Campaign")
                .xLabel("Ad Spend")
                .yLabel("Leads")
                .theme(PlotThemes.aurora())
                .legend(true)
                .trendLine()
                .pointLabels(true)
                .fromTable(leadScatter, "spend", "leads", "campaign", "label")
                .export(Path.of("exports"), "scatter");

        Path bubble = JPlotX.bubble()
                .title("Campaign Bubble Performance")
                .xLabel("Spend")
                .yLabel("Leads")
                .theme(PlotThemes.sunset())
                .legend(true)
                .pointLabels(true)
                .fromTable(bubbleCampaigns, "spend", "leads", "conversions", "campaign", "label")
                .export(Path.of("exports"), "bubble");

        Path histogramPath = JPlotX.histogram()
                .title("Delivery Time Distribution")
                .xLabel("Minutes")
                .seriesName("Delivery Time")
                .theme(PlotThemes.graphite())
                .valueLabels(true)
                .fromTable(histogram, "delivery_minutes")
                .bins(6)
                .export(Path.of("exports"), "histogram");

        Path pie = JPlotX.pie()
                .title("Market Share")
                .theme(PlotThemes.graphite())
                .fromTable(pieMarketShare, "vendor", "share")
                .export(Path.of("exports"), "pie");

        Path pieSvg = JPlotX.pie()
                .title("Market Share")
                .theme(PlotThemes.graphite())
                .fromTable(pieMarketShare, "vendor", "share")
                .export(Path.of("exports"), "pie", "svg");

        Path heatmapPath = JPlotX.heatmap()
                .title("Monthly Regional Heatmap")
                .xLabel("Region")
                .yLabel("Month")
                .theme(PlotThemes.sunset())
                .valueLabels(true)
                .fromTable(heatmap, "month", "region", "value")
                .export(Path.of("exports"), "heatmap");

        System.out.println("Generated line chart at " + line.toAbsolutePath());
        System.out.println("Generated area chart at " + area.toAbsolutePath());
        System.out.println("Generated bar chart at " + bar.toAbsolutePath());
        System.out.println("Generated aggregated bar chart at " + aggregatedBar.toAbsolutePath());
        System.out.println("Generated stacked bar chart at " + stackedBar.toAbsolutePath());
        System.out.println("Generated scatter chart at " + scatter.toAbsolutePath());
        System.out.println("Generated bubble chart at " + bubble.toAbsolutePath());
        System.out.println("Generated histogram chart at " + histogramPath.toAbsolutePath());
        System.out.println("Generated pie chart at " + pie.toAbsolutePath());
        System.out.println("Generated heatmap chart at " + heatmapPath.toAbsolutePath());
    }
}
