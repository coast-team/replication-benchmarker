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

package com.panayotis.gnuplot.dataset.parser;

/**
 * Use a specific numeric parser to check if the data provided are valid or not.
 *
 * @author teras
 */
public interface DataParser {

    /**
     * Check whether a data value with a specific index number is valid or not
     *
     * @param data The data to check
     * @param index The index of the specified data
     * @return True, if the data is valid.
     */
    public boolean isValid(String data, int index);
}
