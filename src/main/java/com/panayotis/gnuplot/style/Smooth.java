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
 * Created on October 31, 2007, 2:29 AM
 */

package com.panayotis.gnuplot.style;

/**
 * Define how this plot should be smoothed. Please refer to the documentation of
 * gnuplot for specific explanation of each method
 *
 * @author teras
 */
public enum Smooth {

    UNIQUE,
    FREQUENCY,
    CSPLINES,
    ACSPLINES,
    BEZIER,
    SBEZIER;

    /**
     * Retrieve the gnuplot argument for this smoothing mechanism
     *
     * @return the gnuplot argument
     */
    public String toString() {
        return " smooth " + name().toLowerCase();
    }
}
