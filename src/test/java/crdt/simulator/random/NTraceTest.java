/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package crdt.simulator.random;

import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.util.Enumeration;
import jbenchmarker.core.LocalOperation;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class NTraceTest {
   
    class TraceMock implements Trace {

        String title;
        int nb;

        public TraceMock(String title, int nb) {
            this.title = title;
            this.nb = nb;
        }

        @Override
        public Enumeration<TraceOperation> enumeration() {
            return new Enumeration<TraceOperation>() {
                int j = nb;

                @Override
                public boolean hasMoreElements() {
                    return j > 0;
                }

                @Override
                public TraceOperation nextElement() {
                    j--;
                    return new TraceOperation() {
                        @Override
                        public LocalOperation getOperation() {
                            return null;
                        }

                        @Override
                        public String toString() {
                            return title;
                        }
                    };
                }
            };
        }
    }

    public NTraceTest() {
    }

    @Test
    public void troisdeuxunzero() {
        NTrace trace = new NTrace(new TraceMock("trois", 3), new TraceMock("deux", 2), new TraceMock("un", 1), new TraceMock("Ignition", 0));
        Enumeration<TraceOperation> enu = trace.enumeration();
        String output = "";
        while (enu.hasMoreElements()) {
            output += enu.nextElement().toString();
        }
        assertEquals("troistroistroisdeuxdeuxun",output);

    }
     @Test
    public void troisdeuxun() {
        NTrace trace = new NTrace(new TraceMock("trois", 3), new TraceMock("deux", 2), new TraceMock("un", 1));
        Enumeration<TraceOperation> enu = trace.enumeration();
        String output = "";
        while (enu.hasMoreElements()) {
            output += enu.nextElement().toString();
        }
        assertEquals("troistroistroisdeuxdeuxun",output);

    }
}
