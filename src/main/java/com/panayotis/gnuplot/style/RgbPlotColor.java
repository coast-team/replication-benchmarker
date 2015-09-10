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
 */

package com.panayotis.gnuplot.style;

/**
 * This is an RGB (red-green-blue) color. For more information see gnuplot
 * documentation for <a
 * href="http://www.gnuplot.info/docs/node62.html">linetype, colors, and
 * styles</a> as well as <a
 * href="http://www.gnuplot.info/docs/node63.html">Colorspec definition</a>.
 *
 * @author dan
 * @author teras
 */
public class RgbPlotColor implements PlotColor {

    private int red;
    private int green;
    private int blue;

    /**
     * Create a new color using the RGB colorspace.
     *
     * @param red value for red color, in the range of 0..255
     * @param green value for green color, in the range of 0..255
     * @param blue value for blue color, in the range of 0..255
     */
    public RgbPlotColor(int red, int green, int blue) {
        if (red < 0)
            red = 0;
        if (red > 255)
            red = 255;
        if (green < 0)
            green = 0;
        if (green > 255)
            green = 255;
        if (blue < 0)
            blue = 0;
        if (blue > 255)
            blue = 255;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Create a new color using the RGB colorspace.
     *
     * @param red value for red color, in the range of 0..1
     * @param green value for green color, in the range of 0..1
     * @param blue value for blue color, in the range of 0..1
     */
    public RgbPlotColor(float red, float green, float blue) {
        this(Math.round(255 * red), Math.round(255 * green), Math.round(255 * blue));
    }

    /**
     * Get the representation of this color
     *
     * @return The color representation
     */
    public String getColor() {
        return "rgb \"#" + String.format("%02x%02x%02x", red, green, blue) + "\"";
    }
}
