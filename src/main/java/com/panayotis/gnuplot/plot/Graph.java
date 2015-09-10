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

package com.panayotis.gnuplot.plot;

import com.panayotis.gnuplot.layout.LayoutMetrics;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graph objects are parts of a multi-plot drawing. Each graph contains other
 * plots which share the same axis. All gnuplot objects have at least one graph
 * object.
 * <p>
 * For single plots, better have a look at Plot objects and GNUPlot.addPlot()
 * command
 *
 * @author teras
 */
public class Graph extends ArrayList<Plot> {

    protected static final String NL = System.getProperty("line.separator");
    private HashMap<String, Axis> axis;
    private LayoutMetrics metrics;

    /**
     * Create a new graph object
     */
    public Graph() {
        axis = new HashMap<String, Axis>();
        axis.put("x", new Axis("x"));
        axis.put("y", new Axis("y"));
        axis.put("z", new Axis("z"));
        metrics = null;
    }

    /**
     * Get one of the available Axis, in order to set some parameters on it.
     *
     * @param axisname The name of the Axis. It is usually "x", "y", "z"
     * @return The desired Axis
     */
    public Axis getAxis(String axisname) {
        if (axisname == null)
            return null;
        return axis.get(axisname.toLowerCase());
    }

    /**
     * Add a new plot to this plot group. At least one plot is needed to produce
     * visual results.
     *
     * @param plot The given plot.
     */
    public void addPlot(Plot plot) {
        if (plot != null)
            add(plot);
    }

    /**
     * Get gnuplot commands for this graph.
     *
     * @param bf
     */
    void retrieveData(StringBuilder bf) {
        /*
         * Do not append anything, if this graph is empty
         */
        if (size() == 0)
            return;

        /*
         * Set various axis parameters
         */
        for (Axis ax : axis.values())
            ax.appendProperties(bf);

        /*
         * Create data plots
         */
        bf.append(getPlotCommand());    // Use the corresponding plot command
        /*
         * Add plot definitions
         */
        for (Plot p : this) {
            bf.append(' ');
            p.retrieveDefinition(bf);
            bf.append(',');
        }
        bf.deleteCharAt(bf.length() - 1).append(NL);
        /*
         * Add plot data (if any)
         */
        for (Plot p : this)
            p.retrieveData(bf);
    }

    /**
     * Set the position and size of thie graph object, relative to a 0,0-1,1
     * page
     *
     * @param x horizontal position
     * @param y vertical position
     * @param width width of this graph
     * @param height of this graph
     */
    public void setMetrics(float x, float y, float width, float height) {
        metrics = new LayoutMetrics(x, y, width, height);
    }

    /**
     * Get the positioning and size of this graph object
     *
     * @return The metrics of this object
     */
    public LayoutMetrics getMetrics() {
        return metrics;
    }

    /**
     * Get the actual gnuplot command to initiate the plot.
     *
     * @return This method always returns "plot"
     */
    protected String getPlotCommand() {
        return "plot";
    }
}
