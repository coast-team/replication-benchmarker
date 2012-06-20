/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import crdt.CRDT;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.factories.LogootFactory;
import crdt.factories.TTFFactories;
import crdt.factories.WootFactories.WootFactory;
import crdt.factories.WootFactories.WootHFactory;
import crdt.factories.WootFactories.WootOFactory;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.StandardSeqOpProfile;
import java.io.IOException;
import jbenchmarker.treedoc.TreedocFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class CausalDispatcherTestSequence {

    Factory s[] = { new LogootFactory(), new TreedocFactory(), new jbenchmarker.treedoc.list.TreedocFactory(),
        new WootFactory(), new WootOFactory(), new WootHFactory(), // new ABTFactory(),
        new TTFFactories.WithoutGC(), 
        new TTFFactories.WithGC10(),
    //    new SOCT2Factory(), new RGAFactory()
    };
    
    
    //Vector<LinkedList<TimeBench>> result = new Vector<LinkedList<TimeBench>>();
    int scale = 100;
    

   
    public CausalDispatcherTestSequence() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    static final int vocabularySize = 100;
    static final OperationProfile seqopp = new StandardSeqOpProfile(0.8, 0.1, 40, 5.0);
    static final OperationProfile uopp = new StandardSeqOpProfile(0.8, 0, 1, 0);
    
    @Ignore
    @Test
    public void stress() throws PreconditionException, IncorrectTraceException, IOException {
        Factory f = new TTFFactories.WithGC3();
        for (int i = 0; i < 5000; ++i) {
//            System.out.println(" i :" + i++);
            CausalDispatcherTest.testRun(f, 10, 3, uopp);           
        }
    }
    
    @Test
    public void testRunSequencesOneCharacter() throws IncorrectTraceException, PreconditionException, IOException {
        
        for (Factory<CRDT> sf : s) {
            CausalDispatcherTest.testRun(sf, 1000, 10, uopp);
        }
    }
    
    @Test
    public void testRunSequences() throws IncorrectTraceException, PreconditionException, IOException {
        
        for (Factory<CRDT> sf : s) {
            CausalDispatcherTest.testRun(sf, 1000, 10, seqopp);
        }
    }
}
