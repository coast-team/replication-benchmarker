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

package com.panayotis.gnuplot.layout;

import com.panayotis.gnuplot.plot.Page;

/**
 * This Object is used to define how graphs will be positioned on the whole page
 *
 * @author teras
 */
public interface GraphLayout {

    /**
     * Sets the required definitions in the "set multiplot" part of gnuplot
     * commands. It can be also used to set various parameters, such as X/Y
     * position or dimension.
     *
     * @param page The Page we are referring to
     * @param buffer Where to send commands, just after the "set multiplot"
     * part. It might not be used.
     */
    public abstract void setDefinition(Page page, StringBuilder buffer);
}
