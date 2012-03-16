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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.sim.OldCausalDispatcher;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.trace.SequenceOperation;
/**
 *
 * @author urso
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        if (args.length < 2 || args.length > 4) {
            System.err.println("Arguments : Factory Trace [nb_exec [thresold]]");
            System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
            System.err.println("- Trace : a xml file of a trace ");
            System.err.println("- nb_exec : the number of execution (default 1)");
            System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
            System.exit(1);
        }

        ReplicaFactory rf = (ReplicaFactory) Class.forName(args[0]).newInstance();
        int nbExec = (args.length > 2) ? Integer.valueOf(args[2]) : 1;
        int nb = (nbExec > 1) ? nbExec + 1 : nbExec;
        double thresold = (args.length > 3) ? Double.valueOf(args[3]) : 2.0;
        long ltime[][] = null, mem[][] = null, rtime[][] = null;
        int cop = 0, uop = 0, nbReplica = 0;
        long st = System.currentTimeMillis();
        for (int ex = 0; ex < nbExec; ex++) {
            Iterator<SequenceOperation> trace = TraceGenerator.traceFromXML(args[1], 1);
            OldCausalDispatcher cd = new OldCausalDispatcher(rf);
            cd.run(trace);

            if (ltime == null) {
                cop = cd.getReplicas().get(0).getHistory().size();
                uop = cd.replicaGenerationTimes().size();
                nbReplica = cd.getReplicas().entrySet().size();
                ltime = new long[nb][uop];
                rtime = new long[nb][cop];
                mem = new long[nb][uop];
            }
            toArrayLong(ltime[ex], cd.replicaGenerationTimes());
            toArrayLong(mem[ex], cd.getMemUsed());
            
            for (MergeAlgorithm m : cd.getReplicas().values()) {
                for (int i = 0; i < cop; i++) {
                    rtime[ex][i] += m.getExecTime().get(i);
                }
            }
            for (int i = 0; i < cop; i++) {
                rtime[ex][i] /= nbReplica;
            }
            cd = null; trace = null;
            System.gc();
            if (nbExec > 1) System.out.print(ex + " . ");
        }


        System.out.println();

        long ft = System.currentTimeMillis();
        // Name of result file is "[package.]Name[Factory]-trace[.xml].res"
        int i = args[1].lastIndexOf('/'), j = args[1].lastIndexOf('.'),
                k = args[0].lastIndexOf('.'), l = args[0].lastIndexOf("Factory");

        if (i == -1) {
            i = args[1].lastIndexOf('\\');
        }
        String fileName = args[0].substring(k + 1, l) + "-" + args[1].substring(i + 1, j);

        System.out.println("---------------------------------------------\nTotal time : " 
                + (ft - st) / 1000.0 + " s");
        System.out.println("---------------------------------------------\n");
        System.out.flush();
//            System.out.println(R0.getDoc().view());
        
        if (nbExec > 1) {
            computeAverage(ltime, thresold);
            computeAverage(rtime, thresold);
            computeAverage(mem, thresold);
        }
        
        writeToFile(ltime, fileName, "usr");
        writeToFile(rtime, fileName, "gen");
        writeToFile(mem, fileName, "mem");
    }
    
    private static void toArrayLong(long[] t, List<Long> l) {
        for (int i = 0; i < l.size(); ++i) 
            t[i] = l.get(i);
    }

    /**
     * Write all array in a file
     */
    private static void writeToFile(long[][] data, String fileName, String type) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName + '-' + type + ".res"));
        for (int op = 0; op < data[0].length; ++op) {
            for (int ex = 0; ex < data.length; ++ex) {
                out.append(data[ex][op] + "\t");
            }
            out.append("\n");
        }
        out.close();
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
            data[nbExpe][op] = sum2 / (k - 1);
        }
    }
}
