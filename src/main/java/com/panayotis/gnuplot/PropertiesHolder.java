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
 * Created on October 19, 2007, 2:20 AM
 */

package com.panayotis.gnuplot;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This object is a data placeholder of various text-based parameters, used in
 * JavaPlot graph objects.<br> It is possible to retrieve all it's data, as long
 * a valid key-value pair is present.
 *
 * @author teras
 */
public class PropertiesHolder extends HashMap<String, String> {

    protected static final String NL = System.getProperty("line.separator");
    private String prefix;
    private String suffix;

    /**
     * Creates a new instance of PropertiesHolder with default prefix and suffix
     * values.<br> The prefix in this case is the token "set " and the suffix is
     * the newline character.
     */
    public PropertiesHolder() {
        this("set ", NL);
    }

    /**
     * Creates a new instance of PropertiesHolder with given prefix and suffix
     * tokens.
     *
     * @param prefix The prefix to use
     * @param suffix The suffix to use.
     */
    public PropertiesHolder(String prefix, String suffix) {
        super();
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * Add a specific key-value pair to this object.
     *
     * @param key The key to use
     * @param value The value of the specified parameter.<br> If value is null,
     * then this key will be removed.
     */
    public void set(String key, String value) {
        if (key != null)
            if (value == null)
                unset(key);
            else
                put(key, value);
    }

    /**
     * Set a specific key to this object, without a value
     *
     * @param key The key to add to this object
     */
    public void set(String key) {
        set(key, "");
    }

    /**
     * Remove a key from this object
     *
     * @param key The key to be removed
     */
    public void unset(String key) {
        remove(key);
    }

    /**
     * Retrieve the list of the stored key-value pairs in this object. Every
     * pair will be prefixed with "prefix" and suffixed with "suffix". Between
     * key and value will be a space character, if and only if the value is
     * present.
     *
     * @param bf The StringBuilder to store the representation of this object.
     */
    public void appendProperties(StringBuilder bf) {
        Object val;
        for (Entry e : entrySet()) {
            bf.append(prefix).append(e.getKey());
            val = e.getValue();
            if (val != null && (!val.equals("")))
                bf.append(' ').append(e.getValue());
            bf.append(suffix);
        }
    }
}
