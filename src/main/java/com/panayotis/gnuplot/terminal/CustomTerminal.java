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

package com.panayotis.gnuplot.terminal;

/**
 * This is a user specific terminal. The user in run-time defines what kind of
 * terminal wants. The output is not processed. If you want to process the
 * output, you might need to subclass this object and override
 * processOutput(InputStream stdout) method.
 *
 * @see GNUPlotTerminal#processOutput(java.io.InputStream)
 * @author teras
 */
public class CustomTerminal extends ExpandableTerminal {

    private String file;

    /**
     * Create a new custom terminal
     *
     * @param type The type of this terminal
     * @param file The filename to redirect output (if desired)
     */
    public CustomTerminal(String type, String file) {
        super(type);
        if (file == null)
            file = "";
        this.file = file;
    }

    /**
     * Retrieve the output filename
     *
     * @return The filename which this terminal will direct gnuplot output
     */
    public String getOutputFile() {
        return file;
    }
}
