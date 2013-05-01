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
package jbenchmarker;

import collect.OrderedNode;
import crdt.CRDT;
import crdt.CRDTMockTime;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.NaiveSet;
import crdt.simulator.CausalSimulator;
import crdt.simulator.TraceFromFile;
import crdt.simulator.TraceObjectWriter;
import crdt.simulator.random.NTrace;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.ProgressTrace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardOrderedTreeOpProfile;
import crdt.simulator.random.StandardOrderedTreeOperationProfileWithMoveRename;
import crdt.simulator.sizecalculator.SizeCalculator;
import crdt.simulator.sizecalculator.StandardSizeCalculator;
import crdt.tree.fctree.FCTree;
import crdt.tree.fctree.policy.FastCycleBreaking;
import crdt.tree.orderedtree.LogootTreeNode;
import crdt.tree.orderedtree.PositionIdentifierTree;
import crdt.tree.orderedtree.PositionnedNode;
import crdt.tree.orderedtree.WootHashTreeNode;
import crdt.tree.orderedtree.renderer.SizeJSonStyleDoc;
import crdt.tree.orderedtree.renderer.SizeXMLDoc;
import crdt.tree.wordtree.WordConnectionPolicy;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.*;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.logoot.BoundaryStrategy;
import jbenchmarker.ot.ottree.OTTree;
import jbenchmarker.ot.ottree.OTTreeTranformation;
import jbenchmarker.ot.ottree.TreeOPT;
import jbenchmarker.ot.ottree.TreeOPTTTFTranformation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2GarbageCollector;
import jbenchmarker.ot.soct2.SOCT2Log;
import jbenchmarker.ot.soct2.SOCT2LogTTFOpt;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TreeSimulation {

    static PositionIdentifierTree createTree(OrderedNode root, Factory<CRDTSet> sf, Factory<WordConnectionPolicy> wcp) {
        WordTree wt = new WordTree(sf.create(), wcp);
        return new PositionIdentifierTree((PositionnedNode) root.createNode(null), wt);
    }
    static Factory policy[] = {new WordSkip(), new WordReappear(), new WordRoot(), new WordCompact(),
        new WordIncrementalSkip(), new WordIncrementalReappear(),
        new WordIncrementalRoot(), new WordIncrementalCompact(), new WordIncrementalSkipOpti(),
        new WordIncrementalSkipUnique()};
    static Factory set[] = {/*
         * new CommutativeCounterSet(), new ConvergentCounterSet(), new
         * CommutativeLwwSet(), new ConvergentLwwSet(), new CommutativeOrSet(),
         * new ConvergentOrSet(), new OTSet(new SOCT2(new
         * AddWinTransformation(), null)), new OTSet(new SOCT2(new
         * DelWinTransformation(), null)), new OTSet(new SOCT2(new
         * AddWinTransformation(), new SOCT2GarbageCollector(5))), new OTSet(new
         * SOCT2(new DelWinTransformation(), new SOCT2GarbageCollector(5))),
         */
        new NaiveSet()};
    static String setstr[] = {
        /*
         * "CommutativeCounterSet" ,"ConvergentCounterSet" ,"CommutativeLwwSet"
         * ,"ConvergentLwwSet" ,"CommutativeOrSet" ,"ConvergentOrSet" ,"OTset
         * Addwin without gc" ,"OTset delwin without gc" ,"OTset Addwin with gc"
         * ,"OTset delwin with gc"
         ,
         */"NaiveSet"};
    /*
     * static Factory<CRDT> fact[]={ new OTTree(new SOCT2(0, new SOCT2Log(new
     * OTTreeTranformation()), null), new
     * PositionIdentifierTree((PositionnedNode)root.createNode(null), wt),      *
     *
     * };
     */
    static OrderedNode nodes[] = {new LogootTreeNode(null, 0, new BoundaryStrategy(32, 100)), new WootHashTreeNode(null, 0)};
    static List<Factory<CRDT>> fact = new LinkedList();
    static List<String> factstr = new LinkedList<String>();

    static void generateFactory() {

        for (Factory<WordConnectionPolicy> pol : policy) {
            for (int i = 0; i < set.length; i++) {
                for (OrderedNode node : nodes) {
                    fact.add(createTree(node, set[i], pol));
                    factstr.add("WordTree:" + node.getClass().getName() + "-" + setstr[i] + "-" + pol.getClass().getName());
                }
            }
        }
        fact.add(new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()),
                new SOCT2GarbageCollector(4))));
        factstr.add("OTTree");
        fact.add(new FCTree());
        factstr.add("FCTree");
        fact.add(new FCTree(new FastCycleBreaking("Garbage")));
        factstr.add("FCTreeCycleBreaker");

        
        //withoutGarbage
        fact.add(new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()),
                null)));
        factstr.add("OTTreeWithoutGarbage");
        fact.add(new TreeOPT(new SOCT2(0, new SOCT2Log(new TreeOPTTTFTranformation()),
                null)));
        factstr.add("TreeOPTWithoutGarbage");
        fact.add(new OTTree(new SOCT2(0, new SOCT2LogTTFOpt(new OTTreeTranformation()),
                null)));
        factstr.add("OTTreeWithoutGarbageO");
        fact.add(new TreeOPT(new SOCT2(0, new SOCT2LogTTFOpt(new TreeOPTTTFTranformation()),
                null)));
        factstr.add("TreeOPTWithoutGarbageO");

        fact.add(new CRDTMockTime(new FCTree(), 2, 3));
        factstr.add("Mock");
        fact.add(new FCTree(true));
        factstr.add("FCTreeRS");
        fact.add(new FCTree(new FastCycleBreaking("Garbage"),true));
        factstr.add("FCTreeCycleBreakerRS");

    }
    int base = 100;
    int baseSerializ = 1;
    int totalDuration = 0;
    CmdLineParser parser;

    enum Serialization {

        OverHead,Data ,JSon, XML
    }
    /**
     * Arg4J arguments
     */
    @Option(name = "-S", usage = "Serialization format default is overHead")
    Serialization serialization = Serialization.OverHead;
    @Option(name = "-t", usage = "trace to this file", metaVar = "TraceFile", required = true)
    private File traceFile;
    @Option(name = "-r", usage = "Thresold multiplicator")
    private int thresold = 2;
    @Option(name = "-s", usage = "Period of serialisation (default=0, disabled)")
    private int scale = 0;
    @Option(name = "-e", usage = "Number of execution (default 1)")
    private int nbExec = 1;
    @Option(name = "-f", usage = "set Factory by its number in list of factory", required = true)
    int numFacotory;
    @Option(name = "-h", usage = "display this message")
    boolean help = false;
    @Option(name = "--pass", usage = "set passive replicas number (default is 0)")
    int passiveReplicats = 0;
    @Option(name = "-J", usage = "Ignore the first run")
    boolean justInTime = false;
    List<NTrace.RandomParameters> randomTrace = new LinkedList();
    TraceParam traceP = new TraceParam(0.1, 5, 10, 5);

    @Option(name = "-P", usage = "Set random trace param probability,delay,deviation,replica")
    private void setTraceParam(String param) throws CmdLineException {
        traceP = new TraceParam(param);
    }
    @Option(name = "-O", usage = "prefix of output file")
    String prefixOutput = null;

    @Option(name = "-A", usage = "Generate Add/del Trace -A perIns,perChild,duration")
    private void genAdddel(String param) throws CmdLineException {
        try {
            param = param.replace(")", "");
            param = param.replace("(", "");
            String[] params = param.split(",");
            double perIns = Double.parseDouble(params[0]);
            double perChild = Double.parseDouble(params[1]);
            int duration = Integer.parseInt(params[2]);

            totalDuration += duration;
            //System.out.println("Generation Add/Del Trace \n"+duration+" ops with:\n"+perIns+"prob insert and \n"+perChild+"prob use child");
            OperationProfile opprof = new StandardOrderedTreeOpProfile(perIns, perChild);
            randomTrace.add(traceP.makeRandomTrace(duration, opprof));
        } catch (Exception ex) {
            throw new CmdLineException("Parameter is invalid " + ex);
        }
    }

    @Option(name = "-M", usage = "Generate Add/del/Rename/Move Trace -M perIns,PerMv,perRen,perChild,duration")
    private void genAddDelMv(String param) throws CmdLineException {
        try {
            param = param.replace(")", "");
            param = param.replace("(", "");

            String[] params = param.split(",");
            double perIns = Double.parseDouble(params[0]);
            double perMv = Double.parseDouble(params[1]);
            double perRen = Double.parseDouble(params[2]);
            double perChild = Double.parseDouble(params[3]);
            int duration = Integer.parseInt(params[4]);

            totalDuration += duration;
            // System.out.println("Generation Add/Del/Ren/Mv Trace \n"+duration+" ops with:\n"+perIns+"prob insert, \n"+perChild+"prob use child\n"+perMv+"Prob move\n"+perRen+"prob ren");
            OperationProfile opprof = new StandardOrderedTreeOperationProfileWithMoveRename(perIns, perMv, perRen, perChild);
            randomTrace.add(traceP.makeRandomTrace(duration, opprof));
        } catch (Exception ex) {
            throw new CmdLineException("Parameter is invalid " + ex);
        }
    }
    /*
     * end of arguements
     */

    final void help(int exit) {
        parser.printUsage(System.out);
        System.out.println("Factories : ");
        for (int p = 0; p < factstr.size(); p++) {
            System.out.println("" + p + ". " + factstr.get(p));
        }
        System.exit(exit);
    }

    public TreeSimulation(String... arg) {
        try {
            this.parser = new CmdLineParser(this);

            parser.parseArgument(arg);

            if (help) {
                help(0);
            }
        } catch (CmdLineException ex) {
            System.err.println("Error in argument " + ex);
            help(-1);
        }
    }

    static public void main(String[] args) throws Exception {

        generateFactory();
        TreeSimulation sim = new TreeSimulation(args);
        sim.run();
        sim.writeFiles();


    }
    LinkedList<List<Double>> resultsTimesLoc = new LinkedList();
    LinkedList<List<Double>> resultsTimesDist = new LinkedList();
    LinkedList<List<Double>> resultsMem = new LinkedList();

    /**
     * experimentation function
     */
    public void run() throws IOException, PreconditionException {
        /**
         * setup
         */
        Factory<CRDT> rf = fact.get(this.numFacotory);
        System.out.println("-" + factstr.get(this.numFacotory));
        SizeCalculator size;
        switch (serialization) {
            case JSon:
                size = new SizeJSonStyleDoc();
                break;
            case XML:
                size = new SizeXMLDoc();
                break;
            case Data: 
                size = new StandardSizeCalculator(false);
            case OverHead:
            default:
                size = new StandardSizeCalculator(true);
        }

        /**
         * Simulation starts.
         */
        for (int ex = 0; ex < nbExec; ex++) {
            TraceObjectWriter writer;
            System.out.println("execution : " + ex);

            CausalSimulator cd;

            cd = new CausalSimulator(rf, true, scale, size);
            cd.setPassiveReplica(passiveReplicats);
            cd.setDebugInformation(false);
            //Trace trace;
            if (traceFile.exists()) {
                System.out.println("-Trace From File : " + traceFile);
                cd.setWriter(null);
                cd.run(new TraceFromFile(traceFile, true));
            } else {
                System.out.println("-Trace to File : " + traceFile);
                writer = new TraceObjectWriter(traceFile);
                cd.setWriter(writer);
                System.out.println(randomTrace);
                cd.run(new ProgressTrace(new NTrace(randomTrace), totalDuration));
                writer.close();
            }
            System.out.println("End of simulation");
            /*
             * Store the result
             */
            if (!justInTime || ex > 0) {
                resultsTimesDist.add(cd.getAvgPerRemoteMessage());
                resultsTimesLoc.add(castDoubleList(cd.getGenerationTimes()));
                resultsMem.add(castDoubleList(cd.getMemUsed()));
            } else {
                System.out.println("\nIt was for fun !");
            }

        }
    }

    static public List<Double> castDoubleList(List<Long> list) {
        LinkedList<Double> ret = new LinkedList();
        for (long l : list) {
            ret.add(new Double(l));
        }
        return ret;
    }

    public void writeFiles() throws FileNotFoundException {
        if (this.prefixOutput == null) {
            prefixOutput = factstr.get(this.numFacotory);
        }

        resultsTimesDist.add(computeAvg(resultsTimesDist));
        resultsTimesLoc.add(computeAvg(resultsTimesLoc));

        writeMapToFile(resultsTimesDist, prefixOutput + "-dist.data");
        writeMapToFile(resultsTimesLoc, prefixOutput + "-loc.data");


        writeListToFile(resultsTimesDist.getLast(), prefixOutput + "-dist.res", base, 1000);//1000 for micro second
        writeListToFile(resultsTimesLoc.getLast(), prefixOutput + "-loc.res", base, 1000);

        if (!resultsMem.isEmpty() && !resultsMem.get(0).isEmpty()) {
            resultsMem.add(computeAvg(resultsMem));
            writeMapToFile(resultsMem, prefixOutput + "-mem.data");
            writeListToFile(resultsMem.getLast(), prefixOutput + "-mem.res", baseSerializ, 1);
        }

    }

    public static void writeMapToFile(List<List<Double>> m, String filename) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filename);
        Iterator[] iterators = getIterators(m);
        while (hasNext(iterators)) {
            for (int i = 0; i < iterators.length; i++) {
                // if (iterators[i].hasNext()) {
                out.print(iterators[i].next());
                out.print((i == iterators.length - 1) ? "\n" : "\t");
                /*} else {
                 out.print("-1");
                 }*/
            }
        }
        out.close();
    }

    static private boolean hasNext(Iterator[] it) {
        int ok = 0;
        for (int i = 0; i < it.length; i++) {
            if (it[i].hasNext()) {
                ok++;
            }
        }
        return ok == it.length;
    }

    static private Iterator<Double>[] getIterators(List<List<Double>> list) {
        Iterator<Double>[] iterators = new Iterator[list.size()];
        int i = 0;
        for (List<Double> l : list) {
            iterators[i++] = l.iterator();
        }
        return iterators;
    }

    public static void writeListToFile(List<Double> l, String filename, int nbAvg, int div) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filename);
        double moy = 0;
        double nb = 0;
        for (Double o : l) {
            nb++;
            moy += o;
            if (nb == nbAvg) {
                out.println(((moy / nb) / ((double) div)));
                nb = 0;
                moy = 0;
            }
        }
        out.close();
    }

    public List<Double> computeAvg(List<List<Double>> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        List<Double> ret = new LinkedList();
        Iterator<Double>[] iterators = getIterators(list);
        LinkedList<Double> vals = new LinkedList();
        double moy = 0;

        while (hasNext(iterators)) {
            for (int i = 0; i < iterators.length; i++) {
                Double value = iterators[i].next();
                vals.add(value);
                moy += value;
            }
            moy /= vals.size();
            double moyfinal = 0;
            int nbElem = 0;
            for (Double l : vals) {
                if (l < thresold * moy) {
                    nbElem++;
                    moyfinal += l;
                }
            }
            vals.clear();
            moyfinal /= (double) nbElem;
            ret.add((double) moyfinal);
        }
        return ret;
    }

    private static class TraceParam {

        double probability;
        long delay;
        double sdv;
        int replicas;

        public double getProbability() {
            return probability;
        }

        public long getDelay() {
            return delay;
        }

        public double getSdv() {
            return sdv;
        }

        public int getReplicas() {
            return replicas;
        }

        public TraceParam(String str) throws CmdLineException {
            try {
                str = str.replace(")", "");
                str = str.replace("(", "");
                String param[] = str.split(",");
                probability = Double.parseDouble(param[0]);
                delay = Long.parseLong(param[1]);
                sdv = Double.parseDouble(param[2]);
                replicas = Integer.parseInt(param[3]);
            } catch (Exception ex) {
                throw new CmdLineException("Parameter is invalid " + ex);
            }
        }

        public NTrace.RandomParameters makeRandomTrace(int duration, OperationProfile opprof) {
//            System.out.println("setup: \n"+probability+" prob to gen op\n"+delay+" delay of op\n"+sdv+" of deviation\n"+replicas+" replicas");
            return new NTrace.RandomParameters(duration, RandomTrace.FLAT, opprof, probability, delay, sdv, replicas);
        }

        public TraceParam(double probability, long delay, double sdv, int replicas) {
            this.probability = probability;
            this.delay = delay;
            this.sdv = sdv;
            this.replicas = replicas;
        }
    }
}
