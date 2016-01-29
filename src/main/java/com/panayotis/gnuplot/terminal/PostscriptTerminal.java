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
 * Created on October 16, 2007, 1:34 AM
 */

package com.panayotis.gnuplot.terminal;

/**
 * This terminal uses postscript as it's backend
 *
 * @author teras
 */
public class PostscriptTerminal extends TextFileTerminal {

    /**
     * Create a new instance of PostscriptTerminal. It is recommended to use
     * PostscriptTerminal(String filename) instead, since this constructor does
     * not produce any output file.
     */
    public PostscriptTerminal() {
        this("");
    }

    /**
     * Create a new Postscript terminal and save output to the specified file
     *
     * @param filename The filename of the output postscript file
     */
    public PostscriptTerminal(String filename) {
        super("postscript", filename);
        setColor(true);
        setEPS(true);
    }

    /**
     * Select if the output will be in EPS format or not
     *
     * @param eps If EPS mode will be used
     */
    public void setEPS(boolean eps) {
        if (eps)
            set("eps");
        else
            unset("eps");
    }

    /**
     * Select if the output will be color or not (monochrome)
     *
     * @param color If the ouput will be in color
     */
    public void setColor(boolean color) {
        if (color) {
            set("color");
            unset("monochrome");
        } else {
            set("monochrome");
            unset("color");
        }
    }
}
