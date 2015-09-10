/* Copyright (c) 2007-2014 by panayotis.com
 *
 * JavaPlot is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * JavaPlot is free in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CrossMobile; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Created on 26 Οκτώβριος 2007, 4:32 μμ
 */

package com.panayotis.gnuplot.style;

/**
 * This is a list of possible colors which the user is able to use under gnuplot
 *
 * @author teras
 */
public enum NamedPlotColor implements PlotColor {

    WHITE,
    BLACK,
    GRAY0,
    GREY0,
    GRAY10,
    GREY10,
    GRAY20,
    GREY20,
    GRAY30,
    GREY30,
    GRAY40,
    GREY40,
    GRAY50,
    GREY50,
    GRAY60,
    GREY60,
    GRAY70,
    GREY70,
    GRAY80,
    GREY80,
    GRAY90,
    GREY90,
    GRAY100,
    GREY100,
    GRAY,
    GREY,
    LIGHT_GRAY,
    LIGHT_GREY,
    DARK_GRAY,
    DARK_GREY,
    RED,
    LIGHT_RED,
    DARK_RED,
    YELLOW,
    LIGHT_YELLOW,
    DARK_YELLOW,
    GREEN,
    LIGHT_GREEN,
    DARK_GREEN,
    SPRING_GREEN,
    FOREST_GREEN,
    SEA_GREEN,
    BLUE,
    LIGHT_BLUE,
    DARK_BLUE,
    MIDNIGHT_BLUE,
    NAVY,
    MEDIUM_BLUE,
    ROYALBLUE,
    SKYBLUE,
    CYAN,
    LIGHT_CYAN,
    DARK_CYAN,
    MAGENTA,
    LIGHT_MAGENTA,
    DARK_MAGENTA,
    TURQUOISE,
    LIGHT_TURQUOISE,
    DARK_TURQUOISE,
    PINK,
    LIGHT_PINK,
    DARK_PINK,
    CORAL,
    LIGHT_CORAL,
    ORANGE_RED,
    SALMON,
    LIGHT_SALMON,
    DARK_SALMON,
    AQUAMARINE,
    KHAKI,
    DARK_KHAKI,
    GOLDENROD,
    LIGHT_GOLDENROD,
    DARK_GOLDENROD,
    GOLD,
    BEIGE,
    BROWN,
    ORANGE,
    DARK_ORANGE,
    VIOLET,
    DARK_VIOLET,
    PLUM,
    PURPLE;

    /**
     * Get the representation of this color
     *
     * @return The color representation
     */
    public String getColor() {
        return "rgb '" + name().toLowerCase().replace('_', '-') + "'";
    }
}
