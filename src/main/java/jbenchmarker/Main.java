/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.trace.git.GitTrace;

/**
 *
 * @author urso
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    static int baseSerializ = 10, base = 100;

    public static void main(String[] args) throws Exception {

        if (args.length < 2 || args.length > 5) {
            System.err.println("Arguments : Factory Trace [nb_exec [thresold]]");
            System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
            System.err.println("- Trace : a xml file of a trace ");
            System.err.println("- nb_exec : the number of execution (default 1)");
            System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
            System.err.println("- number of serialization");
            System.exit(1);
        }
        Long sum = 0L;
        Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[0]).newInstance();
        int nbExec = (args.length > 2) ? Integer.valueOf(args[2]) : 1;
        int nb = (nbExec > 1) ? nbExec + 1 : nbExec;
        double thresold = (args.length > 3) ? Double.valueOf(args[3]) : 2.0;
        int nbrTrace = (args.length > 4) ? Integer.valueOf(args[4]) : 0;
        long ltime[][] = null, mem[][] = null, rtime[][] = null;
        int cop = 0, uop = 0, nbReplica = 0, mop = 0;
        long st = System.currentTimeMillis();
        for (int ex = 0; ex < nbExec; ex++) {
            System.out.println("execution ::: " + ex);
//            Trace trace = TraceGenerator.traceFromXML(args[1], 1);
        GitTrace trace = GitTrace.create("/Users/urso/Rech/github/git", "http://localhost:5984", "Makefile", false);
//        GitTrace trace = GitTrace.create("/Users/urso/Rech/github/linux", "http://localhost:5984", "MAINTAINERS", false);

            CausalSimulator cd = new CausalSimulator(rf);
            /*
             * trace : trace xml
             * args[4] : scalle for serialization
             * boolean : calculate time execution
             * boolean : calculate document with overhead
             */
            cd.runWithMemory(trace, nbrTrace, true, true);//0 sans serialisation

            System.out.println("nb replica : " + cd.replicas.size() + ", nb operation : " + cd.getNbLocal());
            
            if (ltime == null) {
                cop = cd.splittedGenTime().size();
                uop = cd.replicaGenerationTimes().size();
                mop = cd.getMemUsed().size();
                nbReplica = cd.replicas.size();
                ltime = new long[nb][];
                rtime = new long[nb][];
                mem = new long[nb][];
            }

            ltime[ex] = toArrayLong(cd.replicaGenerationTimes());
            mem[ex] = toArrayLong(cd.getMemUsed());

            rtime[ex] = toArrayLong(cd.splittedGenTime());
            for (int i = 0; i < minLength(rtime) - 1; i++) {
                rtime[ex][i] /= nbReplica - 1;
            }
            sum += cd.getRemoteSum() + cd.getLocalSum();
            cd = null;
            trace = null;
            System.gc();
        }
        sum = sum / nbReplica;
        sum = sum / nbExec;

        System.out.println("average execution time in : " + (sum / Math.pow(10, 6)) + " Mili-second");


        System.out.println();

        long ft = System.currentTimeMillis();
        // Name of result file is "[package.]Name[Factory]-trace[.xml].res"
        int i = args[1].lastIndexOf('/'), j = args[1].lastIndexOf('.'),
                k = args[0].lastIndexOf('.'), l = args[0].lastIndexOf("Factory");

        if (l == -1) {
            l = args[0].lastIndexOf("Factories");
        }

        if (i == -1) {
            i = args[1].lastIndexOf('\\');
        }
        String n = args[0].substring(k + 1, l);
        String[] c;
        if (n.contains("$")) {
            c = n.split("\\$");
            n = c[1];
        }
        if (n.equals("TTF")) {
            String tab[] = args[0].split("\\$");
            n = n + "" + tab[tab.length - 1];
        }

        String fileName = n + "-" + args[1].substring(i + 1, j);

        System.out.println("---------------------------------------------\nTotal time : "
                + (ft - st) / 1000.0 + " s");
        System.out.println("---------------------------------------------\n");
        System.out.flush();

        if (nbExec > 1) {
            computeAverage(ltime, thresold);
            computeAverage(rtime, thresold);
            computeAverage(mem, thresold);
        }

        String file1 = writeToFile(ltime, fileName, "usr");
        String file2 = writeToFile(rtime, fileName, "gen");
        String file3 = writeToFile(mem, fileName, "mem");

        treatFile(file1, base, "gen");
        treatFile(file2, base, "usr");
        treatFile(file3, baseSerializ, "mem");
    }

    private static long[] toArrayLong(List<Long> l) {
        long t[] = new long[l.size()];
        for (int i = 0; i < l.size(); ++i) {
            t[i] = l.get(i);
        }
        return t;
    }

    /**
     * Write all array in a file
     */
    private static String writeToFile(long[][] data, String fileName, String type) throws IOException {
        String nameFile = fileName + '-' + type + ".res";
        BufferedWriter out = new BufferedWriter(new FileWriter(nameFile));
        for (int op = 0; op <  minLength(data); ++op) {
            for (int ex = 0; ex < data.length; ++ex) {
                out.append(data[ex][op] + "\t");
            }
            out.append("\n");
        }
        out.close();
        return nameFile;
    }

    public static void computeAverage(long[][] data, double thresold) {
        int nbExpe = data.length - 1, length = minLength(data);
        data[nbExpe] = new long[length];
        for (int op = 0; op < length; ++op) {
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

    static void treatFile(String File, int baz, String result) throws IOException {
        int Tmoyen = 0;
        int cmpt = 0;
        String Line;
        String[] fData = File.split("\\.");
        String fileName = fData[0] + ".data";
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
                float tMicro = Tmoyen;

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

    static int getLastValue(String ligne) {
        String tab[] = ligne.split("\t");
        float t = Float.parseFloat(tab[(tab.length) - 1]);
        return ((int) t);
    }

    private static int minLength(long[][] data) {
        int m = data[0].length;
        for (long[] t : data) {
            if (t != null && t.length < m) m = t.length;
        }
        return m;
    }
}