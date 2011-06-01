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

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.abt.ABTFactory;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.trace.TraceOperation;

import org.junit.Test;

/**
*
* @author Roh
*/
public class IntegrationABT {
   @Test
   public void testABTExempleRun() throws Exception {
       System.out.println("Integration test with ABT");        
       Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/exemple.xml", 1);
       
       CausalDispatcher cd = new CausalDispatcher(new ABTFactory());

       cd.run(trace);
       String r = "Salut Monsieurjour MehdiFin";
       assertEquals(r, cd.getReplicas().get(0).getDoc().view());
       assertEquals(r, cd.getReplicas().get(2).getDoc().view());
       assertEquals(r, cd.getReplicas().get(4).getDoc().view());
   }
   
   // @Ignore
   @Test
   public void testABTG1Run() throws Exception {
       Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
       CausalDispatcher cd = new CausalDispatcher(new ABTFactory());

       cd.run(trace);
       String r = cd.getReplicas().get(0).getDoc().view();
       for (MergeAlgorithm m : cd.getReplicas().values()) {
           assertEquals(r, m.getDoc().view());
       }
   }
   
   @Test
   public void testABTG2Run() throws Exception {
       Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G2.xml", 1,16);
       CausalDispatcher cd = new CausalDispatcher(new ABTFactory());

       cd.run(trace);
       String r = cd.getReplicas().get(0).getDoc().view();
       for (MergeAlgorithm m : cd.getReplicas().values()) {
           assertEquals(r, m.getDoc().view());
       }
   }
   
   @Test
   public void testABTG3Run() throws Exception {
       Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/G3.xml", 1);
       CausalDispatcher cd = new CausalDispatcher(new ABTFactory());

       cd.run(trace);
       String r = cd.getReplicas().get(0).getDoc().view();
       for (MergeAlgorithm m : cd.getReplicas().values()) {
           assertEquals(r, m.getDoc().view());
       }
   }
   
   @Test
   public void testABTSerieRun() throws Exception {
       Iterator<TraceOperation> trace = TraceGenerator.traceFromXML("../../traces/xml/Serie.xml", 1);
       CausalDispatcher cd = new CausalDispatcher(new ABTFactory());

       cd.run(trace);
       String r = cd.getReplicas().get(0).getDoc().view();
       for (MergeAlgorithm m : cd.getReplicas().values()) {
           assertEquals(r, m.getDoc().view());
       }
   }
}