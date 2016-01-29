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
 * Created on 15 Οκτώβριος 2007, 12:14 μμ
 */

package com.panayotis.gnuplot.dataset;

/**
 * This is the generic interface which every data set object should provide. By
 * implementing this interface the author can create his own data objects which
 * can be used in JavaPlot
 *
 * @author teras
 */
public interface DataSet {

    /**
     * Retrieve how many points this data set has.
     *
     * @return the number of points
     */
    public int size();

    /**
     * Retrieve how many dimensions this dataset refers to. Typically, for every
     * point, this method informs JavaPlot how many "columns" of data this point
     * has. Make sure that every point has at least as many dimensions as what
     * is reported here .
     *
     * @return the number of dimensions
     */
    public int getDimensions();

    /**
     * Retrieve data information from a point. To retrieve information for each
     * point, a continuous call to this method will be executed, keeping the
     * item number constant and increasing the dimension.
     *
     * @param point The point number
     * @param dimension The point dimension (or "column") to request data from
     * @return the point data for this dimension
     */
    public String getPointValue(int point, int dimension);
}
