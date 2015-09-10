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

/**
 * Align graphs evenly on the page This Layout is based on AutoGraphLayout
 */
public class StripeLayout extends AutoGraphLayout {

    /**
     * Information if rows or columns are added automatically
     */
    public static final boolean EXPANDROWS = true, EXPANDCOLUMNS = false;

    /**
     * Create a new Strip layout. Default behavior is EXPANDROWS.
     */
    public StripeLayout() {
        setType(EXPANDROWS);
    }

    /**
     * Set the default behavior
     *
     * @param type Whether EXPANDROWS or EXPANDCOLUMNS is desired.
     * @see #EXPANDROWS #EXPANDCOLUMNS
     */
    public void setType(boolean type) {
        if (type == EXPANDROWS) {
            super.setRows(-1);
            super.setColumns(1);
        } else {
            super.setRows(1);
            super.setColumns(-1);
        }
    }

    /**
     * Set behavior, depending on the number of rows. It always creates stripes
     * and it might change to EXPANDCOLUMNS if rows are less than 2.
     *
     * @param rows Number of desired rows
     */
    public void setRows(int rows) {
        if (rows > 1)
            setType(EXPANDROWS);
        else
            setType(EXPANDCOLUMNS);
    }

    /**
     * Set behaviour, depending on the number of columns. It always creates
     * stripes and it might change to EXPANDROWS if columns are less than 2.
     *
     * @param cols Number of desired columns
     */
    public void setColumns(int cols) {
        if (cols > 1)
            setType(EXPANDCOLUMNS);
        else
            setType(EXPANDROWS);
    }
}
