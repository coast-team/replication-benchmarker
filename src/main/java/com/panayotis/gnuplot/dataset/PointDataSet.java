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
 * Created on October 15, 2007, 2:10 AM
 */

package com.panayotis.gnuplot.dataset;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Store data sets in a dynamic Generics ArrayList of Points. Prefer this object
 * instead of ArrayDataSet if you plan to alter the points of this data sets
 * afterwards its creation.<p>
 * If your data are not only numerical, consider using a GenericDataSet instead.
 *
 * @param N The precision of each point
 * @see com.panayotis.gnuplot.dataset.GenericDataSet
 * @author teras
 */
public class PointDataSet<N extends Number> extends ArrayList<Point<N>> implements DataSet {

    /**
     * Create an empty PointDataSet
     */
    public PointDataSet() {
        super();
    }

    /**
     * Create an empty PointDataSet with a specified initial capacity
     *
     * @param initial The initial capacity of this PointDataSet
     */
    public PointDataSet(int initial) {
        super(initial);
    }

    /**
     * Create a new PointDataSet from a previous collection of Points
     *
     * @param pts The collection of Points to use as a model
     * @throws java.lang.NumberFormatException If the given collection is not in
     * the correct format
     */
    public PointDataSet(Collection<? extends Point<N>> pts) throws NumberFormatException {
        super(pts);
        int length = size();
        int old_dim = getDimensions();
        for (int i = 0; i < length; i++)
            old_dim = checkDimension(get(i), old_dim);
    }

    private int checkDimension(Point<N> point, int old_dim) throws ArrayIndexOutOfBoundsException {
        int new_dim = point.getDimensions();
        if (old_dim < 0)
            old_dim = new_dim;   // if the array is still empty, any size is good size
        if (old_dim != new_dim)
            throw new ArrayIndexOutOfBoundsException("Point inserted differs in dimension: found " + new_dim + ", requested " + old_dim);
        return old_dim;
    }

    /**
     * Add a new point to this DataSet
     *
     * @param point The point to add to this DataSet
     * @return Whether the collection changed with this call
     * @throws java.lang.NumberFormatException If the given collection is not in
     * the correct format
     */
    public boolean add(Point<N> point) throws NumberFormatException {
        checkDimension(point, getDimensions());
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
    public void add(int index, Point<N> point) throws NumberFormatException {
        checkDimension(point, getDimensions());
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
    public boolean addAll(Collection<? extends Point<N>> pts) throws NumberFormatException {
        int old_dim = getDimensions();
        for (Point<N> p : pts)
            old_dim = checkDimension(p, old_dim);
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
    public boolean addAll(int index, Collection<? extends Point<N>> pts) throws NumberFormatException {
        int old_dim = getDimensions();
        for (Point<N> p : pts)
            old_dim = checkDimension(p, old_dim);
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
    public Point<N> set(int index, Point<N> point) throws NumberFormatException {
        checkDimension(point, getDimensions());
        return super.set(index, point);
    }

    /**
     * Add a new point to the data set, given the values for each dimension.
     *
     * @param coords a list of primitive data of the same type of this
     * collection. Could also be boxed variables too.
     */
    public void addPoint(N... coords) {
        add(new Point<N>(coords));
    }

    /**
     * Retrieve how many dimensions this dataset refers to.
     *
     * @return the number of dimensions
     * @see DataSet#getDimensions()
     */
    public int getDimensions() {
        if (size() == 0)
            return -1;
        return get(0).getDimensions();
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
        return get(point).get(dimension).toString();
    }

    /**
     * This is a convinient method to transform a statically defined primitive
     * array to PointDataSet object. Use this method if your oroginal data is in
     * a static primitive array but you want to take advantage of the
     * flexibility of PointDataSet, instead od ArrayDataSet.
     *
     * @param objclass The class of this PointDataSet. For example for Double
     * precision numbers, this parameter should be Double.class
     * @param array The array containing the primitive data
     * @return The produced PointDataSet of class objclass
     * @throws java.lang.ArrayStoreException If some misconfiguration is
     * performed on the provided array object
     */
    @SuppressWarnings("unchecked")
    public static final <N extends Number> PointDataSet<N> constructDataSet(Class<N> objclass, Object array) throws ArrayStoreException {
        int length, dim, cdim;
        int i, j;
        Object row, value;

        if (!array.getClass().isArray())
            throw new ArrayStoreException("The second argument of constructDataSet should be a two dimensional array.");

        length = Array.getLength(array);
        dim = -1;
        PointDataSet<N> points = new PointDataSet<N>(length);
        N[] buffer = null;

        for (i = 0; i < length; i++) {
            row = Array.get(array, i);
            if (!row.getClass().isArray())
                throw new ArrayStoreException("The second argument of constructDataSet is a one dimensional, instead of two dimensional, array.");
            cdim = Array.getLength(row);
            if (dim < 0) {
                dim = cdim;
                buffer = (N[]) Array.newInstance(Number.class, dim);
            }
            if (dim != cdim)
                throw new ArrayStoreException("Array has not consistent size, was " + dim + ", found " + cdim);
            for (j = 0; j < dim; j++) {
                value = Array.get(row, j);
                if (!value.getClass().equals(objclass))
                    throw new ArrayStoreException("Array item " + value + " is " + value.getClass().getName() + " and not " + objclass.getName());
                buffer[j] = (N) value;
            }
            points.quickadd(new Point<N>(buffer));
        }
        return points;
    }

    private void quickadd(Point<N> point) {
        super.add(point);
    }
}
