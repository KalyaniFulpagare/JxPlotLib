package com.jplotx.chart.style;

import java.awt.Color;
import java.util.List;

public final class PlotThemes {

    private PlotThemes() {
    }

    public static PlotTheme defaultTheme() {
        return aurora();
    }

    public static PlotTheme aurora() {
        return new PlotTheme(
                "Aurora",
                new Color(244, 248, 253),
                new Color(226, 236, 248),
                new Color(255, 255, 255, 228),
                new Color(28, 42, 58),
                new Color(79, 97, 116),
                new Color(214, 223, 232),
                new Color(54, 69, 84),
                new Color(255, 255, 255, 236),
                new Color(206, 216, 228),
                List.of(
                        new Color(44, 127, 184),
                        new Color(240, 127, 90),
                        new Color(72, 168, 104),
                        new Color(158, 102, 204),
                        new Color(220, 92, 51),
                        new Color(33, 158, 188)
                ),
                new Color(38, 108, 196),
                new Color(234, 94, 45)
        );
    }

    public static PlotTheme graphite() {
        return new PlotTheme(
                "Graphite",
                new Color(239, 242, 246),
                new Color(212, 219, 228),
                new Color(250, 251, 252, 235),
                new Color(34, 40, 49),
                new Color(74, 85, 104),
                new Color(203, 210, 219),
                new Color(52, 64, 84),
                new Color(250, 251, 252, 236),
                new Color(196, 204, 214),
                List.of(
                        new Color(62, 120, 190),
                        new Color(95, 165, 105),
                        new Color(225, 118, 66),
                        new Color(150, 104, 195),
                        new Color(214, 90, 112),
                        new Color(68, 180, 174)
                ),
                new Color(45, 111, 180),
                new Color(231, 112, 66)
        );
    }

    public static PlotTheme sunset() {
        return new PlotTheme(
                "Sunset",
                new Color(252, 245, 238),
                new Color(246, 222, 208),
                new Color(255, 250, 245, 234),
                new Color(87, 48, 31),
                new Color(118, 82, 67),
                new Color(227, 205, 193),
                new Color(96, 63, 50),
                new Color(255, 250, 245, 236),
                new Color(224, 203, 191),
                List.of(
                        new Color(198, 92, 54),
                        new Color(229, 145, 72),
                        new Color(104, 153, 112),
                        new Color(110, 121, 188),
                        new Color(164, 81, 120),
                        new Color(70, 146, 170)
                ),
                new Color(210, 98, 67),
                new Color(245, 182, 84)
        );
    }
}
