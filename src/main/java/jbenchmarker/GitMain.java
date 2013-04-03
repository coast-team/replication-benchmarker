/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jbenchmarker;

import crdt.CRDT;
import crdt.Factory;
import crdt.simulator.CausalSimulator;
import crdt.simulator.TraceObjectWriter;
import crdt.simulator.sizecalculator.StandardSizeCalculator;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.trace.git.CouchConnector;
import jbenchmarker.trace.git.GitTrace;

public final class GitMain extends Experience {

    private final static String dbURL = "http://localhost:5984";

    public GitMain(String[] args) throws Exception {

        if (args.length < 6 || !args[args.length-2].matches("[0-9]*") 
                || !args[args.length-3].matches("[0-9]*") || !args[args.length-4].matches("[0-9]*")) {
            System.err.println("Arguments : ");
            System.err.println("- Factory to run git main");
            System.err.println("- git directory ");
            System.err.println("- file [optional] path or number (default : all files)");
            System.err.println("- --save Store traces");
            System.err.println("- --clean [optional] clean DB");
            System.err.println("- --stat [optional] compute execution time and memory");
            System.err.println("- --dmau [optional] detect moves and updates");
            System.err.println("- Number of serialization");
            System.err.println("- i :  number of file [To begin]");
            System.err.println("- Number of execution");
            System.err.println("- Factory");
            System.exit(1);
        }

        List<String> result = new LinkedList<String>();
        String gitdir = args[1];
        int end;
        List<String> paths = new LinkedList<String>();
        if (args.length > 6 && !args[2].startsWith("--")) {
            if (args[2].matches("[0-9]*")) {
                extractFiles(new File(gitdir), gitdir, paths);
                end = Integer.parseInt(args[2]);
            } else {
                paths.add(args[2]);
                end = 1;
            }
        } else {
            extractFiles(new File(gitdir), gitdir, paths);
            end = paths.size();
        }

//        if (args.length > 1 && args[2].matches("[0-9]*")) {
//            end = Integer.parseInt(args[2]);
//        }
//        
        boolean clean = Arrays.asList(args).contains("--clean");
        boolean save = Arrays.asList(args).contains("--save");
        boolean stat = Arrays.asList(args).contains("--stat");
        boolean dmau = Arrays.asList(args).contains("--dmau");

        System.out.println("*** Total number of files : " + paths.size());
        double[] memory = new double[paths.size()];
        int m = 0;
        boolean memOk = false;
        double serMem = 0;
        //System.out.println("Path;Num;Replicas;Merges;Merge Blocks;Merge Size;Commits;Ins Blocks;Del Blocks;Upd Blocks;Ins Size;Del Size");
        String statr = "";
        String file = createName(args);
        if (stat) {
            System.out.println("\nPath;Num;Replicas;Merges;Merge Blocks;"
                    + "Merge Size;Commits;Ins Blocks;Del Blocks;Upd Blocks;Ins Size;Del Size;"
                    + "nbrOpIns;nbrOpDel;"
                    + "Nbr Ligne;NbrOp;TimeGen;AvgTimeGen;TimeRemote;AvgTimeRemote;"
                    + "AllMemory;AvgMemory");
            statr = "Path;Num;Replicas;Merges;Merge Blocks;"
                    + "Merge Size;Commits;Ins Blocks;Del Blocks;Upd Blocks;Ins Size;Del Size;"
                    + "nbrOpIns;nbrOpDel;"+ "Nbr Ligne;NbrOp;TimeGen;AvgTimeGen;TimeRemote;AvgTimeRemote;"
                    + "AllMemory;AvgMemory;SizeMessage";
            result.add(statr);
            //writeTofile(file, statr);
        } else {
            for (String s : args) {
                System.out.print(s + " ");
            }
            System.out.println("\nPath;Num;Replicas;Merges;Merge Blocks;Merge Size;Commits;Ins Blocks;Del Blocks;Upd Blocks;Ins Size;Del Size");
            result.add("Path;Num;Replicas;Merges;Merge Blocks;Merge Size;Commits;Ins Blocks;Del Blocks;Upd Blocks;Ins Size;Del Size");
        }

        int nbrExec = Integer.parseInt(args[args.length - 2]);
        Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[args.length - 1]).newInstance();

        int nb = (nbrExec > 1) ? nbrExec + 1 : nbrExec;

