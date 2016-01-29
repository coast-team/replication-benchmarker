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
 * Created on October 15, 2007, 1:54 AM
 */

package com.panayotis.gnuplot.dataset;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * This object represents a N-dimensional point. It is used in PointDataSet as
 * the actual data object
 *
 * @param N the type of this object. It should be a Number
 * @author teras
 */
public class Point<N extends Number> implements Serializable {

    private N[] coords;

    /**
     * Creates a new instance of Point with given coordinates
     *
     * @param coords The coordinates given as a list of native (or boxed) type
     * numbers.
     */
    @SuppressWarnings("unchecked")
    public Point(N... coords) {
        this.coords = (N[]) Array.newInstance(Number.class, coords.length);
        System.arraycopy(coords, 0, this.coords, 0, coords.length);
    }

    /**
     * Retrieve the value of a specific coordinate of this point
     *
     * @param dimension the coordination dimension
     * @return the value of this point
     * @throws java.lang.ArrayIndexOutOfBoundsException The coordination
     * required is not present
     */
    public N get(int dimension) throws ArrayIndexOutOfBoundsException {
        return coords[dimension];
    }

    /**
     * Retrieve the actual coordinations of this point
     *
     * @return The coordinations (dimensions) of this point
     */
    public int getDimensions() {
        return coords.length;
    }
}
