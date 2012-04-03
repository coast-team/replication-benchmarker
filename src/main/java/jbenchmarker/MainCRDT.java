/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker;

import collect.Node;
import java.io.FileWriter;
import java.io.IOException;
import crdt.tree.wordtree.policy.WordCompact;
import crdt.tree.wordtree.policy.WordRoot;
import crdt.tree.wordtree.policy.WordReappear;
import crdt.CRDT;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.observedremove.CommutativeOrSet;
import crdt.simulator.CausalSimulator;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.SetOperationProfile;
import crdt.simulator.random.TreeOperationProfile;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.WordSkip;
import crdt.simulator.IncorrectTraceException;
import crdt.simulator.Trace;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mehdi
 *
 * @param args scale
 *
 */
public class MainCRDT {

    static Factory p[] = {new WordSkip(), new WordReappear(), new WordRoot(), new WordCompact()};
    static Factory s[] = {new CommutativeOrSet()};
    //static Vector<LinkedList<TimeBench>> result = new Vector<LinkedList<TimeBench>>();
    //static Vector<Vector<LinkedList<TimeBench>>> setResult = new Vector();//vector of result
    static int maxReplic;
    static int nbExec;
    static int nbDuration;
    static double maxProba;
    static long maxDelay;
    static int dur = 1000;
    static double proba = 0.2;
    static int replic = 20;
    static long dly = 10;

    public static void main(String[] args) throws Exception {

        if (args.length != 5) {
            System.err.println("Arguments : Invalid");
            System.err.println("- Give Number execution ");
            System.err.println("- Give replica number ");
            System.err.println("- Give Max Duration ");
            System.err.println("- Give Max Proba ");
            System.err.println("- Give Max Delay ");
            System.exit(1);
        }

        nbExec = Integer.valueOf(args[0]);
        maxReplic = Integer.valueOf(args[1]);
        nbDuration = Integer.valueOf(args[2]);
        maxProba = Double.valueOf(args[3]);
        maxDelay = Long.valueOf(args[4]);

//        testRunSetsNbReplique();
//        testRunTreeNbReplique();

//        testRunSetsDuration();
//        testRunTreeDuration();
//
     /*   testRunSetsDelay();
        testRunTreeNbDelay();

        testRunSetsProba();
        testRunTreeProba();*/

    }

    static CausalSimulator testRun(Factory<CRDT> factory, int duration, int rn, OperationProfile opp, double prob, long delay) throws PreconditionException, IncorrectTraceException, IOException {
        CausalSimulator cd = new CausalSimulator(factory);
        Trace tr = new RandomTrace(duration, RandomTrace.FLAT, opp, prob, delay, 3, rn);

        cd.run(tr, false);
        //System.out.println(cd.getlTime());

        //result.add(cd.getlTime());

        Object l = null;
        int i = 0;
        for (Map.Entry<Integer, CRDT> r : cd.replicas.entrySet()) {
            if (l == null) {
                l = r.getValue().lookup();
                i = r.getKey();
            } else {
                if (!l.equals(r.getValue().lookup())) {
                    StringBuilder sf = new StringBuilder();
                    sf.append("**** ").append(r.getValue().toString()).append('\n');
                    for (Map.Entry<Integer, CRDT> e : cd.replicas.entrySet()) {
                        int j = e.getKey();
                        sf.append("Local ").append(j).append(":\n").append(cd.getHistory().get(j)).
                                append("\nMessages ").append(":\n").append(cd.getGenHistory().get(j)).
                                append("\nResult ").append(":\n").append(cd.replicas.get(j).lookup()).
                                //                                append("\nSet ").append(":\n").append(((WordTree) cd.replicas.get(j)).words.lookup()).
                                append("\n---------\n");
                    }
                    //cd.view.affiche();
                    //fail(" ** A= " + i + " ** B= " + r.getKey() + "\n" + sf.toString());
                }
            }
        }
        return cd;
    }
    static final int vocabularySize = 100;

