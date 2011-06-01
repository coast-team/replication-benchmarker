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
