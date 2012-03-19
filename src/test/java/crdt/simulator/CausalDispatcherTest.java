/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import collect.Node;
import java.io.IOException;
import java.util.Set;
import crdt.tree.wordtree.policy.WordIncrementalSkip;
import crdt.tree.wordtree.policy.WordIncrementalCompact;
import crdt.tree.wordtree.policy.WordIncrementalRoot;
import crdt.tree.wordtree.policy.WordIncrementalSkipOpti;
import org.junit.Ignore;
import crdt.tree.wordtree.policy.WordIncrementalReappear;
import crdt.tree.wordtree.WordPolicy;
import crdt.tree.wordtree.policy.WordCompact;
import crdt.tree.wordtree.policy.WordRoot;
import crdt.tree.wordtree.policy.WordReappear;
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
import crdt.simulator.random.TreeOperationProfile;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.WordSkip;
import java.util.Map.Entry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class CausalDispatcherTest {

    Factory p[] = {new WordSkip(), new WordReappear(), new WordRoot(), new WordCompact(),
        new WordIncrementalSkip(), new WordIncrementalReappear(),
        new WordIncrementalRoot(), new WordIncrementalCompact(), new WordIncrementalSkipOpti()};
    Factory s[] = {new CommutativeCounterSet(), new ConvergentCounterSet(),
        new CommutativeLwwSet(), new ConvergentLwwSet(),
        new CommutativeOrSet(), new ConvergentOrSet()};
    //Vector<LinkedList<TimeBench>> result = new Vector<LinkedList<TimeBench>>();
    int scale = 100;

    public CausalDispatcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    static CausalSimulator testRun(Factory<CRDT> factory, int duration, int rn, OperationProfile opp) throws PreconditionException, IncorrectTraceException {
        CausalSimulator cd = new CausalSimulator(factory);
        cd.run(new RandomTrace(duration, RandomTrace.FLAT, opp, 0.2, 10, 3, rn));
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
                if (!l.equals(r.getValue().lookup())) {
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
        return cd;
    }
    static final int vocabularySize = 100;
    
    final static OperationProfile seqopp = new SetOperationProfile(0.70) {

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
    
//    @Ignore
    @Test
    public void testRunSets() throws IncorrectTraceException, PreconditionException {

//        long l = 0, r = 0, nl = 0, nr = 0;
//        for (int i = 0; i < 50; i++) {
//            CausalSimulator cd = testRun(new CommutativeCounterSet(), 200, 20, seqopp);
//            l += cd.getLocalSum(); r += cd.getRemoteSum(); nl += cd.getNbLocal(); nr += cd.getNbRemote();
//        }
//        System.out.println("local : " + (l/nl) + "\nRemote : " + (r/nr));

        for (Factory<CRDT> sf : s) {
            testRun(sf, 200, 20, seqopp);
        }
    }
    
//    @Ignore
    @Test
    public void testRunWord() throws IncorrectTraceException, PreconditionException {
        for (Factory<CRDT> sf : s) {
            for (Factory<WordPolicy> pf : p) {
                //System.out.println(new WordTree(sf, pf));
                testRun(new WordTree(sf, pf), 200, 5, treeop);
            }
       }
    }

    @Ignore
    @Test
    public void stress() throws PreconditionException, IncorrectTraceException, IOException {
        int i = 0;
        while (i <1) {
            //System.out.println(" i :"+i++);
            i++;
            testRun(new WordTree(s[1], p[1]), 15000, 4, treeop);
            
        }
        //writeResult();
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
