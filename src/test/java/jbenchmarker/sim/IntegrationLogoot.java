/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.sim;

import org.junit.Ignore;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.logoot.LogootFactory;
import java.util.Iterator;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.trace.TraceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationLogoot {
    @Test
    public void testLogootExempleRun() throws Exception {
        System.out.println("Integration test with logoot");
        Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/exemple.xml", 1);
        OldCausalDispatcher cd = new OldCausalDispatcher(new LogootFactory());

        cd.run(trace);
        String r = "Salut Monsieurjour MehdiFin";
        assertEquals(r, cd.getReplicas().get(0).getDoc().view());
        assertEquals(r, cd.getReplicas().get(2).getDoc().view());
        assertEquals(r, cd.getReplicas().get(4).getDoc().view());
    }
    
//    @Ignore
    @Test
    public void testLogootG1Run() throws Exception {
        Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
        OldCausalDispatcher cd = new OldCausalDispatcher(new LogootFactory());

        cd.run(trace);
        String r = cd.getReplicas().get(0).getDoc().view();
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(r, m.getDoc().view());
        }
    }
    
//    @Ignore
    @Test
    public void testLogootG2Run() throws Exception {
        Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G2.xml", 1);
        OldCausalDispatcher cd = new OldCausalDispatcher(new LogootFactory());

        cd.run(trace);
        String r = cd.getReplicas().get(0).getDoc().view();
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(r, m.getDoc().view());
        }
    }
    
//    @Ignore
    @Test
    public void testLogootG3Run() throws Exception {
        Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G3.xml", 1);
        OldCausalDispatcher cd = new OldCausalDispatcher(new LogootFactory());

        cd.run(trace);
        String r = cd.getReplicas().get(0).getDoc().view();
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(r, m.getDoc().view());
        }
    }
    
//    @Ignore
    @Test
    public void testLogootSerieRun() throws Exception {
        Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/Serie.xml", 1);
        OldCausalDispatcher cd = new OldCausalDispatcher(new LogootFactory());

        cd.run(trace);
        String r = cd.getReplicas().get(0).getDoc().view();
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(r, m.getDoc().view());
        }
    }
    
    //    @Ignore
    @Test
    public void testLogootRandom() throws Exception {
        Iterator<TraceOperation> trace = new RandomTrace(4200, RandomTrace.FLAT, new StandardOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
        OldCausalDispatcher cd = new OldCausalDispatcher(new LogootFactory());

        cd.run(trace);
        String r = cd.getReplicas().get(0).getDoc().view();
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(r, m.getDoc().view());
        }     
    }
}