    static OperationProfile getOperationProfileSet() {
        return new SetOperationProfile(0.70) {

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
    }

    static void testRunSetsNbReplique() throws IncorrectTraceException, PreconditionException, IOException {
        OperationProfile opp = getOperationProfileSet();
        Stat stat=new Stat();
        for (Factory<CRDT> sf : s) {
            String name = "Replica." + sf.getClass().getName();

            int nbReplic = 10;
            int nbR = 0;
            while (nbReplic <= maxReplic) {
                System.out.println(name + " with :" + nbReplic + " replica");


                int i = 0;
                nbR++;
                while (i < nbExec) {
                    i++;
                    System.out.println(i);
                    stat.addCD(testRun(sf, dur, nbReplic, opp, proba, dly));
                }
                stat.writeAverage(name, nbR);
                stat.clear();
                //result.clear();
                if (nbReplic <= 100) {
                    nbReplic += 10;
                } else {
                    nbReplic += 50;
                }
            }

        }
    }
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

    static void testRunTreeNbReplique() throws IncorrectTraceException, PreconditionException, IOException {

        OperationProfile opp = getOperationProfileSet();
        Stat stat = new Stat();
        int nbR = 0;
        for (int k = 0; k < p.length; k++) {
            for (int j = 0; j < s.length; j++) {
                String name = "Replica." + s[j].getClass().getName() + "." + p[k].getClass().getName();

                int nbReplic = 10;
                while (nbReplic <= maxReplic) {
                    System.out.println(name + " with :" + nbReplic + " replica");

                    int i = 0;
                    nbR++;
                    while (i < nbExec) {
                        i++;
                        System.out.println(i);
                        stat.addCD(testRun(new WordTree(s[j], p[k]), dur, nbReplic, treeop, proba, dly));

                    }
                    stat.writeAverage(name, i);
                    stat.clear();


                    //result.clear();
                    if (nbReplic <= 100) {
                        nbReplic += 10;
                    } else {
                        nbReplic += 50;
                    }
                }
            }
        }
    }

    static void testRunSetsDuration() throws IncorrectTraceException, PreconditionException, IOException {
        OperationProfile opp = getOperationProfileSet();
        Stat stat = new Stat();

        FileWriter Filelocal = null, Fileremote = null, Fileloop = null;
        for (Factory<CRDT> sf : s) {

            String name = "Duration." + sf.getClass().getName();

            int nbDur = 100;
            int nbD = 0;
            while (nbDur <= nbDuration) {
                System.out.println(name + " with :" + nbDur + " Dur");

                int i = 0;
                nbD++;
                while (i < nbExec) {
                    i++;
                    System.out.println(i);
                    stat.addCD(testRun(sf, nbDur, replic, opp, proba, dly)
                
                );
                }
                stat.writeAverage(name, i);
                stat.clear();
                nbDur += 100;
                //result.clear();
            }

        }
    }

    static void testRunTreeDuration() throws IncorrectTraceException, PreconditionException, IOException {
        Stat st = new Stat();
        OperationProfile opp = getOperationProfileSet();
        for (int k = 0; k < p.length; k++) {
            for (int j = 0; j < s.length; j++) {

                String name = "Duration." + s[j].getClass().getName() + "." + p[k].getClass().getName();

                int nbDur = 100;
                int nbD = 0;
                while (nbDur <= nbDuration) {
                    System.out.println(name + " with :" + nbDur + " Dur");
                    int i = 0;
                    nbD++;
                    while (i < nbExec) {
                        i++;
                        System.out.println(i);
                        st.addCD(testRun(new WordTree(s[j], p[k]), nbDur, replic, treeop, proba, dly));
                    }
                    st.writeAverage(name, nbD);
                    nbDur += 100;
                    //result.clear();
                }
            }
        }
    }

    static class Stat {

        long timeRemote = 0L;
        long nbRemote = 0L;
        long timeLocal = 0L;
        long nbLocal = 0L;

        public void addCD(CausalSimulator cd) {
            timeLocal += cd.getLocalSum();
            nbLocal += cd.getNbLocal();
            timeRemote += cd.getRemoteSum();
            nbRemote += cd.getNbRemote();
        }

        public long getRemoteAvg() {
            return timeRemote / nbRemote;
        }

        public long getLocalAvg() {
            return timeLocal / nbLocal;
        }

        public void clear() {
            timeRemote = 0L;
            nbRemote = 0L;
            timeLocal = 0L;
            nbLocal = 0L;
        }

        void writeAverage(String name, int n) throws IOException {
            FileWriter local = new FileWriter("Local." + name + ".data", true);
            FileWriter remote = new FileWriter("Remote." + name + ".data", true);

            local.write("" + n + " " + this.getLocalAvg() + "\n");
            remote.write("" + n + " " + this.getRemoteAvg() + "\n");

            if (local != null) {
                local.close();
                remote.close();
            }
        }
    }
}
