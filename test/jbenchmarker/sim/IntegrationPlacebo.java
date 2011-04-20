/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.sim;

import jbenchmarker.core.MergeAlgorithm;
import java.util.Iterator;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.trace.TraceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationPlacebo {
    @Test
    public void testG1Run() throws Exception {
        System.out.println("Integration test with placebo");
        Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
        int uop = 0, cop = 0;
        while (trace.hasNext()) {
            TraceOperation top = trace.next();
            uop++;
            cop += top.getRange();
        }
        CausalDispatcher cd = new CausalDispatcher(new PlaceboFactory());
        trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
        cd.run(trace);
        assertEquals(uop, cd.getMemUsed().size());
        assertEquals(uop, cd.replicaGenerationTimes().size());
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(cop, m.getExecTime().size());
        }
    }
}
