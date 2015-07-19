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
 * Position graphs in absolute coordinates. This is actually a dummy layout - no
 * layout information is used.
 *
 * @author teras
 */
public class ManualGraphLayout implements GraphLayout, Serializable {

    /**
     * This is a dummy layout manager, which actually does nothing
     *
     * @param page The Page which layout we want to calculate
     * @param buffer The definition part of the multiplot layout
     */
    public void setDefinition(Page page, StringBuilder buffer) {
    }

}
