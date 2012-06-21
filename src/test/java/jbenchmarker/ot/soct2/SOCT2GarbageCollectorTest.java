/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import jbenchmarker.ot.otset.AddWinTransformation;
import jbenchmarker.ot.otset.OTSet;
import jbenchmarker.ot.ttf.TTFTransformations;
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
        
        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());

        set2.applyRemote(op1);
        
        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());

        CRDTMessage op3 = set1.add(3),
                op4 = set2.add(4);        

        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set1.applyRemote(op4);

        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set2.applyRemote(op3);

        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());
    }

    /**
     * Test of garbage collecting method, of class SOCT2GarbageCollector.
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
        
        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());        
        
        set1.applyRemote(op2);
        
        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());

        set2.applyRemote(op1);
        
        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());

        set3.applyRemote(op1);
        
        CRDTMessage op3 = set1.add(3),
                op4 = set2.add(4),
                op5 = set3.remove(1);
        
        assertEquals(2, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set1.applyRemote(op4);

        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(2, set2.getOtAlgo().getLog().getSize());

        set2.applyRemote(op3);

        assertEquals(1, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());
        
        set1.applyRemote(op5);

        assertEquals(3, set1.getOtAlgo().getLog().getSize());
        assertEquals(1, set2.getOtAlgo().getLog().getSize());
        
        assertTrue(set1.contains(1));
    }
}
