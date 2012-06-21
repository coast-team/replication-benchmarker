/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import crdt.CRDT;
import crdt.Factory;
import crdt.PreconditionException;
import jbenchmarker.factories.LogootFactory;
import jbenchmarker.factories.TTFFactories;
import jbenchmarker.factories.WootFactories.WootFactory;
import jbenchmarker.factories.WootFactories.WootHFactory;
import jbenchmarker.factories.WootFactories.WootOFactory;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.StandardSeqOpProfile;
import java.io.IOException;
import jbenchmarker.core.Operation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2GarbageCollector;
import jbenchmarker.ot.soct2.SOCT2LogOptimizedPlace;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFTransformations;
import jbenchmarker.factories.TreedocFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class CausalDispatcherSequenceTest {

    Factory s[] = { new LogootFactory(), new TreedocFactory(), // new jbenchmarker.treedoc.list.TreedocFactory(),
        new WootFactory(), new WootOFactory(), new WootHFactory(), // new ABTFactory(),
        new TTFFactories.WithoutGC(), 
        new TTFFactories.WithGC10(),
        
//        new TTFMergeAlgorithm(new TTFDocument(), 0,
//                                new SOCT2(new SOCT2LogOptimizedPlace(new TTFTransformations()), null)),
                                    //new SOCT2GarbageCollector(10))),
    //    new SOCT2Factory(), new RGAFactory()
    };
    
    
    //Vector<LinkedList<TimeBench>> result = new Vector<LinkedList<TimeBench>>();
    int scale = 100;
    

   
    public CausalDispatcherSequenceTest() {
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
    
//    @Ignore
    @Test
    public void stress() throws PreconditionException, IncorrectTraceException, IOException {
//        Factory f = new TTFFactories.WithGC3();
        Factory f = new TTFMergeAlgorithm(new TTFDocument(), 0,
                                new SOCT2(new SOCT2LogOptimizedPlace(new TTFTransformations()), null));
        for (int i = 0; i < 500; ++i) {
//            System.out.println(" i :" + i++);
            CausalDispatcherSetsAndTreesTest.testRun(f, 10, 3, uopp);           
        }
    }
    
    @Test
    public void testRunSequencesOneCharacter() throws IncorrectTraceException, PreconditionException, IOException {
        
        for (Factory<CRDT> sf : s) {
            CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, uopp);
        }
    }
    
    @Test
    public void testRunSequences() throws IncorrectTraceException, PreconditionException, IOException {
        
        for (Factory<CRDT> sf : s) {
            CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
        }
    }
}
