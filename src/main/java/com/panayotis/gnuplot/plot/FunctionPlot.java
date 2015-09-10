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
 * Created on 12 Οκτώβριος 2007, 5:17 μμ
 */

package com.panayotis.gnuplot.plot;

/**
 * This type of Plot is used to provide an interface to the functional plots of
 * gnuplot. For example plots like sin(x) or x**2+1 <br> It can also be used as
 * a generic plot command, if the user wishes to manually provide any plot
 * information, without the interference of JavaPlot library.
 *
 * @author teras
 */
public class FunctionPlot extends AbstractPlot {

    /**
     * Creates a new instance of FunctionPlot.
     *
     * @param function The function definition. It is a free text describing the
     * function to be plotted. The independent variable (for 2D plots) is x
     */
    public FunctionPlot(String function) {
        if (function == null)
            function = "0";
        set("title", "'" + function + "'");
        setDefinition(function);
    }

    /**
     * This method is unused in this object. It is here only for compatibility
     * reasons with Plot object.
     *
     * @param buf This parameter is not used
     */
    public void retrieveData(StringBuilder buf) {
    }
}
