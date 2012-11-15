/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package jbenchmarker.trace.git;

import crdt.CRDT;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.CausalSimulator;
import crdt.simulator.IncorrectTraceException;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Hello world!
 *
 */
public class App {


    private final static String dbURL = "http://localhost:5984";
        
    public static void main(String[] args) throws IOException, GitAPIException, IncorrectTraceException, PreconditionException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        //        GitExtraction.parseRepository(("/Users/urso/Rech/github/linux", "http://localhost:5984", "kernel/sched.c", true);
        if (args.length < 1) {
            System.err.println("Arguments : ");
            System.err.println("- git directory ");
            System.err.println("- file [optional] path or number (default : all files)");
            System.err.println("- --save [optional] save trace");
            System.err.println("- --clean [optional] clean DB");
            System.err.println("- --stat [optional] compute execution time and memory");
            System.err.println("- Number of execution");
            System.err.println("- Factory");
            System.exit(1);
        }
        String gitdir = args[0];

        List<String> paths = new LinkedList<String>();
        if (args.length > 1 && !args[1].startsWith("--") && !args[1].matches("[0-9]*")) {
            paths.add(args[1]);
        } else {
            extractFiles(new File(gitdir), gitdir, paths);
        }
        int end = paths.size();
//        if (args.length > 1 && args[1].matches("[0-9]*")) {
//            end = Integer.parseInt(args[1]);
//        }

        boolean clean = Arrays.asList(args).contains("--clean");
        boolean save = Arrays.asList(args).contains("--save");
        boolean stat = Arrays.asList(args).contains("--stat");
        
        System.out.println("*** Total number of files : " + paths.size());
        //System.out.println("Path;Num;Replicas;Merges;Merge Blocks;Merge Size;Commits;Ins Blocks;Del Blocks;Upd Blocks;Ins Size;Del Size");
        String file = getNameFile(args);
        if (stat) {
            writeTofile(file, "Path;Num;Replicas;Merges;Merge Blocks;Merge Size;Commits;Ins Blocks;Del Blocks;Upd Blocks;Ins Size;Del Size; Nbr Op; Local time; Remote Time; Memory");
        } else {
            for (String s : args) {
                System.out.print(s + " ");
            }
            System.out.println("\nPath;Num;Replicas;Merges;Merge Blocks;Merge Size;Commits;Ins Blocks;Del Blocks;Upd Blocks;Ins Size;Del Size");
        }
        
