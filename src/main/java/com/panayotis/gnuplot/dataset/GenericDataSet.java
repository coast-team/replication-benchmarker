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

package com.panayotis.gnuplot.dataset;

import com.panayotis.gnuplot.dataset.parser.DataParser;
import com.panayotis.gnuplot.dataset.parser.DoubleDataParser;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Generic data class to store data. This class stores data as a list of
 * Strings, not numbers. Still, the user can check if the data are valid, by
 * using a specific DataParser for this object.<p>
 * In this dataset one can use any type of data, while in PointDataSet the data
 * are stricted to numerical data only. Thus, data such as dates can be used.
 *
 * @see com.panayotis.gnuplot.dataset.parser.DataParser
 * @see com.panayotis.gnuplot.dataset.PointDataSet
 * @author teras
 */
public class GenericDataSet extends ArrayList<ArrayList<String>> implements DataSet {

    private DataParser parser;

    /**
     * Create a new instance of GenericDataSet, with the default DataParser
     * (DoubleDataParser)
     *
     * @see com.panayotis.gnuplot.dataset.parser.DoubleDataParser
     */
    public GenericDataSet() {
        super();
        parser = new DoubleDataParser();
    }

    /**
     * Create a new instance of GenericDataSet, with the default DataParser
     * (DoubleDataParser), and the information that the first column is in date
     * format.
     *
     * @param first_column_date Whether the first column is in date format
     */
    public GenericDataSet(boolean first_column_date) {
        super();
        parser = new DoubleDataParser(first_column_date);
    }

    /**
     * Create a new instance of GenericDataSet, with a given DataParser
     *
     * @param parser The DataParser to use
     */
    public GenericDataSet(DataParser parser) {
        super();
        this.parser = parser;
    }

    /**
     * Retrieve how many dimensions this dataset refers to.
     *
     * @return the number of dimensions
     * @see DataSet#getDimensions()
     */
    public int getDimensions() {
        if (size() < 1)
            return -1;
        return get(0).size();
    }

    /**
     * Retrieve data information from a point.
     *
     * @param point The point number
     * @param dimension The point dimension (or "column") to request data from
     * @return the point data for this dimension
     * @see DataSet#getPointValue(int,int)
     */
    public String getPointValue(int point, int dimension) {
        return get(point).get(dimension);
    }

    /**
     * Add a new point to this DataSet
     *
     * @param point The point to add to this DataSet
     * @return Whether the collection changed with this call
     * @throws java.lang.NumberFormatException If the given collection is not in
     * the correct format
     */
    public boolean add(ArrayList<String> point) throws NumberFormatException {
        checkData(point, getDimensions());
        return super.add(point);
    }

    /**
     * Add a new point to this DataSet at a specified position
     *
     * @param index Where to add this point
     * @param point The point to add to this DataSet
     * @throws java.lang.NumberFormatException If the given collection is not in
     * the correct format
     */
    public void add(int index, ArrayList<String> point) throws NumberFormatException {
        checkData(point, getDimensions());
        super.add(index, point);
    }

    /**
     * Add a collection of points to this DataSet
     *
     * @param pts The points colelction
     * @return Whether the collection changed with this call
     * @throws java.lang.NumberFormatException If the given collection is not in
     * the correct format
     */
    public boolean addAll(Collection<? extends ArrayList<String>> pts) throws NumberFormatException {
        int old_dim = getDimensions();
        for (ArrayList<String> p : pts)
            old_dim = checkData(p, old_dim);
        return super.addAll(pts);
    }

    /**
     * Add a collection of points to this DataSet starting at a specified
     * position if there are data at the specified position, these will be
     * shifted
     *
     * @param index Where to start adding point data.
     * @param pts The point collection to add
     * @return Whether the collection changed with this call
     * @throws java.lang.NumberFormatException If the given collection is not in
     * the correct format
     */
    public boolean addAll(int index, Collection<? extends ArrayList<String>> pts) throws NumberFormatException {
        int old_dim = getDimensions();
        for (ArrayList<String> p : pts)
            old_dim = checkData(p, old_dim);
        return super.addAll(index, pts);
    }

    /**
     * Replace the Point at the specified position with the provided one
     *
     * @param index The position of the point to be altered
     * @param point The point to use
     * @return The Point previously found in the specified position
     * @throws java.lang.NumberFormatException If the given collection is not in
     * the correct format
     */
    public ArrayList<String> set(int index, ArrayList<String> point) throws NumberFormatException, ArrayIndexOutOfBoundsException {
        checkData(point, getDimensions());
        return super.set(index, point);
    }

    private int checkData(ArrayList<String> point, int old_dim) throws NumberFormatException {
        int new_dim = point.size();
        if (old_dim < 0)
            old_dim = new_dim;   // if the array is still empty, any size is good size
        if (old_dim != new_dim)
            throw new ArrayIndexOutOfBoundsException("Point inserted differs in dimension: found " + new_dim + ", requested " + old_dim);
        for (int i = 0; i < point.size(); i++)
            if (!parser.isValid(point.get(i), i))
                throw new NumberFormatException("The point added with value \"" + point.get(i) + "\" and index " + i + " is not valid with parser " + parser.getClass().getName());
        return old_dim;
    }
}
