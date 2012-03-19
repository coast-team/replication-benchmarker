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

import crdt.CRDT;
import crdt.simulator.Trace;
import crdt.simulator.CausalSimulator;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.core.SequenceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationPlacebo {
    @Test
    public void testG1Run() throws Exception {
//        System.out.println("Integration test with placebo");
//        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
//        int uop = 0, cop = 0;
//        while (trace.hasNext()) {
//            SequenceOperation top = trace.next();
//            uop++;
//            cop += top.getRange();
//        }
//        CausalSimulator cd = new CausalSimulator(new PlaceboFactory());
//        trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
//        cd.run(trace);
//        assertEquals(uop, cd.getMemUsed().size());
//        assertEquals(uop, cd.replicaGenerationTimes().size());
//        for (CRDT m : cd.getReplicas().values()) {
   //        assertEquals(cop, m.getExecTime().size());
//        }
    }
}
