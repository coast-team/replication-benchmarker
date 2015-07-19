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
 * Created on 12 Οκτώβριος 2007, 4:12 μμ
 */

package com.panayotis.gnuplot;

/**
 * Runtime exception used whenever a recoverable error occured.
 *
 * @author teras
 */
public class GNUPlotException extends RuntimeException {

    /**
     * Creates a new instance of GNUPlotException
     *
     * @param reason Message describing what went wrong
     */
    public GNUPlotException(String reason) {
        super(reason);
    }
}
