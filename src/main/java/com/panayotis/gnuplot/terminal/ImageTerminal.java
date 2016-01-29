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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * This terminal is able to process gnuplot output as an image. The image
 * produced can be used by any Java object which is able to handle BufferedImage
 *
 * @author teras
 */
public class ImageTerminal extends FileTerminal {

    private BufferedImage img;

    /**
     * Create a new image terminal, and use PNG as it's backend
     */
    public ImageTerminal() {
        super("png");
    }

    /**
     * Read the produced image from gnuplot standard output
     *
     * @param stdout The gnuplot output stream
     * @return The error definition, if any
     */
    public String processOutput(InputStream stdout) {
        try {
            img = ImageIO.read(stdout);
        } catch (IOException ex) {
            return "Unable to create PNG image: " + ex.getMessage();
        }
        if (img == null)
            return "Unable to create image from the gnuplot output. Null image created.";
        return null;
    }

    /**
     * Get a handler of the produced image by this terminal
     *
     * @return the plot image
     */
    public BufferedImage getImage() {
        return img;
    }
}
