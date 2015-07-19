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

import com.panayotis.gnuplot.PropertiesHolder;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Smooth;

/**
 * This is the base class of all plot objects. It is used to define the basic
 * characteristics of plot handling, such as definition and styles
 *
 * @author teras
 */
public abstract class AbstractPlot extends PropertiesHolder implements Plot {

    private static int last_id = 0;
    private String definition = "";
    private PlotStyle style = null;
    private Smooth smooth = null;

    /**
     * General purpose constructor of a generic plot object
     */
    public AbstractPlot() {
        super(" ", "");
        setTitle("Datafile " + (++last_id));
    }

    /**
     * Set the plot definition for this plot object. It is the part that the
     * plot command parses.
     *
     * @param definition The string representing the plot definition.
     */
    protected void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * Retrieve the definition of this Plot object. If styles or smoothing
     * engines were used, the appropriate arguments are appended
     *
     * @param buf The buffer to store the data set.
     */
    public void retrieveDefinition(StringBuilder buf) {
        buf.append(definition);
        appendProperties(buf);
        if (smooth != null)
            buf.append(smooth.toString());
        if (style != null)
            style.appendProperties(buf);
    }

    /**
     * Set how this plot will be presented. Provide information about colors and
     * sizes, in accordance with the selected terminal. Give "null" if you want
     * to turn off this feature.
     *
     * @param style The style to use
     */
    public void setPlotStyle(PlotStyle style) {
        this.style = style;
    }

    /**
     * Retrieve the style of this Plot command
     *
     * @return The style being used
     */
    public PlotStyle getPlotStyle() {
        if (style == null)
            style = new PlotStyle();
        return style;
    }

    /**
     * Define if this plot should be smoothed.
     *
     * @param smooth The smooth definition. Give "null" if you want to turn off
     * this feature.
     */
    public void setSmooth(Smooth smooth) {
        this.smooth = smooth;
    }

    /**
     * A convenient method to set the title of this plot
     *
     * @param title The title to use for this plot
     */
    public final void setTitle(String title) {
        set("title", "'" + title + "'");
    }
}
