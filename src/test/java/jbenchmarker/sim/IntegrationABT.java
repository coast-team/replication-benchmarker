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