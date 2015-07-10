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

import collect.Node;
import collect.OrderedNode;
import crdt.CRDT;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.counter.CommutativeCounterSet;
import crdt.set.counter.ConvergentCounterSet;
import crdt.set.lastwriterwins.CommutativeLwwSet;
import crdt.set.lastwriterwins.ConvergentLwwSet;
import crdt.set.observedremove.CommutativeOrSet;
import crdt.set.observedremove.ConvergentOrSet;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.SetOperationProfile;
import crdt.simulator.random.StandardDiffProfile;
import crdt.simulator.random.StandardSeqOpProfile;
import crdt.simulator.random.TreeOperationProfile;
import crdt.tree.wordtree.WordPolicy;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.*;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import jbenchmarker.factories.LogootListFactory;
import jbenchmarker.factories.RGAFactory;
import jbenchmarker.factories.RgaSFactory;
import jbenchmarker.ot.otset.AddWinTransformation;
import jbenchmarker.ot.otset.DelWinTransformation;
import jbenchmarker.ot.otset.OTSet;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2GarbageCollector;
import jbenchmarker.rgasplit.RgaSMerge;

import org.junit.AfterClass;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class CausalDispatcherSetsAndTreesTest {

    Factory policy[] = {new WordSkip(), new WordReappear(), new WordRoot(), new WordCompact(),
        new WordIncrementalSkip(), new WordIncrementalReappear(),
        new WordIncrementalRoot(), new WordIncrementalCompact(), new WordIncrementalSkipOpti()};
    Factory set[] = {new CommutativeCounterSet(), new ConvergentCounterSet(),
        new CommutativeLwwSet(), new ConvergentLwwSet(),
        new CommutativeOrSet(), new ConvergentOrSet(),
        new OTSet(new SOCT2(new AddWinTransformation(), null)),
        new OTSet(new SOCT2(new DelWinTransformation(), null)),
        new OTSet(new SOCT2(new AddWinTransformation(), new SOCT2GarbageCollector(5))),
        new OTSet(new SOCT2(new DelWinTransformation(), new SOCT2GarbageCollector(5)))};
    //Vector<LinkedList<TimeBench>> result = new Vector<LinkedList<TimeBench>>();
    int scale = 100;

    public CausalDispatcherSetsAndTreesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    public static CRDT testRun(Factory<CRDT> factory, int times, int duration, OperationProfile opp) throws PreconditionException, IncorrectTraceException, IOException {
        return testRunX(factory, times, duration, 20, opp);
    }

    public static CRDT testRunX(Factory<CRDT> factory, int times, int duration, int nbreplica, OperationProfile opp) throws PreconditionException, IncorrectTraceException, IOException {
        CausalSimulator cd = new CausalSimulator(factory, false, 0, false);
        for (int t = 0; t < times; ++t) {
            //System.out.println(t);
            cd.reset(); 
            cd.run(new RandomTrace(duration, RandomTrace.FLAT, opp, 0.9, 1, 1, nbreplica));
			//System.out.println(cd.getlTime());

			//result.add(cd.getlTime());
            //System.out.println(factory + "\nlocal : " + cd.getLocalAvg() + "   --- remote : " + cd.getRemoteAvg());
            Object l = null;
            int i = 0;
            for (Entry<Integer, CRDT> r : cd.replicas.entrySet()) {
                if (l == null) {
                    l = r.getValue().lookup();
                    i = r.getKey();
                } else {
                    if (//(l instanceof OrderedNode && !((OrderedNode) l).same((OrderedNode) r.getValue().lookup())) ||  
                            !l.equals(r.getValue().lookup())) {
                        StringBuilder sf = new StringBuilder();
                        sf.append("**** ").append(r.getValue().toString()).append('\n');
                        for (Entry<Integer, CRDT> e : cd.replicas.entrySet()) {
                            int j = e.getKey();
                            sf.append("Local ").append(j).append(":\n").append(cd.getHistory().get(j)).
                                    append("\nMessages ").append(":\n").append(cd.getGenHistory().get(j)).
                                    append("\nResult ").append(":\n").append(cd.replicas.get(j).lookup()).
                                    //                                append("\nSet ").append(":\n").append(((WordTree) cd.replicas.get(j)).words.lookup()).
                                    append("\n---------\n");
                        }
                        //cd.view.affiche();

                        fail(" ** A= " + i + " ** B= " + r.getKey() + "\n" + sf.toString());
                    }
                }
            }
        }
        return cd.replicas.values().iterator().next();
    }
    static final int vocabularySize = 100;

    final static OperationProfile setopp = new SetOperationProfile(1) {

        @Override
        public Object nextElement() {
            return (int) (Math.random() * vocabularySize);
        }

        @Override
        public Object nextElement(Object elem) {
            return ((Integer) elem + 1) % vocabularySize;
        }

        @Override
        public boolean full(Set s) {
            return s.size() == vocabularySize;
        }
    };

    final static OperationProfile treeop = new TreeOperationProfile(0.70) {

        @Override
        public Object nextElement() {
            return (int) (Math.random() * vocabularySize);
        }

        @Override
        public Object nextElement(Object elem) {
            return ((Integer) elem + 1) % vocabularySize;
        }

        @Override
        public boolean full(Node n) {
            return n.getChildrenNumber() == vocabularySize;
        }
    };

    @Ignore
    @Test
    public void testRunSets() throws IncorrectTraceException, PreconditionException, IOException {

        //        long l = 0, r = 0, nl = 0, nr = 0;
        //        for (int i = 0; i < 50; i++) {
        //            CausalSimulator cd = testRun(new CommutativeCounterSet(), 200, 20, seqopp);
        //            l += cd.getLocalTimeSum(); r += cd.getRemoteSum(); nl += cd.getNbLocalOp(); nr += cd.getNbRemote();
        //        }
        //        System.out.println("local : " + (l/nl) + "\nRemote : " + (r/nr));
        for (Factory<CRDT> sf : set) {
            testRun(sf, 2000, 10, setopp);
        }
    }

    //bug
    @Ignore
    @Test
    public void testRunLogootTree() throws IncorrectTraceException, PreconditionException, IOException {
        while (true) {
            for (Factory<CRDT> sf : set) {
                for (Factory<WordPolicy> pf : policy) {
                    System.out.println(new WordTree(sf, pf));
                    testRun(new WordTree(sf, pf), 500, 20, treeop);
                }
            }
        }
    }

    @Ignore
    @Test
    public void stressSet() throws PreconditionException, IncorrectTraceException, IOException {
        testRun(set[7], 10000, 5, setopp);
    }

    @Ignore
    @Test
    public void stressTree() throws PreconditionException, IncorrectTraceException, IOException {
        testRun(new WordTree(set[7], policy[1]), 5000, 10, treeop);
    }

    /* void writeResult() throws IOException {
     System.out.println("Write files");
     PrintWriter Filelocal, Fileremote, Fileloop;
     Long timeLocal = 0L, timeRemote = 0L, timeLoop = 0L;

     Filelocal = new PrintWriter(new BufferedWriter(new FileWriter("../../File.local.data")));
     Fileremote = new PrintWriter(new BufferedWriter(new FileWriter("../../File.remote.data")));
     Fileloop = new PrintWriter(new BufferedWriter(new FileWriter("../../File.loop.data")));

     //retreive size of operation
     int size = result.get(0).size();
     for (int i = 1; i < result.size(); i++) {
     LinkedList<TimeBench> currentBench = result.get(i);
     if(size> currentBench.size())
     size = currentBench.size();
     }

     int k= 0, nb =0;
     for (int i = 0; i < size; i++) {
     for (int j = 0; j < result.size(); j++) {
     TimeBench tB = result.get(j).get(i);
     timeLocal += tB.getTimeLocal();
     timeRemote += tB.getTimeRemote();
     timeLoop += tB.getTimeLoop();
     if( k == scale)
     {
     k =0;
     nb++;
     Filelocal.println(nb +"\t"+timeLocal / scale);
     Fileremote.println(nb +"\t"+timeRemote / scale);
     Fileloop.println(nb +"\t"+timeLoop / scale);
     timeLocal =0L;
     timeRemote =0L;
     timeLoop = 0L;
     }
     else
     k++;
     }
     //            System.out.println(j); //size of operation
     }
     Filelocal.close();
     Fileremote.close();
     Fileloop.close();
     }*/
}
