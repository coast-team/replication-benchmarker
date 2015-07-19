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
 * Created on 12 Οκτώβριος 2007, 4:07 μμ
 */

package com.panayotis.gnuplot.plot;

import com.panayotis.gnuplot.dataset.ArrayDataSet;
import com.panayotis.gnuplot.dataset.DataSet;
import com.panayotis.gnuplot.dataset.PointDataSet;

/**
 * This plot uses data sets as coordinates of the points to e displayed. The
 * user can provide data either statically (through the specialized constructors
 * with native base types) or with the more flexible generic object
 * PointDataSet.
 *
 * @author teras
 */
public class DataSetPlot extends AbstractPlot {

    private DataSet dataset;

    /**
     * Create a new data set with a default data set.
     */
    public DataSetPlot() {
        this(new PointDataSet());
    }

    /**
     * Create a new data set with the specified double-precision array as a data
     * set
     *
     * @param dataset A 2D double table with the data set
     */
    public DataSetPlot(double[][] dataset) {
        this(new ArrayDataSet(dataset));
    }

    /**
     * Create a new data set with the specified float-precision array as a data
     * set
     *
     * @param dataset A 2D float table with the data set
     */
    public DataSetPlot(float[][] dataset) {
        this(new ArrayDataSet(dataset));
    }

    /**
     * Create a new data set with the specified integer-precision array as a
     * data set
     *
     * @param dataset A 2D integer table with the data set
     */
    public DataSetPlot(int[][] dataset) {
        this(new ArrayDataSet(dataset));
    }

    /**
     * Create a new data set with the specified long-precision array as a data
     * set
     *
     * @param dataset A 2D long table with the data set
     */
    public DataSetPlot(long[][] dataset) {
        this(new ArrayDataSet(dataset));
    }

    /**
     * Create a new object with a specific data set
     *
     * @param dataset The data set to use
     */
    public DataSetPlot(DataSet dataset) {
        setDataSet(dataset);
        setDefinition("'-'");
    }

    /**
     * Retrieve the data set of this plot command. It is used internally by
     * JavaPlot library. Please do not use it.
     *
     * @param bf The buffer to store the data set
     */
    public void retrieveData(StringBuilder bf) {
        int i, j;
        int isize, jsize;

        if (dataset != null) {
            isize = dataset.size();
            jsize = dataset.getDimensions();
            for (i = 0; i < isize; i++) {
                for (j = 0; j < jsize; j++)
                    bf.append(dataset.getPointValue(i, j)).append(' ');
                bf.append(NL);
            }
        }
        bf.append("e").append(NL);
    }

    /**
     * Set the data set of this plot to the specified one
     *
     * @param set The data set to use
     */
    public final void setDataSet(DataSet set) {
        dataset = set;
    }

    /**
     * Retrieve the data set of this plot
     *
     * @return The data set of this plot
     */
    public DataSet getDataSet() {
        return dataset;
    }
}
