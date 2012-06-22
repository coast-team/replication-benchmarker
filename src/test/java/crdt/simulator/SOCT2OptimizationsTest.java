/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import java.io.IOException;
import jbenchmarker.ot.soct2.*;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFTransformations;
import org.junit.*;
import static crdt.simulator.CausalDispatcherSetsAndTreesTest.*;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.StandardSeqOpProfile;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.ot.soct2.SOCT2LogOptimizedLast;
import jbenchmarker.trace.TraceGenerator;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class SOCT2OptimizationsTest {
    TTFTransformations ttf = new TTFTransformations();
    static final OperationProfile seqopp = new StandardSeqOpProfile(0.8, 0.1, 40, 5.0);
    
    @Test
    public void testRunBasic() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2Log(ttf), null));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }
    
    @Test
    public void testRunOLL() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedLast(ttf), null));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }

    @Test
    public void testRunOLP() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedPlace(ttf), null));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }

    @Test
    public void testRunOLLP() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), null));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }
    
    @Test
    public void testRunGC() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2Log(ttf), new SOCT2GarbageCollector(10)));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }
    
    @Test
    public void testRunOLL_GC() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedLast(ttf), new SOCT2GarbageCollector(10)));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    } 
    
    @Test
    public void testRunOLP_GC() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedPlace(ttf), new SOCT2GarbageCollector(10)));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }

    @Test
    public void testRunOLLP_GC() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), new SOCT2GarbageCollector(10)));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }
    
    @Test
    public void testRunPGC() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2Log(ttf), new PreemptiveGarbageCollector(10, 10)));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }
    
    @Test
    public void testRunOLL_PGC() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedLast(ttf), new PreemptiveGarbageCollector(10, 10)));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    } 
    
    @Test
    public void testRunOLP_PGC() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedPlace(ttf), new PreemptiveGarbageCollector(10, 10)));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }

    @Test
    public void testRunOLLP_PGC() throws IncorrectTraceException, PreconditionException, IOException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), new PreemptiveGarbageCollector(10, 10)));
        CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
    }
    
    @Test 
    public void testBlock() throws PreconditionException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                        new SOCT2(new SOCT2Log(ttf), new PreemptiveGarbageCollector(20)));
        MergeAlgorithm r1 = (MergeAlgorithm) sf.create(), r2 = (MergeAlgorithm) sf.create();
        r1.setReplicaNumber(0); r2.setReplicaNumber(1);
       
        CRDTMessage m1 = r1.insert(0, "a"), m2 = r2.insert(0, "bcdefghijklmnopqrstuvwxyz");
        r1.applyRemote(m2); r2.applyRemote(m1);
        
        assertEquals(r1.lookup(), r2.lookup());      
    }
}
