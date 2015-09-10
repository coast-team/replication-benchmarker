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
 * Created on October 17, 2007, 2:51 AM
 */

package com.panayotis.gnuplot.terminal;

/**
 * This Terminal supports file operations. The results of the gnuplot commands
 * can be stored in a file, if desired.
 *
 * @author teras
 */
public class FileTerminal extends ExpandableTerminal {

    private String filename;

    /**
     * Creates a new instance of FileTerminal and output to stadard out
     *
     * @param type The terminal type
     */
    public FileTerminal(String type) {
        this(type, "");
    }

    /**
     * Creates a new instance of FileTerminal and output to a specific file
     *
     * @param type The terminal type
     * @param filename e filename to use as an output for this terminal
     * @see #getOutputFile()
     */
    public FileTerminal(String type, String filename) {
        super(type);
        if (filename == null)
            filename = "";
        this.filename = filename;
    }

    /**
     * Retrieve the filename to use as an output for this terminal. If the
     * filename empty, then the output will be dumped to standard output, and
     * retrieved by JavaPlot
     *
     * @return If this parameter is not empty, the output filename
     */
    public String getOutputFile() {
        return filename;
    }
}
