/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2012
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
import crdt.Factory;
import crdt.set.CRDTSet;
import crdt.set.NaiveSet;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.TraceFromFile;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardOrderedTreeOpProfile;
import crdt.simulator.random.StandardOrderedTreeOperationProfileWithMoveRename;
import crdt.tree.fctree.FCTree;
import crdt.tree.fctree.policy.FastCycleBreaking;
import crdt.tree.orderedtree.LogootTreeNode;
import crdt.tree.orderedtree.PositionIdentifierTree;
import crdt.tree.orderedtree.PositionnedNode;
import crdt.tree.orderedtree.WootHashTreeNode;
import crdt.tree.wordtree.WordConnectionPolicy;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.*;
import java.io.*;
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
                    factstr.add("WordTree " + node.getClass().getName() + "," + setstr[i] + "," + pol.getClass().getName());
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


    }
    static int base = 100;
    static int baseSerializ = 1;

    static public void main(String[] args) throws Exception {

        generateFactory();

        if (args.length < 13) {
            System.err.println("Arguments :");
            System.err.println("- Factory number ");
            for (int p = 0; p < factstr.size(); p++) {
                System.out.println("" + p + ". " + factstr.get(p));
            }
            System.err.println("- Number of execu : ");
            System.err.println("- duration : ");
            System.err.println("- perIns : ");
            System.err.println("- perMove : ");
            System.err.println("- perRen : ");
            System.err.println("- perChild : ");
            /*
             * System.err.println("- avgBlockSize : "); System.err.println("-
             * sdvBlockSize : ");
             */
            System.err.println("- probability : ");
            System.err.println("- delay : ");
            System.err.println("- sdv : ");
            System.err.println("- replicas : ");
            System.err.println("- thresold : ");
            System.err.println("- scale for serealization : ");
            //System.err.println("- name File of trace : ");
            System.exit(1);
        }

        int j = 0;

        System.err.println("Arguments :");
        String clas = factstr.get(Integer.parseInt(args[j]));
        System.err.println("- Factory number : " + args[j++] + " \n\t" + clas);

        System.err.println("- Number of execu : " + args[j++]);
        System.err.println("- duration : " + args[j++]);
        System.err.println("- perIns : " + args[j++]);
        System.err.println("- perMove : " + args[j++]);
        System.err.println("- perRen : " + args[j++]);
        System.err.println("- perChild : " + args[j++]);
        /*
         * System.err.println("- avgBlockSize : "); System.err.println("-
         * sdvBlockSize : ");
         */
        System.err.println("- probability : " + args[j++]);
        System.err.println("- delay : " + args[j++]);
        System.err.println("- sdv : " + args[j++]);
        System.err.println("- replicas : " + args[j++]);
        System.err.println("- thresold : " + args[j++]);
        System.err.println("- scale for serealization : " + args[j++]);

        // System.err.println("- name File : " + args[j]);
        j = 0;

        Factory<CRDT> rf = (Factory<CRDT>) fact.get(Integer.parseInt(args[j++]));


        int nbExec = Integer.valueOf(args[j++]);
        int nb = 1;
        if (nbExec > 1) {
            nb = nbExec + 1;
        }
        long duration = Long.valueOf(args[j++]);
        double perIns = Double.valueOf(args[j++]);
        double perMove = Double.valueOf(args[j++]);
        double perRen = Double.valueOf(args[j++]);
        double perChild = Double.valueOf(args[j++]);
        /*
         * int avgBlockSize = Integer.valueOf(args[5]); double sdvBlockSize = Double.valueOf(args[6]);
         */
        double probability = Double.valueOf(args[j++]);
        long delay = Long.valueOf(args[j++]);
        double sdv = Double.valueOf(args[j++]);
        int replicas = Integer.valueOf(args[j++]);
        int thresold = Integer.valueOf(args[j++]);
        int scaleMemory = Integer.valueOf(args[j++]);
        //String nameUsr = args[j++];
        /**
         * *****create file result****
         */
        String fileRes;
        String nameUsr;
        if (clas.equals("OTTree")
                || clas.equals("FCTree")
                || clas.equals("FCTreeCycleBreaker")
                || clas.equals("OTTreeWithoutGarbage")
                || clas.equals("TreeOPTWithoutGarbage")
                || clas.equals("OTTreeWithoutGarbageO")
                || clas.equals("TreeOPTWithoutGarbageO")) {
            nameUsr = clas;
        } else {
            String[] res = clas.split("\\,");
            String[] typeTree = res[0].split("\\.");
            String[] typePlicy = res[2].split("\\.");
            nameUsr = typeTree[typeTree.length - 1] + "." + res[1] + "." + typePlicy[typePlicy.length - 1];

        }
        fileRes = nameUsr;

        if (j < args.length) {
            nameUsr = args[j++];
        }

        File fileUsr = new File(nameUsr);

        long ltime[][] = null, rtime[][] = null, mem[][] = null;
        int minSizeGen = 0, minSizeInteg = 0, minSizeMem = 0, nbrReplica = 0;
        int cop = 0, uop = 0, mop = 0;

        Long sum = 0L;
        for (int ex = 0; ex < nbExec; ex++) {
            System.out.println("execution : " + ex);
            /*
             * Trace trace = new RandomTrace(duration, RandomTrace.FLAT, new
             * StandardSeqOpProfile(perIns, perBlock, avgBlockSize,
             * sdvBlockSize), probability, delay, sdv, replicas);
             */
            CausalSimulator cd = new CausalSimulator(rf, true, scaleMemory, true);
            Trace trace;
            if (fileUsr.exists()) {
                System.out.println("-Trace From File : " + nameUsr);
                trace = new TraceFromFile(fileUsr, true);
                cd.setWriter(null);
            } else {
                OperationProfile opprof;
                if (perMove <= 0 && perRen <=0) {
                    opprof = new StandardOrderedTreeOpProfile(perIns, perChild);
                } else {
                    opprof = new StandardOrderedTreeOperationProfileWithMoveRename(perIns, perMove, perRen, perChild);
                }

                System.out.println("-Trace to File  " + nameUsr);
                trace = new RandomTrace(duration, RandomTrace.FLAT,opprof, probability, delay, sdv, replicas);
                cd.setWriter(new ObjectOutputStream(new FileOutputStream(nameUsr)));
            }
            System.out.println("perIns" + perIns + ", perChild" + perChild + " probability " + probability + "delay " + delay + "sdv+" + sdv + "+, replicas" + replicas);

            //file result
            /*
             * ObjectOutputStream enc=new ObjectOutputStream(new
             * FileOutputStream(nameUsr)); //XMLEncoder enc=new XMLEncoder(new
             * FileOutputStream(nameUsr)); enc.writeObject(trace); enc.flush();
             * enc.close();
             */
            /*
             * trace : trace xml args[4] : scalle for serialization boolean :
             * calculate time execution boolean : calculate document with
             * overhead
             */

            cd.run(trace);
            System.out.println("End of simulation");
            if (ltime == null) {
                cop = cd.splittedGenTime().size();
                uop = cd.replicaGenerationTimes().size();
                mop = cd.getMemUsed().size();
                ltime = new long[nb][uop];
                rtime = new long[nb][cop];
                mem = new long[nb][mop];
                minSizeGen = uop;
                minSizeInteg = cop;
                minSizeMem = mop;
                nbrReplica = cd.replicas.size();
            }

            List<Long> l = cd.replicaGenerationTimes();
            if (l.size() < minSizeGen) {
                minSizeGen = l.size();
            }
            toArrayLong(ltime[ex], l, minSizeGen);

            List<Long> m = cd.getMemUsed();
            if (m.size() < minSizeMem) {
                minSizeMem = m.size();
            }
            toArrayLong(mem[ex], m, minSizeMem);

            if (minSizeInteg > cd.splittedGenTime().size()) {
                minSizeInteg = cd.splittedGenTime().size();
            }
            toArrayLong(rtime[ex], cd.splittedGenTime(), minSizeInteg);
            for (int i = 0; i < cop - 1; i++) {
                rtime[ex][i] /= nbrReplica - 1;
            }
            sum += cd.getRemoteSum() + cd.getLocalSum();
            cd = null;
            trace = null;
            System.gc();
            Thread.sleep(1000);
            System.out.println("-----ltime : " + ltime[ex].length);
            System.out.println("-----rtime : " + rtime[ex].length);
        }
        sum = sum / nbrReplica;
        sum = sum / nbExec;
        System.out.println("Best execution time in :" + (sum / Math.pow(10, 9)) + " second");



        if (nbExec > 1) {
            computeAverage(ltime, thresold, minSizeGen);
            computeAverage(mem, thresold, minSizeMem);
            computeAverage(rtime, thresold, minSizeInteg);
        }

        String file = writeToFile(ltime, fileRes, "gen", minSizeGen);
        treatFile(file, "gen", base);
        String file2 = writeToFile(rtime, fileRes, "usr", minSizeInteg);
        treatFile(file2, "usr", base);
        String file3 = writeToFile(mem, fileRes, "mem", minSizeMem);
        treatFile(file3, "mem", baseSerializ);
    }

    private static void toArrayLong(long[] t, List<Long> l, int minSize) {
        for (int i = 0; i < minSize - 1; i++) {
            t[i] = l.get(i);
        }
    }

    /**
     * Write all array in a file
     */
    private static String writeToFile(long[][] data, String algo, String type, int minSize) throws IOException {
        String nameFile = algo + '-' + type + ".res";
        BufferedWriter out = new BufferedWriter(new FileWriter(nameFile));
        for (int op = 0; op < minSize - 1; op++) {
            for (int ex = 0; ex < data.length; ex++) {
                out.append(data[ex][op] + "\t");
            }
            out.append("\n");
        }
        out.close();
        return nameFile;
    }

    static public void treatFile(String File, String result, int baz) throws IOException {
        double Tmoyen = 0L;
        int cmpt = 0;
        String Line;
        String fileName = File.replaceAll(".res", ".data");
        PrintWriter ecrivain = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        InputStream ips1 = new FileInputStream(File);
        InputStreamReader ipsr1 = new InputStreamReader(ips1);
        BufferedReader br1 = new BufferedReader(ipsr1);
        try {
            Line = br1.readLine();
            while (Line != null) {
                for (int i = 0; i < baz; i++) {
                    if (Line != null) {
                        Tmoyen += getLastValue(Line);
                        Line = br1.readLine();
                        cmpt++;
                    } else {
                        break;
                    }
                }
                Tmoyen = Tmoyen / cmpt;
                double tMicro = Tmoyen;

                if (!result.equals("mem")) {
                    tMicro = Tmoyen / 1000; // microSeconde
                }
                ecrivain.println(tMicro);
                Tmoyen = 0;
                cmpt = 0;
            }
            br1.close();
            ecrivain.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    static double getLastValue(String ligne) {
        String tab[] = ligne.split("\t");
        double t = Double.parseDouble(tab[(tab.length) - 1]);
        return (t);
    }

    public static void computeAverage(long[][] data, double thresold, int minSize) {
        int nbExpe = data.length - 1;//une colonne réserver à la moyenne
        for (int op = 0; op < minSize - 1; op++) {
            long sum = 0;
            for (int ex = 0; ex < nbExpe; ex++) { // calculer moyenne de la ligne
                sum += data[ex][op];
            }
            long moy = 0, sum2 = 0, k = 0;
            if (nbExpe == 0) {
                moy = sum;
            } else {
                moy = sum / nbExpe;
            }

            for (int ex = 0; ex < nbExpe; ex++) {
                if (data[ex][op] < thresold * moy) {
                    sum2 += data[ex][op];
                    k++;
                }
            }
            if (k != 0) {
                data[nbExpe][op] = sum2 / k;
            }
        }
    }
}
