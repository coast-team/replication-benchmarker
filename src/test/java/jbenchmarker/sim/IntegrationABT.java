/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
import crdt.simulator.random.StandardSeqOpProfile;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import static org.junit.Assert.assertEquals;


import jbenchmarker.abt.ABTFactory;
import jbenchmarker.trace.TraceGenerator;

import org.junit.Test;

/**
*
* @author Roh
*/
public class IntegrationABT {
   @Test
   public void testABTExempleRun() throws Exception {
       System.out.println("Integration test with ABT");        
       Trace trace = TraceGenerator.traceFromXML("../../traces/xml/exemple.xml", 1);
       
       CausalSimulator cd = new CausalSimulator(new ABTFactory());

       cd.run(trace, false);
       String r = "Salut Monsieurjour MehdiFin";
       assertEquals(r, cd.getReplicas().get(0).lookup());
       assertEquals(r, cd.getReplicas().get(2).lookup());
       assertEquals(r, cd.getReplicas().get(4).lookup());
   }
   
   // @Ignore
   @Test
   public void testABTG1Run() throws Exception {
       Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
       CausalSimulator cd = new CausalSimulator(new ABTFactory());

       cd.run(trace, false);
       String r = (String) cd.getReplicas().get(0).lookup();
       for (CRDT m : cd.getReplicas().values()) {
    	   //System.out.println(m.getReplicaNumber()+"  "+m.lookup());
           assertEquals(r, m.lookup());
           
       }
   }
   
   @Test
   public void testABTG2Run() throws Exception {
       Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G2.xml", 1,16);
       CausalSimulator cd = new CausalSimulator(new ABTFactory());

       cd.run(trace, false);
       String r = (String) cd.getReplicas().get(0).lookup();
       for (CRDT m : cd.getReplicas().values()) {
           assertEquals(r, m.lookup());
       }
   }
   
   @Test
   public void testABTG3Run() throws Exception {
       Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G3.xml", 1);
       CausalSimulator cd = new CausalSimulator(new ABTFactory());

       cd.run(trace, false);
       String r = (String) cd.getReplicas().get(0).lookup();
       for (CRDT m : cd.getReplicas().values()) {
           assertEquals(r, m.lookup());
       }
   }
   
   @Test
   public void testABTSerieRun() throws Exception {
       Trace trace = TraceGenerator.traceFromXML("../../traces/xml/Serie.xml", 1);
       CausalSimulator cd = new CausalSimulator(new ABTFactory());

       cd.run(trace, false);
       String r = (String) cd.getReplicas().get(0).lookup();
       for (CRDT m : cd.getReplicas().values()) {
           assertEquals(r, m.lookup());
       }
   }
     
    //    @Ignore
    @Test
    public void testABTRandom() throws Exception {
        Trace trace = new RandomTrace(200, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
        CausalSimulator cd = new CausalSimulator(new ABTFactory());

        cd.run(trace, false);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }
}
