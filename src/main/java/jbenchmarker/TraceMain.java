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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker;

import crdt.CRDT;
import crdt.Factory;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.TraceFromFile;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import jbenchmarker.trace.TraceGenerator;

/**
 *
 * @author score
 */
public final class TraceMain extends Experience {

    static int baseSerializ = 1, base = 100;

    public TraceMain(String[] args) throws Exception {

        if (args.length < 10) {
            System.err.println("Arguments : Factory Trace [nb_exec [thresold]]");
            System.err.println("- Factory to run trace main");
            System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
            System.err.println("- Trace : a file of a trace ");
            System.err.println("- nb_exec : the number of execution (default 1)");
            System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
            System.err.println("- number of serialization");
            System.err.println("- Save traces ? (0 don't save, else save)");
            System.err.println("- Calcule Time execution ? (0 don't calcul, else calcule)");//make it bool
            System.err.println("- Serialization with overhead ? (0 don't store, else store)");//make it bool
            System.err.println("- Compute size of messages ? (0 don't store, else store)");//make it bool
            System.exit(1);
        }

        Long sum = 0L;
        Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[1]).newInstance();
        int nbExec = (args.length > 2) ? Integer.valueOf(args[3]) : 1;
        int nb = (nbExec > 1) ? nbExec + 1 : nbExec;
        double thresold = (args.length > 3) ? Double.valueOf(args[4]) : 2.0;
        long ltime[][] = null, mem[][] = null, rtime[][] = null;
        int cop = 0, uop = 0, nbReplica = 0, mop = 0;
        int minCop = 0, minUop = 0, minMop = 0;

        boolean calculTimeEx = Integer.valueOf(args[7]) == 0 ? false : true;
        boolean overhead = Integer.valueOf(args[8]) == 0 ? false : true;
        boolean sizeMessage = Integer.valueOf(args[9]) == 0 ? false : true;
        int sizemsg = 0;

        for (int ex = 0; ex < nbExec; ex++) {
            System.out.println("execution ::: " + ex);

            //Trace trace = TraceGenerator.traceFromXML(args[2], 1);
            Trace trace = new TraceFromFile(args[2]);
            CausalSimulator cd;




            if (ex == 0 || args[1].contains("Logoot")) {
                cd = new CausalSimulator(rf, calculTimeEx, Integer.valueOf(args[5]), overhead);
            } else {
                cd = new CausalSimulator(rf, calculTimeEx, 0, overhead);
            }
            cd.setWriter(Integer.valueOf(args[6]) == 1 ? new ObjectOutputStream(new FileOutputStream("trace")) : null);
            cd.run(trace);
            if (ltime == null) {
                cop = cd.getRemoteTimes().size();
                uop = cd.replicaGenerationTimes().size();
                mop = cd.getMemUsed().size();
                nbReplica = cd.replicas.size();
                ltime = new long[nb][uop];
                rtime = new long[nb][cop];
                mem = new long[nb][mop];
                minCop = cop;
                minUop = uop;
                minMop = mop;
            }

            minCop = minCop > cd.getRemoteTimes().size() ? cd.getRemoteTimes().size() : minCop;
            minUop = minUop > cd.replicaGenerationTimes().size() ? cd.replicaGenerationTimes().size() : minUop;
            minMop = minMop > cd.getMemUsed().size() ? cd.getMemUsed().size() : minMop;

            if (calculTimeEx) {
                toArrayLong(ltime[ex], cd.replicaGenerationTimes());
                toArrayLong(rtime[ex], cd.getRemoteTimes());
            }
            if (args[1].contains("Logoot") || ex == 0) {
                toArrayLong(mem[ex], cd.getMemUsed());
            }

            if (nbReplica > 2) {
                for (int i = 0; i < cop - 1; i++) {
                    rtime[ex][i] /= nbReplica - 1;
                }
            }

            if (sizeMessage) {
                sizemsg += this.serializ(cd.getGenHistory());
            }

            sum += cd.getRemoteSum() + cd.getLocalSum();
            cd = null;
            trace = null;
            System.gc();
        }
        sum = sum / nbReplica;
        sum = sum / nbExec;

        if (sizeMessage) {
            System.out.println("Size of message is :" + sizemsg / nbExec);
        }

        System.out.println("average execution time in : " + (sum / Math.pow(10, 6)) + " Mili-second");


        String fileName = createName(args);

        if (nbExec > 1) {
            computeAverage(ltime, thresold, minUop);
            computeAverage(rtime, thresold, minCop);
            if (args[1].contains("Logoot")) {
                computeAverage(mem, thresold, minMop);
            }
        }

        writeToFile(ltime, fileName, "gen");
        writeToFile(rtime, fileName, "usr");
        writeToFile(mem, fileName, "mem");

    }

    @Override
    String createName(String[] args) {
        int i = args[2].lastIndexOf('/'), j = args[2].lastIndexOf('.'),
                k = args[1].lastIndexOf('.'), l = args[1].lastIndexOf("Factory");

        if (l == -1) {
            l = args[1].lastIndexOf("Factories");
        }

        if (i == -1) {
            i = args[1].lastIndexOf('\\');
        }

        String n = args[1].substring(k + 1, l);

        String[] c;
        if (n.contains("$")) {
            c = n.split("\\$");
            n = c[1];
        }

        if (n.equals("TTF")) {
            String tab[] = args[1].split("\\$");
            n = n + "" + tab[tab.length - 1];
        }

        if (j < 0) {
            j = args[1].length();
        }
        return n + "-" + args[2].substring(i + 1, j);
    }
}
