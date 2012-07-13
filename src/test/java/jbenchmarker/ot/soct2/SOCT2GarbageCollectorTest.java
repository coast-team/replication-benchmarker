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
package jbenchmarker.ot.soct2;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.simulator.CausalSimulator;
import crdt.simulator.IncorrectTraceException;
import java.io.IOException;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.ot.otset.AddWinTransformation;
import jbenchmarker.ot.otset.OTSet;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFTransformations;
import jbenchmarker.trace.TraceGenerator;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class SOCT2GarbageCollectorTest {
    
    public SOCT2GarbageCollectorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of garbage collecting method, of class SOCT2GarbageCollector.
     */
    @Test
    public void testGC() throws PreconditionException {
        SOCT2GarbageCollector instance = new SOCT2GarbageCollector(1, 2);
        OTAlgorithm soct2Algorithm = new SOCT2(new AddWinTransformation(), instance);
        OTSet set1 = new OTSet(soct2Algorithm, 1), 
                set2 = new OTSet(soct2Algorithm, 2);
        
        CRDTMessage op1 = set1.add(1),
                op2 = set2.add(2);
        
        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());        
        
        set1.applyRemote(op2);
        
        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());

        set2.applyRemote(op1);
        
        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        CRDTMessage op3 = set1.add(3),
                op4 = set2.add(4);        

        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set1.applyRemote(op4);

        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set2.applyRemote(op3);

        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());
    }

    /**
     * Test of preemptive garbage collecting method.
     */
    @Test
    public void testPGC() throws PreconditionException {
        GarbageCollector instance = new PreemptiveGarbageCollector(1, 1);
        OTAlgorithm soct2Algorithm = new SOCT2(new AddWinTransformation(), instance);
        OTSet set1 = new OTSet(soct2Algorithm, 1), 
                set2 = new OTSet(soct2Algorithm, 2),
                set3 = new OTSet(soct2Algorithm, 3);
        
        CRDTMessage op1 = set1.add(1),
                op2 = set2.add(1);
        
        assertEquals(0, set1.getOtAlgo().getLog().getSize());
        assertEquals(0, set2.getOtAlgo().getLog().getSize());        
        
        set1.applyRemote(op2);
        
        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(0, set2.getOtAlgo().getLog().getSize());

        set2.applyRemote(op1);
        
        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set3.applyRemote(op1);
        
        CRDTMessage op3 = set1.add(3),
                op4 = set2.add(4),
                op5 = set3.remove(1);
        
        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set1.applyRemote(op4);

        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set2.applyRemote(op3);

        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());
        
        set1.applyRemote(op5);

        assertEquals(4, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());
        
        assertTrue(set1.contains(1));
    }
    
    protected void assertConsistentViews(CausalSimulator cd) {
        String referenceView = null;
        for (final CRDT replica : cd.getReplicas().values()) {
            final String view = ((MergeAlgorithm) replica).lookup();
            if (referenceView == null) {
                referenceView = view;
            } else {
                assertEquals(referenceView, view);
            }
        }
        assertNotNull(referenceView);
    }
        
    TTFTransformations ttf = new TTFTransformations();
    SOCT2Log logs[] = { 
        new SOCT2Log(ttf), 
        new SOCT2LogOptimizedLast(ttf),
        new SOCT2LogOptimizedPlace(ttf), 
//        new SOCT2LogOptimizedPlaceAndLast(ttf),
        };
    GarbageCollector gcs[] = { 
//        null, 
//        new SOCT2GarbageCollector(0), 
//        new PreemptiveGarbageCollector(1, 1), 
        new PreemptiveGarbageCollector(20),
    };
    String traces[] = {
        "../../traces/xml/exemple.xml", 
//        "../../traces/xml/G1.xml", 
//        "../../traces/xml/G2.xml", 
//        "../../traces/xml/G3.xml",
//        "../../traces/xml/Serie.xml",
    };
            
    @Test
    public void testBigOne() throws Exception {
        for (SOCT2Log l : logs) {
            for (GarbageCollector g : gcs) {
                for (String t : traces) {
                    System.out.println(l.getClass().getCanonicalName() + " | " + (g == null ? "No gc" : g.getClass().getCanonicalName()) + " | " + t);                   
                    CausalSimulator cd = new CausalSimulator
                            (new TTFMergeAlgorithm(new TTFDocument(), 0,
                        new SOCT2<TTFOperation>(l, g)));
                    cd.run(TraceGenerator.traceFromXML(t, 1), false);
                    assertConsistentViews(cd);
                }
            }
       }
    }
}
