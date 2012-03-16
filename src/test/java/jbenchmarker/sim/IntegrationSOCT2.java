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
import java.util.Iterator;
import java.util.logging.Logger;
import jbenchmarker.ot.SOCT2Factory;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.trace.SequenceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationSOCT2 {
   
    //@Ignore   // 231,986 s  on rev 105
    @Test
    public void testSOCT2RunG1() throws Exception {
        Iterator<SequenceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);         
        OldCausalDispatcher cd = new OldCausalDispatcher(new SOCT2Factory());

        long startTime = System.currentTimeMillis();
        cd.run(trace);
        long endTime = System.currentTimeMillis();
         
        Logger.getLogger(getClass().getCanonicalName()).info("computation time: "+(endTime-startTime)+" ms");
        
        String r = cd.getReplicas().get(0).getDoc().view();
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(r, m.getDoc().view());
        }
    }
    
    @Test
    public void testSOCT2Random() throws Exception {
        Iterator<SequenceOperation> trace = new RandomTrace(5000, RandomTrace.FLAT, new StandardOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
        OldCausalDispatcher cd = new OldCausalDispatcher(new SOCT2Factory());

        cd.run(trace);
        String r = cd.getReplicas().get(0).getDoc().view();
        for (MergeAlgorithm m : cd.getReplicas().values()) {
            assertEquals(r, m.getDoc().view());
        }     
    }
}
