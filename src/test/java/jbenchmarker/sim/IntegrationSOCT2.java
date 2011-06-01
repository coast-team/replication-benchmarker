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
