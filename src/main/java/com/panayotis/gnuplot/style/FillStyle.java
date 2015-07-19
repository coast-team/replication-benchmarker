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
 * Created on 26 Οκτώβριος 2007, 4:35 μμ
 */

package com.panayotis.gnuplot.style;

import com.panayotis.gnuplot.PropertiesHolder;

/**
 * This object carries information on how to fill a specific graph
 *
 * @author teras
 */
public class FillStyle extends PropertiesHolder {

    /**
     * Define the fill styles
     */
    public enum Fill {

        EMPTY, SOLID, PATTERN
    };
    private Fill style;
    private String params;

    /**
     * Create a new fill style object with default style
     */
    public FillStyle() {
        this(null);
    }

    /**
     * Create a new fill style object with a specific fill style
     *
     * @param style The style to use If it is null, then this graph will not be
     * filled.
     */
    public FillStyle(Fill style) {
        super(" ", "");
        setStyle(style);
    }

    /**
     * Set the border type
     *
     * @param type An integer describing how the border will look like. This
     * parameter is terminal specific
     */
    public void setBorder(int type) {
        unset("noborder");
        set("border", String.valueOf(type));
    }

    /**
     * Remove the border of this graph
     */
    public void removeBorder() {
        unset("border");
        set("noborder");
    }

    /**
     * Set the density of this fill style
     *
     * @param density A number between 0 and 1
     */
    public void setDensity(float density) {
        setStyle(Fill.SOLID);
        params = String.valueOf(density);
    }

    /**
     * Set the fill pattern
     *
     * @param pattern An integer describing the fill pattern. This parameter is
     * terminal specific
     */
    public void setPattern(int pattern) {
        setStyle(Fill.PATTERN);
        params = String.valueOf(pattern);
    }

    /**
     * Set the fill style
     *
     * @param style The style to use. If it is null, then this graph will not be
     * filled.
     */
    public void setStyle(Fill style) {
        if (style == null) {
            style = Fill.EMPTY;
            params = "";
        }
        this.style = style;
    }

    /**
     * Retrieve information for this style. This method is used internally by
     * JavaPlot
     *
     * @param buf The String buffer to store information about this style
     */
    @Override
    public void appendProperties(StringBuilder buf) {
        buf.append(" fill ");
        buf.append(style.name().toLowerCase());
        if (!params.equals(""))
            buf.append(' ').append(params);

        super.appendProperties(buf);
    }
}
