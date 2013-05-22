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
import crdt.set.CRDTSet;
import crdt.set.NaiveSet;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.StandardOrderedTreeOpProfile;
import crdt.simulator.random.StandardOrderedTreeOperationProfileWithMoveRename;
import crdt.tree.fctree.FCTreeGf;
import crdt.tree.fctree.FCTreeT;
import crdt.tree.fctree.policy.FCTreeGC;
import crdt.tree.fctree.policy.FastCycleBreaking;
import crdt.tree.orderedtree.LogootTreeNode;
import crdt.tree.orderedtree.PositionIdentifierTree;
import crdt.tree.orderedtree.PositionnedNode;
import crdt.tree.orderedtree.WootHashTreeNode;
import crdt.tree.wordtree.WordConnectionPolicy;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.logoot.BoundaryStrategy;
import jbenchmarker.ot.ottree.OTTree;
import jbenchmarker.ot.ottree.OTTreeTransformation;
import jbenchmarker.ot.ottree.TreeOPT;
import jbenchmarker.ot.ottree.TreeOPTTTFTranformation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2GarbageCollector;
import jbenchmarker.ot.soct2.SOCT2Log;
import jbenchmarker.ot.soct2.SOCT2LogTTFOpt;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TreeSimulation extends SimulationBase {

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
     * OTTreeTransformation()), null), new
     * PositionIdentifierTree((PositionnedNode)root.createNode(null), wt),      *
     *
     * };
     */
    static OrderedNode nodes[] = {new LogootTreeNode(null, 0, new BoundaryStrategy(32, 100)), new WootHashTreeNode(null, 0)};
    static List<Factory<CRDT>> fact = new ArrayList();
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
        fact.add(new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTransformation()),
                new SOCT2GarbageCollector(4))));
        factstr.add("OTTree");

        fact.add(new FCTreeT());
        factstr.add("FCTreeT");

        fact.add(new FCTreeT(true));
        factstr.add("FCTreeTRS");




        //withoutGarbage
        fact.add(new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTransformation()),
                null)));
        factstr.add("OTTreeWithoutGarbage");

        fact.add(new TreeOPT(new SOCT2(0, new SOCT2Log(new TreeOPTTTFTranformation()),
                null)));
        factstr.add("TreeOPTWithoutGarbage");

        fact.add(new OTTree(new SOCT2(0, new SOCT2LogTTFOpt(new OTTreeTransformation()),
                null)));
        factstr.add("OTTreeWithoutGarbageO");

        fact.add(new TreeOPT(new SOCT2(0, new SOCT2LogTTFOpt(new TreeOPTTTFTranformation()),
                null)));
        factstr.add("TreeOPTWithoutGarbageO");

        fact.add(new CRDTMockTime(new FCTreeGf(), 2, 3));
        factstr.add("Mock");
        fact.add(new FCTreeGf());
        factstr.add("FCTreeGf");

        fact.add(new FCTreeGf(new FastCycleBreaking("Garbage")));
        factstr.add("FCTreeGfCycleBreaker");

        fact.add(new FCTreeGf(true));
        factstr.add("FCTreeGfRS");

        fact.add(new FCTreeGf(new FastCycleBreaking("Garbage"), true));
        factstr.add("FCTreeGfCycleBreakerRS");

        fact.add(new FCTreeT(new FCTreeGC()));
        factstr.add("FCTreeTGC");

        fact.add(new FCTreeT(new FCTreeGC(), true));
        factstr.add("FCTreeTGCRS");
    }

    @Override
    Factory<CRDT> getFactory() {
        return fact.get(numFacotory);
    }

    @Override
    String getDefaultPrefix() {
        return factstr.get(numFacotory);
    }

    enum Serialization {

        OverHead, Data, JSON, XML
    }

    enum TraceFormat {

        Bin, XML, JSON
    }
    /**
     * Arg4J arguments
     */
    @Option(name = "-f", usage = "set Factory by its number in list of factory", required = true)
    int numFacotory;

    @Option(name = "-A", usage = "Generate Add/del Trace -A perIns,perChild,duration", metaVar = "perIns,perChild,duration")
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

    @Option(name = "-M", usage = "Generate Add/del/Rename/Move Trace", metaVar = "perIns,PerMv,perRen,perChild,duration")
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
    @Override
    public void printMessageafterHelp() {
        System.out.println("Factories : ");
        for (int p = 0; p < factstr.size(); p++) {
            System.out.println("" + p + ". " + factstr.get(p)/*+":"+fact.get(p).toString()+')'*/);
        }
    }

    public TreeSimulation(String... arg) {
        super(arg);
    }

    static public void main(String[] args) throws Exception {

        generateFactory();
        TreeSimulation sim = new TreeSimulation(args);
        sim.run();
        sim.writeFiles();
    }
}
