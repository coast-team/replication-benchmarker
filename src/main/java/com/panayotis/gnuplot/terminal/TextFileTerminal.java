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
 * Created on October 23, 2007, 10:46 AM
 */

package com.panayotis.gnuplot.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Base class of all terminals with output in text format.
 *
 * @author teras
 */
public class TextFileTerminal extends FileTerminal {

    /**
     *
     */
    protected String output = "";

    /**
     * Creates a new instance of TextFileTerminal. The output will be parsed by
     * JavaPlot and stored in a String, since it is expected to be a text and
     * not binary data.
     *
     * @param type the terminal type
     */
    public TextFileTerminal(String type) {
        this(type, "");
    }

    /**
     * Creates a new instance of TextFileTerminal and output to a specific file
     *
     * @param type the terminal type
     * @param filename the file to save output to
     */
    public TextFileTerminal(String type, String filename) {
        super(type, filename);
    }

    /**
     * Process output of this terminal. Since this is a text terminal, the
     * output will be stored in a String
     *
     * @param stdout The gnuplot output stream
     * @return Return the error as a String, if an error occured.
     */
    @Override
    public String processOutput(InputStream stdout) {
        StringBuilder out = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(stdout));
        String line;
        try {
            while ((line = in.readLine()) != null)
                out.append(line);
            in.close();
        } catch (IOException ex) {
            return "I/O error while processing gnuplot output: " + ex.getMessage();
        }
        output = out.toString();
        return null;
    }

    /**
     * Retrieve the String with the output of the last gnuplot command
     *
     * @return The String with gnuplot output
     */
    public String getTextOutput() {
        return output;
    }
}
