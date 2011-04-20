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

package jbenchmarker.sim;

import org.junit.Ignore;
import jbenchmarker.core.MergeAlgorithm;
import java.util.Iterator;
import java.util.logging.Logger;
import jbenchmarker.ot.SOCT2Factory;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.trace.TraceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationSOCT2 {
   
    //@Ignore   // 231,986 s  on rev 105
    @Test
    public void testSOCtestSOCT2ExempleRunT2G1Run() throws Exception {
        Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);         
        CausalDispatcher cd = new CausalDispatcher(new SOCT2Factory());

        long startTime = System.currentTimeMillis();
        cd.run(trace);
        long endTime = System.currentTimeMillis();
         
        Logger.getLogger(getClass().getCanonicalName()).info("computation time: "+(endTime-startTime)+" ms");
        
        String r = cd.getReplicas().get(0).getDoc().view();
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(r, m.getDoc().view());
        }
    }
}
