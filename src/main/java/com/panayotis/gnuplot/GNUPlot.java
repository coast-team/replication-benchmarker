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
 * Created on 12 Οκτώβριος 2007, 3:07 μμ
 */

package com.panayotis.gnuplot;

import com.panayotis.gnuplot.layout.GraphLayout;
import com.panayotis.gnuplot.plot.Axis;
import com.panayotis.gnuplot.plot.Graph;
import com.panayotis.gnuplot.plot.Page;
import com.panayotis.gnuplot.plot.Plot;
import com.panayotis.gnuplot.terminal.DefaultTerminal;
import com.panayotis.gnuplot.terminal.GNUPlotTerminal;
import com.panayotis.gnuplot.utils.Debug;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the main class of JavaPlot. It is the cental point to create a
 * gnuplot. Typically the user needs to create a new instance of this object and
 * add the desired plots.<p>
 * It also provides some convenient methods in order to set various parameters.
 * <p>
 * This object is not serializable, use GNUPlotParameters instead.
 *
 * @author teras
 * @see com.panayotis.gnuplot.JavaPlot
 * @see #addPlot(Plot)
 */
public class GNUPlot {

    private static final long serialVersionUID = GNUPlot.serialVersionUID;
    /**
     * GNUPlot parameters. Here we store all gnuplot information.
     */
    private GNUPlotParameters param;
    /**
     * The GNUPlot parameter to use
     */
    private transient GNUPlotTerminal term;
    /**
     * The GNUPlotExec to use.
     */
    private transient GNUPlotExec exec;
    private static transient Debug dbg = new Debug();

    /**
     * Create a new instance of gnuplot, using the default parameters
     *
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. Typically at this case there is
     * need to use a constructor which defines the gnuplot path.
     * @see #GNUPlot(String)
     * @see #GNUPlot(GNUPlotParameters,String,GNUPlotTerminal,boolean)
     */
    public GNUPlot() throws GNUPlotException {
        this(null, null, null, false);
    }

    /**
     * Create a new instance of gnuplot, using the default parameters. Use this
     * method if you want to specifically define that the default plot is
     * Graph3D
     *
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. Typically at this case there is
     * need to use a constructor which defines the gnuplot path.
     * @param isGraph3D true if the default plot is Graph3D
     * @see #GNUPlot(String)
     * @see #GNUPlot(GNUPlotParameters,String,GNUPlotTerminal,boolean)
     */
    public GNUPlot(boolean isGraph3D) throws GNUPlotException {
        this(null, null, null, isGraph3D);
    }

    /**
     * Create a new instance of gnuplot, with a given set of parameters.
     *
     * @see #GNUPlot(String)
     * @see #GNUPlot(GNUPlotParameters,String,GNUPlotTerminal,boolean)
     * @param par Use this set of parameters, instead of a default one.
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. Typically at this case there is
     * need to use a constructor which defines the gnuplot path.
     */
    public GNUPlot(GNUPlotParameters par) throws GNUPlotException {
        this(par, null, null, false);
    }