        int nbrExec = Integer.parseInt(args[args.length - 2]);
        Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[args.length - 1]).newInstance();

        int nb = (nbrExec > 1) ? nbrExec + 1 : nbrExec;
        
        int i = 0;
        CouchConnector cc = new CouchConnector(dbURL);
        for (String path : paths.subList(i, end)) {
            long ltime[][] = null, mem[][] = null, rtime[][] = null;
            int cop = 0, uop = 0, nbReplica = 0, mop = 0;
            int minCop = 0, minUop = 0, minMop = 0;
            String statr = "";
            GitTrace trace = GitTrace.create(gitdir, cc, path, clean);
            for (int k = 0; k < nbrExec; k++) {
                CausalSimulator cd = new CausalSimulator(rf);
                
                cd.run(trace, stat,  stat ? 100 : 0, stat);
                if (k == 0) {
                    if (stat) {
                        cop = cd.splittedGenTime().size();
                        uop = cd.replicaGenerationTimes().size();
                        mop = cd.getMemUsed().size();
                        nbReplica = cd.replicas.keySet().size();
                        ltime = new long[nb][uop];
                        rtime = new long[nb][cop];
                        mem = new long[nb][mop];
                        minCop = cop;
                        minUop = uop;
                        minMop = mop;
                    }


                }
                                    statr = path + ';' + ++i + ';' + cd.replicas.keySet().size()
                            + ';' + trace.nbMerge + ';' + trace.nbBlockMerge
                            + ';' + trace.mergeSize
                            + ';' + trace.nbCommit + ';'
                            + trace.nbInsBlock + ';' + trace.nbDelBlock
                            + ';' + trace.nbUpdBlock + ';'
                            + trace.insertSize + ';' + trace.deleteSize;
                    System.out.println(statr);
                    
                if (nbReplica == 0) {
                    break;
                }

                if (stat) {
                    if (minCop > cd.splittedGenTime().size()) {
                        minCop = cd.splittedGenTime().size();
                    }
                    if (minUop > cd.replicaGenerationTimes().size()) {
                        minUop = cd.replicaGenerationTimes().size();
                    }
                    toArrayLong(ltime[k], cd.replicaGenerationTimes(), minUop);
                    toArrayLong(rtime[k], cd.splittedGenTime(), minCop);
                
                if (k == 0) {
                    toArrayLong(mem[k], cd.getMemUsed(), minMop);
                }

                for (int j = 0; j < minCop - 1; j++) {
                    if (nbReplica > 1) {
                        rtime[k][j] /= nbReplica - 1;
                    }
                }
                }

            }

            if (stat) {
                double thresold = 2.0;
                if (nbrExec > 1) {
                    computeAverage(ltime, thresold, minUop);
                    computeAverage(rtime, thresold, minCop);
                }

                long avgGen = calculAvg(ltime, minUop, "gen");
                long avgUsr = calculAvg(rtime, minCop, "usr");
                long avgMem = calculAvg(mem, minMop, "mem");
                statr = statr + ';' + minCop + ';' + avgGen / 1000 + ';' + avgUsr / 1000 + ';' + avgMem;
                writeTofile(file, statr);
            }
        }
    }

    private static void extractFiles(File dir, String gitdir, List<String> paths) {
        for (File f : dir.listFiles()) {
            if (f.isFile()) { // && !f.getName().startsWith(".git")) { 
                paths.add(f.getAbsolutePath().substring(gitdir.length() + 1));
            } else if (f.isDirectory() && !".git".equals(f.getName())) {
                extractFiles(f, gitdir, paths);
            }
        }
    }

    private static void toArrayLong(long[] t, List<Long> l, int size) {
        for (int i = 0; i < size; ++i) {
            t[i] = l.get(i);
        }
    }

    private static long calculAvg(long[][] data,int l, String type) throws IOException {

        long avg = 0L;
        if(type.equals("mem"))
            for (int ex = 0; ex < l; ++ex)
            avg += data[0][ex];
        else
            for (int ex = 0; ex < l; ++ex) {
            avg += data[data.length - 1][ex];

        }
        
        return avg;
    }
    
    public static void writeTofile(String file, String s) throws IOException
    {
        FileWriter local = new FileWriter(file+".xlsx", true);

        local.write(s+"\n");

        if (local != null) {
            local.close();
        }
    }

    public static void computeAverage(long[][] data, double thresold, int l) {
        int nbExpe = data.length - 1;

        for (int op = 0; op < l; ++op) {
            long sum = 0;
            for (int ex = 0; ex < nbExpe; ++ex) {
                sum += data[ex][op];
            }
            long moy = sum / nbExpe, sum2 = 0, k = 0;
            for (int ex = 0; ex < nbExpe; ++ex) {
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
    
    public static String getNameFile(String args[]) {
        int k = args[args.length - 1].lastIndexOf('.'), l = args[args.length - 1].lastIndexOf("Factory");
        if (l == -1) {
            l = args[0].lastIndexOf("Factories");
        }

        String n = args[args.length - 1].substring(k + 1, l);

        String[] c;
        if (n.contains("$")) {
            c = n.split("\\$");
            n = c[1];
        }
        if (n.equals("TTF")) {
            String tab[] = args[0].split("\\$");
            n = n + "" + tab[tab.length - 1];
        }
        return n;
    }
}
// git : 967 authors, 30000 commits: Total time: 5:46:52.821s 