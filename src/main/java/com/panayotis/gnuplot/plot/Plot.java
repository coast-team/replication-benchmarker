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
 * Created on October 14, 2007, 1:59 PM
 */

package com.panayotis.gnuplot.plot;

/**
 * This interface is used by JavaPlot to handle various plot arguments. It can
 * be implemented to provide other entries for the plot command
 *
 * @author teras
 */
public interface Plot {

    /**
     * Retrieve the definition part of the plot command. This is the part that
     * is given to the plot command, separated by commas. Commas and newlines
     * are automatically added
     *
     * @param buffer The buffer to store the argument of the plot command
     */
    public abstract void retrieveDefinition(StringBuilder buffer);

    /**
     * Retrieve the data set of this plot command. It is used only in data-set
     * plots and it is usually a set of numbers separated by space and newline
     * and terminated by the 'e' character. These data are appended at the end
     * of the actual plot command. If a plot argument does not require
     * additional data sets, then this method should do nothing.
     *
     * @param buffer The buffer to store the data set
     */
    public abstract void retrieveData(StringBuilder buffer);
}