    /**
     * Create a new instance of gnuplot, with a given path for gnuplot. This
     * constructor is useful if the automatic path search for gnuplot is not
     * fruitful, or the user wants to point to a specific gnuplot executable.
     *
     * @param gnuplotpath The pathname of the gnuplot executable. If this
     * parameter is set to null, use the default path.
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. It means that the provided path
     * for gnuplot is not valid.
     */
    public GNUPlot(String gnuplotpath) throws GNUPlotException {
        this(null, gnuplotpath, null, false);
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
     */
    public GNUPlot(String gnuplotpath, boolean isGraph3D) throws GNUPlotException {
        this(null, gnuplotpath, null, isGraph3D);
    }

    /**
     * Create a new instance of gnuplot, with given parameters and given path
     * for gnuplot.
     * <p>
     * This constructor is useful if the user wants to fine tune eny aspect of
     * GNUPlot object, and especially if there is need to define a priori the
     * output terminal.
     * <p>
     * Any parameters set to null, produce the default parameters.
     *
     * @param par GNUPlot parameters to use. These parameters encapsulate the
     * whole gnuplot variables, including data sets.
     * @param gnuplotpath The pathname of the gnuplot executable. If this
     * parameter is set to null, use the default path.
     * @param term The gnuplot terminal to use
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. It means that the provided path
     * for gnuplot is not valid.
     */
    public GNUPlot(GNUPlotParameters par, String gnuplotpath, GNUPlotTerminal term) throws GNUPlotException {
        this(par, gnuplotpath, term, false);
    }

    /**
     * Create a new instance of gnuplot, with given parameters and given path
     * for gnuplot.
     * <p>
     * This constructor is useful if the user wants to fine tune eny aspect of
     * GNUPlot object, and especially if there is need to define a priori the
     * output terminal.
     * <p>
     * Any parameters set to null, produce the default parameters.
     *
     * @param par GNUPlot parameters to use. These parameters encapsulate the
     * whole gnuplot variables, including data sets.
     * @param gnuplotpath The pathname of the gnuplot executable. If this
     * parameter is set to null, use the default path.
     * @param term The gnuplot terminal to use
     * @param isGraph3D true, if this is a Graph3D object
     * @throws com.panayotis.gnuplot.GNUPlotException If the gnuplot executable
     * is not found, this exception is thrown. It means that the provided path
     * for gnuplot is not valid.
     */
    public GNUPlot(GNUPlotParameters par, String gnuplotpath, GNUPlotTerminal term, boolean isGraph3D) throws GNUPlotException {
        if (par == null)
            par = new GNUPlotParameters(isGraph3D);
        this.param = par;

        if (term == null)
            term = new DefaultTerminal();
        this.term = term;

        try {
            exec = new GNUPlotExec(gnuplotpath);
        } catch (IOException e) {
            String msg = e.getMessage();
            if (gnuplotpath == null)
                msg += " Please provide gnuplot path to the constructor of GNUPlot.";
            throw new GNUPlotException(msg);
        }
    }

    /**
     * Set various GNUPlot parameters. All parameters added here will be used in
     * the form of "set key value"
     *
     * @param key The key to use for this gnuplot
     * @param value The value of this key
     */
    public void set(String key, String value) {
        param.set(key, value);
    }

    /**
     * Use this method to get a reference to the plot axis, in order to set
     * various parameters.
     *
     * @param axisname The name of the axis. This typically is "x", "y", "z".
     * @return The requested Axis, or null if axis is not found
     */
    public Axis getAxis(String axisname) {
        return param.getAxis(axisname);
    }

    /**
     * Add a new Plot
     *
     * @param plot The plot to add to the list of plots.
     * @see com.panayotis.gnuplot.plot.Plot
     */
    public void addPlot(Plot plot) {
        if (plot == null)
            return;
        param.addPlot(plot);
    }

    /**
     * Add a defined graph to this GNUPlot object.
     *
     * @param gr Graph object to be added
     * @see #newGraph()
     */
    public void addGraph(Graph gr) {
        param.addGraph(gr);
    }

    /**
     * Add a new Graph object. This method is used to create a multiplot graph.
     * Every "plot" command corresponds to a different Graph object. In order to
     * draw to a new plot gnuplot object, create a new page.
     *
     * @see #newGraph3D()
     */
    public void newGraph() {
        param.newGraph();
    }

    /**
     * Add a new Graph3D object. This method is used to create a multiplot
     * graph. Every "splot" command corresponds to a different Graph object. In
     * order to draw to a new plot gnuplot object, create a new page.
     *
     * @see #newGraph()
     */
    public void newGraph3D() {
        param.newGraph3D();
    }

    /**
     * Set the title of all graph objects, in multiplot environment.
     *
     * @param title The title to use
     */
    public void setMultiTitle(String title) {
        param.setMultiTitle(title);
    }

    /**
     * Get a list of the (default) plots used in this set. This method is a way
     * to enumerate the plots already inserted, epspecially if a plot is added
     * on the fly.
     *
     * @return An array of stored plots.
     */
    public ArrayList<Plot> getPlots() {
        return param.getPlots();
    }

    /**
     * Get a Page containing all Graphs. This method is used for example in
     * order to get a list of graphs already inserted, especially if a graph is
     * automatically added
     *
     * @return An array of stored Graphs
     */
    public Page getPage() {
        return param.getPage();
    }

    /**
     * Get the current layout of this plot object
     *
     * @return The used layout
     */
    public GraphLayout getLayout() {
        return param.getLayout();
    }

    /**
     * Perform the actual action of plotting. Use the current parameters and
     * terminal, and perform a plot. If an error occured, an exception is thrown
     *
     * @throws com.panayotis.gnuplot.GNUPlotException This exception is thrown
     * if an error occured. Use the Debug object to dump information about this
     * error.
     */
    public void plot() throws GNUPlotException {
        exec.plot(param, term);
    }

    /**
     * Retrieves the command which will actually send to gnuplot, if we perform
     * a plot with the already defined parameters to the selected terminal. <br>
     * This method is used for debugging purposes.
     *
     * @return The commands to send to the gnuplot executable
     */
    public String getCommands() {
        return exec.getCommands(param, term);
    }

//    public void splot() throws GNUPlotException {
//        exec.splot(param, term);
//    }
    /**
     * Set the desired path for gnuplot executable.
     *
     * @param path Filename of gnuplot executable
     * @throws java.io.IOException gnuplot is not found, or not valid
     */
    public void setGNUPlotPath(String path) throws IOException {
        exec.setGNUPlotPath(path);
    }

    /**
     * Retrieve the file path of gnuplot
     *
     * @return The gnuplot file path
     */
    public String getGNUPlotPath() {
        return exec.getGNUPlotPath();
    }

    /**
     * Set all terminals to be persistent. Thus, after executing plot command,
     * the graph window stays open and does not disappear automatically.
     *
     * @param ispersist whether the terminal window should be persistent
     */
    public void setPersist(boolean ispersist) {
        exec.setPersist(ispersist);
    }

    /**
     * Set gnuplot parameters to another set of parameters.
     *
     * @param parameters The new GNUPlot parameters.
     */
    public void setParameters(GNUPlotParameters parameters) {
        if (param == null)
            return;
        param = parameters;
    }

    /**
     * Ge the actual gnuplot parameters. This method is useful if the developer
     * wants to have access to lower level GNUPlotparameter methods.
     *
     * @return Object having all information on how to make the plot.
     */
    public GNUPlotParameters getParameters() {
        return param;
    }

    /**
     * Change gnuplot terminal. Use this method to make gnuplot draw to another
     * terminal than the default
     *
     * @param term The terminal to use
     */
    public void setTerminal(GNUPlotTerminal term) {
        if (term == null)
            return;
        this.term = term;
    }

    /**
     * Get the current used terminal
     *
     * @return The used terminal
     */
    public GNUPlotTerminal getTerminal() {
        return term;
    }

    /**
     * Get the specific GNUPlot Debug object
     *
     * @return The Debug object
     */
    public static Debug getDebugger() {
        return dbg;
    }

    /**
     * Execute gnuplot commands before any kind of initialization. This method
     * together with getPostInit() is useful to add basic commands to gnuplot
     * exetutable, if the library does not support the desired functionality
     *
     * @return Array of pre-init commands
     */
    public ArrayList<String> getPreInit() {
        return param.getPreInit();
    }

    /**
     * Execute gnuplot commands before any kind of initialization. This method
     * together with getPostInit() is useful to add basic commands to gnuplot
     * exetutable, if the library does not support the desired functionality
     *
     * @return Array of pre-init commands
     */
    public ArrayList<String> getPostInit() {
        return param.getPostInit();
    }
}
