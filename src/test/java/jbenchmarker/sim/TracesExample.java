/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.sim;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class TracesExample {

    public static enum Type {

        XML, JSON
    };
    public static final String prefix[] = {"traces/xml/", "traces/json/"};
    public static final String exampleTrace[][] = {
        {
            "exemple.xml",
            "G1.xml",
            "G2.xml",
            "G3.xml",
            "Serie.xml"},
        {
            "dirtyCS.db",
            "dirtyCSGerald3.db"
        }};

    public static String getExampleTraceXML(int i) throws JDOMException, IOException {
        return getExampleTrace(i, Type.XML);
    }

    public static String getExampleTrace(int i, Type type) throws JDOMException, IOException {
        return getExampleTrace(prefix[type.ordinal()] + exampleTrace[type.ordinal()][i]);
    }

    public static String getExampleTrace(String str) throws JDOMException, IOException {
        return TracesExample.class.getResource(str).getPath();
    }

    public static int getNbTrace(Type type) {
        return exampleTrace[type.ordinal()].length;
    }

    public static String getExampleTraceMatch(String exemplexml) throws JDOMException, IOException {
        Type type = Type.JSON;
        if (exemplexml.endsWith(".xml") || exemplexml.endsWith(".XML")) {
            type = Type.XML;
        }
        for (int i = 0; i < exampleTrace[type.ordinal()].length; i++) {
            if (exampleTrace[type.ordinal()][i].contains(exemplexml)) {
                return getExampleTrace(i, type);
            }
        }
        return null;
    }

    static public Iterable<String> getIterable(Type type) {
        return new IterableImpl(type);


    }

    static class IterableImpl implements Iterable<String> {

        Type type;

        public IterableImpl(Type type) {
            this.type = type;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator() {
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < exampleTrace[type.ordinal()].length;
                }

                @Override
                public Object next() {
                    try {
                        return getExampleTrace(i++, type);
                    } catch (JDOMException ex) {
                        Logger.getLogger(TracesExample.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(TracesExample.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };

        }
    };
}