        int i = Integer.parseInt(args[args.length - 3]);
        int nbserializ = Integer.parseInt(args[args.length - 4]);
        CouchConnector cc = new CouchConnector(dbURL);
        for (String path : paths.subList(i, end)) {
            long ltime[][] = null, mem[][] = null, rtime[][] = null;
            int cop = 0, uop = 0, nbReplica = 0, mop = 0;
            int minCop = 0, minUop = 0, minMop = 0;

            int nbBlockMerge = 0, mergeSize = 0, nbInsBlock = 0,
                    nbDelBlock = 0, insertSize = 0, deleteSize = 0, 
                    nbUpdBlock = 0, nbMerge = 0, nbCommit = 0;

            int nbrIns = 0, nbrDel = 0;
            int sizeMsg = 0;
            boolean oneclean = clean;
            for (int k = 0; k < nbrExec; k++) {
                GitTrace trace = dmau ? GitTrace.createWithMoves(gitdir, cc, path, oneclean, 50, 20, 10) 
                        : GitTrace.create(gitdir, cc, path, oneclean);
                oneclean = false;
                CausalSimulator cd = new CausalSimulator(rf, stat, stat ? nbserializ : 0, stat);
                cd.setWriter(save ? new TraceObjectWriter("trace") : null);

                cd.run(trace);

                if (k == 0 && stat) {
                    cop = cd.getRemoteTimes().size();
                    uop = cd.getGenerationTimes().size();
                    mop = cd.getMemUsed().size();
                    nbReplica = cd.replicas.keySet().size();
                    ltime = new long[nb][uop];
                    rtime = new long[nb][cop];
                    mem = new long[nb][mop];
                    minCop = cop;
                    minUop = uop;
                    minMop = mop;
                    nbMerge = trace.nbMerge;
                    nbCommit = trace.nbCommit;
                }

                if (cd.replicas.keySet().isEmpty()) {
                    break;
                }

                if (!stat) {
                    statr = path + ';' + ++i + ';' + cd.replicas.keySet().size()
                            + ';' + trace.nbMerge + ';' + trace.nbBlockMerge
                            + ';' + trace.mergeSize
                            + ';' + trace.nbCommit + ';'
                            + trace.nbInsBlock + ';' + trace.nbDelBlock
                            + ';' + trace.nbUpdBlock + ';'
                            + trace.insertSize + ';' + trace.deleteSize;

                    System.out.println(statr);
                    result.add(statr);
                }


                if (nbReplica == 0) {
                    break;
                }

                if (stat) {
                    nbBlockMerge += trace.nbBlockMerge;
                    mergeSize += trace.mergeSize;
                    nbInsBlock += trace.nbInsBlock;
                    nbDelBlock += trace.nbDelBlock;
                    nbUpdBlock += trace.nbUpdBlock;
                    insertSize += trace.insertSize;
                    deleteSize += trace.deleteSize;
                    /*nbrIns += cd.getnbrIns();
                    nbrDel += cd.getnbrDel();*/
                    sizeMsg += this.serializ(cd.getGenHistory());

                    minCop = minCop > cd.getRemoteTimes().size() ? cd.getRemoteTimes().size() : minCop;
                    minUop = minUop > cd.getGenerationTimes().size() ? cd.getGenerationTimes().size() : minUop;
                    minMop = minMop > cd.getMemUsed().size() ? cd.getMemUsed().size() : minMop;

                    toArrayLong(ltime[k], cd.getGenerationTimes());
                    toArrayLong(rtime[k], cd.getRemoteTimes());

                    if (k == 0 || args[args.length - 1].contains("Logoot")) {
                        toArrayLong(mem[k], cd.getMemUsed());
                    }

                    for (int j = 0; j < minCop - 1; j++) {
                        if (nbReplica > 1) {
                            rtime[k][j] /= nbReplica - 1;
                        }
                    }
                }

                Iterator<Integer> a = cd.replicas.keySet().iterator();
                if (a.hasNext()) {
                    int r = a.next();
                    serMem += StandardSizeCalculator.sizeOf(cd.replicas.get(r));
                    memOk = true;
                }

                trace = null;
                cd = null;
            }

            if (memOk) {
                memory[m] = serMem / nbrExec;
                serMem = 0;
                m++;
                memOk = false;
            }

            if (stat) {
                statr = path + ';' + ++i + ';' + nbReplica
                        + ';' + nbMerge + ';' + nbBlockMerge / nbrExec
                        + ';' + mergeSize / nbrExec
                        + ';' + nbCommit + ';'
                        + nbInsBlock / nbrExec + ';' + nbDelBlock / nbrExec
                        + ';' + nbUpdBlock / nbrExec + ';'
                        + insertSize / nbrExec + ';' + deleteSize / nbrExec + ';'
                        + nbrIns / nbrExec + ';' + nbrDel / nbrExec;


                double thresold = 2.0;
                if (nbrExec > 1) {
                    computeAverage(ltime, thresold, minUop);
                    computeAverage(rtime, thresold, minCop);
                    if (args[args.length - 1].contains("Logoot")) {
                        computeAverage(mem, thresold, minMop);
                    }
                }

                int nbrLigne = (insertSize + deleteSize) / nbrExec;

                long avgGen = calcul(ltime, minUop, "gen", file, "avg");
                long somGen = calcul(ltime, minUop, "gen", file, "sum");
                long avgUsr = calcul(rtime, minCop, "usr", file, "avg");
                long somUsr = calcul(rtime, minCop, "usr", file, "sum");
                long avgMem = calcul(mem, minMop, "mem", file, "avg");
                long sumMem = calcul(mem, minMop, "mem", file, "sum");

                statr = statr + ';' + nbrLigne + ';' + minCop + ';' + somGen + ';' + avgGen
                        + ';' + somUsr + ';' + avgUsr + ';' + sumMem + ';' + avgMem + ';' + sizeMsg / nbrExec;

                result.add(statr);
                System.out.println(statr);
            }
        }

        double resMem = 0;
        if (stat) {
            for (int p = 0; p < memory.length; p++) {
                resMem += memory[p];

                System.out.println("Max Memory : " + resMem);
                writeTofile(file, result);
            }
        }
    }

    @Override
    String createName(String[] args) {
        int k = args[args.length - 1].lastIndexOf('.'), l = args[args.length - 1].lastIndexOf("Factory");
        if (l == -1) {
            l = args[args.length - 1].lastIndexOf("Factories");
        }

        String n = args[args.length - 1].substring(k + 1, l);

        String[] c;
        if (n.contains("$")) {
            c = n.split("\\$");
            n = c[1];
        }
        if (n.equals("TTF")) {
            String tab[] = args[args.length - 1].split("\\$");
            n = n + "" + tab[tab.length - 1];
        }
        return n;
    }
}
