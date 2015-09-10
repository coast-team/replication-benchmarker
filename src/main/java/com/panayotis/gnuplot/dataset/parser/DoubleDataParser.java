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
 * Parser for Double data
 *
 * @author teras
 */
public class DoubleDataParser extends NumericDataParser {

    /**
     * Create a new numeric data parser for Double values
     */
    public DoubleDataParser() {
        super();
    }

    /**
     * Create a new Double data parser, with the information that the first
     * column is in date format.
     *
     * @param first_column_date Whether the first column is in date format
     */
    public DoubleDataParser(boolean first_column_date) {
        super(first_column_date);
    }

    /**
     * Check whether this String represents a Double number
     *
     * @param format the String containing the Double number
     * @return True, if this is a representation of a Double number
     */
    protected boolean checkNumberFormat(String format) {
        try {
            Double.parseDouble(format);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
