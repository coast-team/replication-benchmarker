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
 * Created on October 21, 2007, 6:58 PM
 */

package com.panayotis.gnuplot.terminal;

import com.panayotis.gnuplot.PropertiesHolder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is the base class of all special terminals found in JavaPlot. It
 * provides support for terminal types and consumes unwanted gnuplot output.
 *
 * @author teras
 */
public abstract class ExpandableTerminal extends PropertiesHolder implements GNUPlotTerminal {

    private String type;

    /**
     * Create a new Terminal with a given type
     *
     * @param type The terminal to use
     */
    public ExpandableTerminal(String type) {
        super(" ", "");
        if (type == null)
            type = "unknown";
        this.type = type;
    }

    /**
     * Get the type of this terminal
     *
     * @return String representation of this terminal type
     */
    public String getType() {
        StringBuilder buf = new StringBuilder();
        buf.append(type);
        appendProperties(buf);
        return buf.toString();
    }

    /**
     * This method only consumes gnuplot stdout input stream. It is performed to
     * prevent a possible thread lockup.
     *
     * @param stdout The output of GNUPlot. It will be consumed.
     */
    @SuppressWarnings("empty-statement")
    public String processOutput(InputStream stdout) {
        byte[] buffer = new byte[1000];
        BufferedInputStream in = new BufferedInputStream(stdout);
        try {
            while (in.read(buffer) >= 0);   // consume stream
            in.close();
        } catch (IOException ex) {
            return "I/O error while processing gnuplot output: " + ex.getMessage();
        }
        return null;
    }
}
