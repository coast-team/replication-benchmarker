/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.simulator;

import crdt.CRDT;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.StandardDiffProfile;
import crdt.simulator.random.StandardSeqOpProfile;
import java.io.IOException;
import jbenchmarker.abt.ABTFactory;
import jbenchmarker.factories.*;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2LogOptimizedPlace;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFTransformations;
import jbenchmarker.factories.TreedocFactory;
import jbenchmarker.factories.WootFactories.WootFactory;
import jbenchmarker.factories.WootFactories.WootHFactory;
import jbenchmarker.factories.WootFactories.WootOFactory;
import jbenchmarker.ot.ttf.update.TTFUMergeAlgorithm;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class CausalDispatcherSequenceTest {

    Factory s[] = {
        new LogootFactory(),
        new LogootListFactory.ByteList(),
        new LogootListFactory.ShortList(),
        new LogootListFactory.IntList(),
        new LogootBinaryFactory(),
        new NCBLogootFactory(),
        new MuFactories.UpdateFactory<String>(),
        new MuFactories.MoveFactory<String>(),
        new TreedocFactory(),
        new WootFactory(),
        new WootOFactory(),
        new WootHFactory(),
        new RGAFactory(),
        new TTFFactories.WithoutGCFactory(),
        new TTFFactories.WithGC10(),
        new TTFFactories.WithLL_PGC(),
        new TTFMergeAlgorithm(new TTFDocument(), 0, new SOCT2(new SOCT2LogOptimizedPlace(new TTFTransformations()), null)),
        new TTFUMergeAlgorithm(0),       
        new TTFUFactories.NoUpdate(),
        new TTFUFactories.Update(),
        new TTFUFactories.DelWins(),
    };
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
    static final OperationProfile diffopp = new StandardDiffProfile(0.7, 0.75, 0.90, 30, 20.0, 30, 10.0);
    static final OperationProfile seqopp = new StandardSeqOpProfile(0.8, 0.1, 40, 5.0);
    static final OperationProfile uopp = new StandardSeqOpProfile(0.8, 0, 1, 0);

//    @Ignore
    @Test
    public void stress() throws PreconditionException, IncorrectTraceException, IOException {
//        Factory f = new TTFFactories.WithGC3();
//        Factory f = new TTFUMergeAlgorithm(0);        
//        Factory f = new TTFFactories.WithoutGCFactory();
        Factory f = new TTFUFactories.DelWins();
        CausalDispatcherSetsAndTreesTest.testRunX(f, 20, 100, 10, StandardSeqOpProfile.WITH_UPDATE);
    }

    @Test
    public void testRunSequencesOneCharacter() throws IncorrectTraceException, PreconditionException, IOException {
        String msg = "";
        for (Factory<CRDT> sf : s) {
            try {
                CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, uopp);
            } catch (AssertionError e) {
                msg = "### " + sf.getClass().getName() + "\n" + msg;
            } catch (RuntimeException e) {
                msg = "!!! " + sf.getClass().getName() + " : " + e.getClass().getName() + "\n" + msg;
            }
        }
        if (!msg.isEmpty()) {
            fail(msg);
        }
    }

    @Test
    public void testRunSequences() throws IncorrectTraceException, PreconditionException, IOException {
        String msg = "";
        for (Factory<CRDT> sf : s) {
            try {
                CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, seqopp);
            } catch (AssertionError e) {
                msg = "### " + sf.getClass().getName() + "\n" + msg;
            } catch (RuntimeException e) {
                msg = "!!! " + sf.getClass().getName() + " : " + e.getClass().getName() + "\n" + msg;
            }
        }
        if (!msg.isEmpty()) {
            fail(msg);
        }
    }

    @Test
    public void testRunDiffs() throws IncorrectTraceException, PreconditionException, IOException {
        String msg = "";
        for (Factory<CRDT> sf : s) {
            try {
                CausalDispatcherSetsAndTreesTest.testRun(sf, 1000, 10, diffopp);
            } catch (AssertionError e) {
                msg = "### " + sf.getClass().getName() + "\n" + msg;
            } catch (RuntimeException e) {
                msg = "!!! " + sf.getClass().getName() + " : " + e.getClass().getName() + "\n" + msg;
            }
        }
        if (!msg.isEmpty()) {
            fail(msg);
        }
    }

    @Test
    public void testLogootUpdate() throws IncorrectTraceException, PreconditionException, IOException {
        CausalDispatcherSetsAndTreesTest.testRun((Factory) new LogootListFactory.ShortList<String>(), 1000, 20, StandardDiffProfile.SMALL);
    }
}
