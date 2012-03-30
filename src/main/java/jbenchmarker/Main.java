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

        if (args.length < 3 || args.length > 5) {
            System.err.println("Arguments : Factory Trace [nb_exec [thresold]]");
            System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
            System.err.println("- Trace : a xml file of a trace ");
            System.err.println("- nb_exec : the number of execution (default 1)");
            System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
            System.err.println("- number of serialization");
            System.exit(1);
        }

        Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[0]).newInstance();
        int nbExec = (args.length > 2) ? Integer.valueOf(args[2]) : 1;
        int nb = (nbExec > 1) ? nbExec + 1 : nbExec;
        double thresold = (args.length > 3) ? Double.valueOf(args[3]) : 2.0;
        long ltime[][] = null, mem[][] = null, rtime[][] = null;
        int cop = 0, uop = 0, nbReplica = 0, mop = 0;
        long st = System.currentTimeMillis();
        for (int ex = 0; ex < nbExec; ex++) {
            System.out.println("execution ::: " + ex);
            Trace trace = TraceGenerator.traceFromXML(args[1], 1);
            CausalSimulator cd = new CausalSimulator(rf);
            cd.runWithMemory(trace, Integer.valueOf(args[4]));

            if (ltime == null) {
                cop = cd.replicaRemoteTimes().get(0).size();
                uop = cd.replicaGenerationTimes().size();
                mop = cd.getMemUsed().size();
                nbReplica = cd.getReplicas().entrySet().size();
                ltime = new long[nb][uop];
                rtime = new long[nb][cop];
                mem = new long[nb][mop];
            }
            toArrayLong(ltime[ex], cd.replicaGenerationTimes());
            toArrayLong(mem[ex], cd.getMemUsed());

//            for (int r : cd.replicaRemoteTimes().keySet()) {
//                for (int i = 0; i < cop - 1; i++) {
//                    rtime[ex][i] += cd.replicaRemoteTimes().get(r).get(i);
//                }
//            }
//            for (int i = 0; i < cop; i++) {
//                rtime[ex][i] /= nbReplica;
//            }
            
            List<Hashtable<Integer, Long>> l = cd.replicaRemoteTimes().get(0);
            Iterator<Hashtable<Integer, Long>> iterator = l.iterator();
            int num = 0;
            while (iterator.hasNext()) {
                Hashtable<Integer, Long> table = iterator.next();
                int repRec = table.keys().nextElement();
                for (int r : cd.replicaRemoteTimes().keySet()) {
                    List<Hashtable<Integer, Long>> list = cd.replicaRemoteTimes().get(r);
                    Iterator<Hashtable<Integer, Long>> it = list.iterator();
                    boolean find = false;
                    while (it.hasNext()) {
                        Hashtable<Integer, Long> hs = it.next();
                        if (hs.containsKey(repRec) && !find) {
                            rtime[ex][num] += hs.get(repRec);
                            if (r != 0) {
                                it.remove();
                            }
                            find = true;
                        }
                    }
                }
                num++;
                iterator.remove();
            }

            for (int i = 0; i < cop - 1; i++) {
                rtime[ex][i] /= nbReplica;
            }
            
            
            cd = null;
            trace = null;
            System.gc();
        }


        System.out.println();

        long ft = System.currentTimeMillis();
        // Name of result file is "[package.]Name[Factory]-trace[.xml].res"
        int i = args[1].lastIndexOf('/'), j = args[1].lastIndexOf('.'),
                k = args[0].lastIndexOf('.'), l = args[0].lastIndexOf("Factory");

        if (i == -1) {
            i = args[1].lastIndexOf('\\');
        }
        String n = args[0].substring(k + 1, l);
        String[] c;
        if (n.contains("$")) {
            c = n.split("\\$");
            n = c[1];
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

        String file1 =  writeToFile(ltime, fileName, "usr");
        String file2 =  writeToFile(rtime, fileName, "gen");
        String file3 =  writeToFile(mem, fileName, "mem");
        
        treatFile(file1, base, "usr");
        treatFile(file2, base, "gen");
        treatFile(file3, baseSerializ, "mem");
    }

    private static void toArrayLong(long[] t, List<Long> l) {
        for (int i = 0; i < l.size(); ++i) {
            t[i] = l.get(i);
        }
    }

    /**
     * Write all array in a file
     */
    private static String writeToFile(long[][] data, String fileName, String type) throws IOException {
        String nameFile = fileName + '-' + type + ".res";
        BufferedWriter out = new BufferedWriter(new FileWriter(nameFile));
        for (int op = 0; op < data[0].length; ++op) {
            for (int ex = 0; ex < data.length; ++ex) {
                out.append(data[ex][op] + "\t");
            }
            out.append("\n");
        }
        out.close();
        return nameFile;
    }

    public static void computeAverage(long[][] data, double thresold) {
        int nbExpe = data.length - 1;
        for (int op = 0; op < data[0].length; ++op) {
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
}