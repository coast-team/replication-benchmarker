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
                        new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), new PreemptiveGarbageCollector(20)));
        MergeAlgorithm r1 = (MergeAlgorithm) sf.create(), r2 = (MergeAlgorithm) sf.create();
        r1.setReplicaNumber(0); r2.setReplicaNumber(1);
       
        CRDTMessage m1 = r1.insert(0, "a").clone(), 
                m2 = r2.insert(0, "bcdefghijklmnopqrstuvwxyz").clone();
        r1.applyRemote(m2); 
        r2.applyRemote(m1);
        
        assertEquals(r1.lookup(), r2.lookup());      
    }
        
    @Test 
    public void testBlock2() throws PreconditionException {
        Factory sf = new TTFMergeAlgorithm(new TTFDocument(),
                        new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), new PreemptiveGarbageCollector(20)));
        MergeAlgorithm r1 = (MergeAlgorithm) sf.create(), r2 = (MergeAlgorithm) sf.create();
        r1.setReplicaNumber(1); r2.setReplicaNumber(0);
       
        CRDTMessage m1 = r1.insert(0, "a").clone(),
                m2 = r2.insert(0, "bcdefghijklmnopqrstuvwxyz").clone();
        r1.applyRemote(m2); 
        r2.applyRemote(m1);
        
        assertEquals(r1.lookup(), r2.lookup());      
    }
}
