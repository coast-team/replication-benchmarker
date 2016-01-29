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
import java.io.Serializable;

/**
 * Align graphs evenly on the page, in a grid layout, based on
 * <p>
 * If you manually set metrics and use this, these metrics will be lost
 *
 * @author teras
 */
public class AutoGraphLayout implements GraphLayout, Serializable {

    /**
     * Orientation of the graph layout
     */
    public static final boolean DOWNWARDS = true, UPWARDS = false;
    /**
     * Draw rows or columns first
     */
    public static final boolean ROWSFIRST = true, COLUMNSFIRST = false;
    private boolean orientation;
    private boolean drawfirst;
    private int rows, cols;

    /**
     * Create a new automatic grid layout. Default values are ROWSFIRST,
     * DOWNWARDS, automatic layout of components
     */
    public AutoGraphLayout() {
        drawfirst = ROWSFIRST;
        orientation = DOWNWARDS;
        rows = -1;
        cols = -1;
    }

    /**
     * Set where the first graph will be put
     *
     * @param drawfirst Position of the first graph
     * @see #ROWSFIRST
     * @see #COLUMNSFIRST
     */
    public void setDrawFirst(boolean drawfirst) {
        this.drawfirst = drawfirst;
    }

    /**
     * Sey the orientation of the graphs, as being put
     *
     * @param orientation Selected orientation
     * @see #DOWNWARDS
     * @see #UPWARDS
     */
    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    /**
     * Manually set the number of rows. This method overrides the automatic
     * component layout.
     *
     * @param rows Desired number of rows. Set it to -1 to be automatically
     * computed.
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Manually set the number of columns. This method overrides the automatic
     * component layout.
     *
     * @param cols Desired number of columns. Set it to -1 to be automatically
     * computed.
     */
    public void setColumns(int cols) {
        this.cols = cols;
    }

    private int getOtherDimension(int size, int dim) {
        return (int) Math.ceil((double) size / dim);
    }

    /**
     * Update the capacity of this layout. This manager creates a grid, as much
     * square as possible, if automatic layout is wanted.
     *
     * @param page The page with the elements we would like to position
     * @param buffer Where to send commands, just after the "set multiplot"
     * part.
     */
    public void setDefinition(Page page, StringBuilder buffer) {
        int size = page.size();

        if (size <= 0)
            return;

        int drawcols = cols, drawrows = rows;
        if (cols > 0 && rows > 0) {
            drawcols = cols;
            drawrows = rows;
        } else if (cols > 0) {
            drawcols = cols;
            drawrows = getOtherDimension(size, drawcols);
        } else if (rows > 0) {
            drawrows = rows;
            drawcols = getOtherDimension(size, drawrows);
        } else {
            drawrows = (int) (Math.floor(Math.sqrt(size)));
            drawcols = getOtherDimension(size, drawrows);
        }

        buffer.append(" layout ");
        buffer.append(drawrows).append(',').append(drawcols);

        if (drawfirst == ROWSFIRST)
            buffer.append(" rowsfirst");
        else
            buffer.append(" columnsfirst");

        if (orientation == DOWNWARDS)
            buffer.append(" downwards");
        else
            buffer.append(" upwards");
    }
}
