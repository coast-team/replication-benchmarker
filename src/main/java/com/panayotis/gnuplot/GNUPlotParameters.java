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
 * Created on October 13, 2007, 4:02 PM
 */

package com.panayotis.gnuplot;

import com.panayotis.gnuplot.layout.GraphLayout;
import com.panayotis.gnuplot.plot.Axis;
import com.panayotis.gnuplot.plot.Graph;
import com.panayotis.gnuplot.plot.Graph3D;
import com.panayotis.gnuplot.plot.Page;
import com.panayotis.gnuplot.plot.Plot;
import com.panayotis.gnuplot.terminal.GNUPlotTerminal;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This is a placeholder for the parameters used to create the actual plot.
 *
 * @author teras
 */
public class GNUPlotParameters extends PropertiesHolder implements Serializable {

    private Page page;
    private int defaultgraph;
    private ArrayList<String> preinit;
    private ArrayList<String> postinit;

    /**
     * Create a new plot with the default parameters
     */
    public GNUPlotParameters() {
    }

    /**
     * Create a new plot with the default parameters
     *
     * @param isGraph3D Whether this plot is a Graph3D
     */
    public GNUPlotParameters(boolean isGraph3D) {
        page = new Page(isGraph3D);
        defaultgraph = 0;

        preinit = new ArrayList<String>();
        postinit = new ArrayList<String>();
    }

    /**
     * Get one of the available Axis from default Graph, in order to set some
     * parameters on it.
     *
     * @param axisname The name of the Axis. It is usually "x", "y", "z"
     * @return The desired Axis
     */
    public Axis getAxis(String axisname) {
        return page.get(defaultgraph).getAxis(axisname);
    }

    /**
     * This list is used to add special commands to gnuplot, before the
     * automatically generated from this library. It is a convenient method to
     * send unsupported commands to gnuplot at the beginning of the program.
     *
     * @return The list of the initialization commands
     */
    public ArrayList<String> getPreInit() {
        return preinit;
    }

    /**
     * This list is used to add special commands to gnuplot, after the
     * automatically generated from this library. It is a convenient method to
     * send unsupported commands to gnuplot at the end of the program, just
     * before the final plot command.
     *
     * @return he list of the post initialization commands
     */
    public ArrayList<String> getPostInit() {
        return postinit;
    }

    /**
     * Add a new plot to the default plot group. At least one plot is needed to
     * produce visual results.
     *
     * @param plot The given plot.
     */
    public void addPlot(Plot plot) {
        page.get(defaultgraph).add(plot);
    }

    /**
     * Add a new Graph object. This method is used to create a multiplot graph.
     * Every "plot" command corresponds to a different Graph object. In order to
     * draw to a new plot gnuplot object, create a new page.
     *
     * @see #newGraph3D()
     */
    public void newGraph() {
        addGraph(new Graph());
    }

    /**
     * Add a new Graph3D object. This method is used to create a multiplot
     * graph. Every "splot" command corresponds to a different Graph object. In
     * order to draw to a new plot gnuplot object, create a new page.
     *
     * @see #newGraph()
     */
    public void newGraph3D() {
        addGraph(new Graph3D());
    }

    /**
     * Add a defined graph.
     *
     * @param gr Graph object to be added
     * @see #newGraph()
     */
    public void addGraph(Graph gr) {
        page.add(gr);
        defaultgraph = page.size() - 1;
    }

    /**
     * Set the title of all graph objects, in multiplot environment.
     *
     * @param title The title to use
     */
    public void setMultiTitle(String title) {
        page.setTitle(title);
    }

    /**
     * Get the current layout of this plot object
     *
     * @return The used layout
     */
    GraphLayout getLayout() {
        return page.getLayout();
    }

    /**
     * Retrieve the whole page object, defining the various graph plots
     *
     * @return the Page object which holds all plots
     */
    public Page getPage() {
        return page;
    }

    /**
     * Get the list of the stored plots from default graph
     *
     * @return List of Plot objects
     */
    public ArrayList<Plot> getPlots() {
        return page.get(defaultgraph);
    }

    /**
     * Get the actual GNUPlot commands. This method is used to construct the
     * gnuplot program
     *
     * @param term The terminal to use
     * @return The GNUPlot program
     */
    String getPlotCommands(GNUPlotTerminal term) {
        StringBuilder bf = new StringBuilder();

        /*
         * First execute pre-init commands
         */
        for (String com : preinit)
            bf.append(com).append(NL);

        /*
         * Gather various "set" parameters
         */
        appendProperties(bf);

        /*
         * Set Terminal (and it's parameters)
         */
        if (!term.getType().equals(""))
            bf.append("set term ").append(term.getType()).append(NL);
        if (!term.getOutputFile().equals(""))
            bf.append("set output \'").append(term.getOutputFile()).append("\'").append(NL);


        /*
         * We are almost ready. Before executing the actual plot command, issue
         * the post-init commands
         */
        for (String com : postinit)
            bf.append(com).append(NL);

        /*
         * Append various plots
         */
        page.getGNUPlotPage(bf);

        /*
         * Finish!
         */
        bf.append("quit").append(NL);

        return bf.toString();
    }
}
