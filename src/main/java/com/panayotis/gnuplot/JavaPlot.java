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
 * Created on October 19, 2007, 1:11 AM
 */

package com.panayotis.gnuplot;

import com.panayotis.gnuplot.dataset.DataSet;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.plot.FunctionPlot;
import com.panayotis.gnuplot.terminal.GNUPlotTerminal;

/**
 * A friendly wrapper of GNUPlot, able to set common plot parameters. If easy of
 * use is required, it is recommended to use this class instead of GNUPlot.
 *
 * <p>
 * This object is not serializable, use GNUPlotParameters instead.
 *
 * @author teras
 */
public class JavaPlot extends GNUPlot {

    /**
     * Create a new instance of JavaPlot, with the default parameters
     *
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. Typically at this case there is
     * need to use a constructor which defines the gnuplot path.
     * @see GNUPlot#GNUPlot()
     */
    public JavaPlot() throws GNUPlotException {
        super();
    }

    /**
     * Create a new instance of JavaPlot, using the default parameters. Use this
     * method if you want to specifically define that the default plot is
     * Graph3D
     *
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. Typically at this case there is
     * need to use a constructor which defines the gnuplot path.
     * @param isGraph3D true if the default plot is Graph3D
     * @see GNUPlot#GNUPlot(boolean)
     */
    public JavaPlot(boolean isGraph3D) throws GNUPlotException {
        super(isGraph3D);
    }

    /**
     * Create a new JavaPlot object with a given gnuplot path
     *
     * @param gnuplotpath
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. It means that the provided path
     * for gnuplot is not valid.
     * @see GNUPlot#GNUPlot(String)
     */
    public JavaPlot(String gnuplotpath) throws GNUPlotException {
        super(gnuplotpath);
    }

    /**
     * Create a new instance of gnuplot, with a given path for gnuplot. This
     * constructor is useful if the automatic path search for gnuplot is not
     * fruitful, or the user wants to point to a specific gnuplot executable.
     *
     * @param gnuplotpath The pathname of the gnuplot executable. If this
     * parameter is set to null, use the default path.
     * @param isGraph3D true if the default plot is Graph3D
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. It means that the provided path
     * for gnuplot is not valid.
     * @see GNUPlot#GNUPlot(String,boolean)
     */
    public JavaPlot(String gnuplotpath, boolean isGraph3D) throws GNUPlotException {
        super(gnuplotpath, isGraph3D);
    }

    /**
     * Create a new JavaPlot object with given parameters
     *
     * @param par
     * @throws com.panayotis.gnuplot.GNUPlotException
     * @see GNUPlot#GNUPlot(GNUPlotParameters)
     */
    public JavaPlot(GNUPlotParameters par) throws GNUPlotException {
        super(par);
    }

    /**
     * Create a new JavaPlot object with given parameters, gnuplot path and
     * terminal
     *
     * @param par
     * @param gnuplotpath
     * @param term
     * @throws com.panayotis.gnuplot.GNUPlotException
     * @see GNUPlot#GNUPlot(GNUPlotParameters,String,GNUPlotTerminal)
     */
    public JavaPlot(GNUPlotParameters par, String gnuplotpath, GNUPlotTerminal term) throws GNUPlotException {
        super(par, gnuplotpath, term);
    }

    /**
     * Create a new JavaPlot object with given parameters, gnuplot path terminal
     * ans isGraph3D
     *
     * @param par
     * @param gnuplotpath
     * @param term
     * @param isGraph3D
     * @throws com.panayotis.gnuplot.GNUPlotException
     * @see GNUPlot#GNUPlot(GNUPlotParameters,String,GNUPlotTerminal,boolean)
     */
    public JavaPlot(GNUPlotParameters par, String gnuplotpath, GNUPlotTerminal term, boolean isGraph3D) throws GNUPlotException {
        super(par, gnuplotpath, term, isGraph3D);
    }

    /**
     * Set the graph Title
     *
     * @param title Title of the graph
     */
    public void setTitle(String title) {
        setTitle(title, null, -1);
    }

    /**
     * Set the graph title and the title font
     *
     * @param title Title of the graph
     * @param font Font name of this title text
     * @param size Font size of this title text
     */
    public void setTitle(String title, String font, int size) {
        String fontname = "";
        if (font != null)
            fontname = " font '" + font + ((size > 1) ? "," + size : "") + "'";
        set("title", "'" + title + "'" + fontname);
    }

    /**
     *
     */
    public static enum Key {

        OFF, TOP_RIGHT, BOTTOM_RIGHT, TOP_LEFT, BOTTOM_LEFT, BELOW, OUTSIDE
    };

    /**
     *
     * @param position
     */
    public void setKey(Key position) {
        if (position == null)
            set("key", null);
        else
            set("key", position.name().replace('_', ' ').toLowerCase());
    }

    /**
     *
     * @param points
     */
    public void addPlot(double[][] points) {
        addPlot(new DataSetPlot(points));
    }

    /**
     *
     * @param points
     */
    public void addPlot(float[][] points) {
        addPlot(new DataSetPlot(points));
    }

    /**
     *
     * @param points
     */
    public void addPlot(int[][] points) {
        addPlot(new DataSetPlot(points));
    }

    /**
     *
     * @param points
     */
    public void addPlot(long[][] points) {
        addPlot(new DataSetPlot(points));
    }

    /**
     *
     * @param function
     */
    public void addPlot(String function) {
        addPlot(new FunctionPlot(function));
    }

    /**
     *
     * @param set
     */
    public void addPlot(DataSet set) {
        addPlot(new DataSetPlot(set));
    }
}
