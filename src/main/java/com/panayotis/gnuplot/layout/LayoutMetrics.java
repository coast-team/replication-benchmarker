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

package com.panayotis.gnuplot.layout;

import java.io.Serializable;

/**
 * Container of the metrics for a specific graph
 *
 * @author teras
 */
public class LayoutMetrics implements Serializable {

    private float x, y, width, height;
    private float minsize = 0.001f;

    /**
     * Set default position, covering the whole screen
     */
    public LayoutMetrics() {
        this(0, 0, 1, 1);
    }

    /**
     * Set a specific position, in the area of 0,0-1,1 and with a minimum size
     *
     * @param x horizontal position
     * @param y vertical position
     * @param width width
     * @param height height
     */
    public LayoutMetrics(float x, float y, float width, float height) {
        if (width < 0)
            width = minsize;
        if (height < 0)
            height = minsize;
        if (width > 1)
            width = 1;
        if (height > 1)
            height = 1;

        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (x > 1)
            x = 1;
        if (y > 1)
            y = 1;

        float x2 = x + width;
        float y2 = y + height;
        if (x2 > 1) {
            x = 1 - minsize;
            width = minsize;
        }
        if (y2 > 1) {
            y = 1 - minsize;
            height = minsize;
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Get horizontal position
     *
     * @return horizontal position
     */
    public float getX() {
        return x;
    }

    /**
     * Get vertical position
     *
     * @return vertical position
     */
    public float getY() {
        return y;
    }

    /**
     * Get width
     *
     * @return width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Get height
     *
     * @return height
     */
    public float getHeight() {
        return height;
    }
}
