/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker;

import crdt.CRDT;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.NTrace;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.ProgressTrace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.sizecalculator.SizeCalculator;
import crdt.simulator.sizecalculator.StandardSizeCalculator;
import crdt.simulator.tracestorage.TraceFromFile;
import crdt.simulator.tracestorage.TraceFromJSONObjectFile;
import crdt.simulator.tracestorage.TraceFromXMLObjectFile;
import crdt.simulator.tracestorage.TraceJSonObjectWriter;
import crdt.simulator.tracestorage.TraceObjectWriter;
import crdt.simulator.tracestorage.TraceStore;
import crdt.simulator.tracestorage.TraceXMLObjectWriter;
import crdt.tree.orderedtree.renderer.SizeJSonStyleDoc;
import crdt.tree.orderedtree.renderer.SizeXMLDoc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import static jbenchmarker.TreeSimulation.Serialization.Data;
import static jbenchmarker.TreeSimulation.TraceFormat.JSON;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public abstract class SimulationBase {

    protected int baseSerializ = 1;
    protected int totalDuration = 0;
    protected CmdLineParser parser;
    protected TraceParam traceP = new SimulationBase.TraceParam(0.1, 5, 10, 5);
    protected List<NTrace.RandomParameters> randomTrace = new LinkedList();

    enum Serialization {

        OverHead, Data, JSON, XML
    }

    enum TraceFormat {

        Bin, XML, JSON
    }
    /**
     * Arg4J arguments
     */
    @Option(name = "--avg", usage = "time average in res files (default 100)")
    int base = 100;
    @Option(name = "-S", usage = "kind of mem mesures format (default is overHead)")
    jbenchmarker.TreeSimulation.Serialization serialization = jbenchmarker.TreeSimulation.Serialization.OverHead;
    @Option(name = "-t", usage = "trace file used for experimentation", metaVar = "TraceFile")
    private File traceFile;
    @Option(name = "-T", usage = "Select trace format (default is binary)", metaVar = "TraceFormat")
    private jbenchmarker.TreeSimulation.TraceFormat traceFormat = jbenchmarker.TreeSimulation.TraceFormat.Bin;
    @Option(name = "-r", usage = "Thresold multiplicator")
    private int thresold = 2;
    @Option(name = "-P", usage = "Period of serialisation (default=0, disabled)")
    private int scale = 0;
    @Option(name = "-e", usage = "Number of execution (default 1)")
    private int nbExec = 1;
    @Option(name = "-h", usage = "display this message")
    boolean help = false;
    @Option(name = "--pass", usage = "set passive replicas number (default is 0)")
    int passiveReplicats = 0;
    @Option(name = "-J", usage = "Ignore the first run")
    boolean justInTime = false;
    @Option(name = "-O", usage = "prefix of output file")
    String prefixOutput = null;
    @Option(name = "-p", usage = "Show progresss bar")
    boolean progressBar = false;

    @Option(name = "-R", usage = "Set random trace param probability,delay,deviation,replica", metaVar = "prob,delay,deviation,replica")
    private void setTraceParam(String param) throws CmdLineException {
        traceP = new TraceParam(param,parser);
    }
    /*
     * end of arguements
     */

    public void printMessageafterHelp() {
    }

    final void help(int exit) {
        parser.printUsage(System.out);

        System.exit(exit);
    }

    public SimulationBase(String... arg) {
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
    LinkedList<List<Double>> resultsTimesLoc = new LinkedList();
    LinkedList<List<Double>> resultsTimesDist = new LinkedList();
    LinkedList<List<Double>> resultsMem = new LinkedList();

    abstract Factory<CRDT> getFactory();

    public Trace getTrace() {
        Trace trace = new NTrace(randomTrace);
        if (progressBar) {
            trace = new ProgressTrace(trace, totalDuration);
        }
        return trace;
    }

    /**
     * experimentation function
     */
    public void run() throws IOException, PreconditionException {
        /**
         * setup
         */
        Factory<CRDT> rf = getFactory();
        System.out.println("-" + getDefaultPrefix());
        SizeCalculator size;
        switch (serialization) {
            case JSON:
                size = new SizeJSonStyleDoc();
                break;
            case XML:
                size = new SizeXMLDoc();
                break;
            case Data:
                size = new StandardSizeCalculator(false);
                break;
            case OverHead:
            default:
                size = new StandardSizeCalculator(true);
        }

        /**
         * Simulation starts.
         */
        for (int ex = 0; ex < nbExec; ex++) {
            System.out.println("execution : " + ex);

            CausalSimulator cd;

            cd = new CausalSimulator(rf, true, scale, size);
            cd.setPassiveReplica(passiveReplicats);
            cd.setDebugInformation(false);
            //Trace trace;
            if (traceFile != null && traceFile.exists()) {
                System.out.println("-Trace From File : " + traceFile);
                cd.setWriter(null);
                cd.run(traceReader());
            } else {
                TraceStore writer = null;
                if (traceFile != null) {
                    System.out.println("-Trace to File : " + traceFile);
                    writer = getTraceWriter();
                    cd.setWriter(writer);
                }
                //System.out.println(getTrace());
                cd.run(getTrace());
                if (writer != null) {
                    writer.close();
                }
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

    private Trace traceReader() throws IOException {
        switch (traceFormat) {
            default:
            case Bin:
                return new TraceFromFile(traceFile, true);
            case XML:
                return new TraceFromXMLObjectFile(traceFile, true);
            case JSON:
                return new TraceFromJSONObjectFile(traceFile, true);


        }
    }

    private TraceStore getTraceWriter() throws IOException {
        switch (traceFormat) {
            default:
            case Bin:
                return new TraceObjectWriter(traceFile);
            case XML:
                return new TraceXMLObjectWriter(traceFile);
            case JSON:
                return new TraceJSonObjectWriter(traceFile);
        }
    }

    static public List<Double> castDoubleList(List<Long> list) {
        LinkedList<Double> ret = new LinkedList();
        for (long l : list) {
            ret.add(new Double(l));
        }
        return ret;
    }

    abstract String getDefaultPrefix();

    public void writeFiles() throws FileNotFoundException {
        if (this.prefixOutput == null) {
            prefixOutput = getDefaultPrefix();
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

    /**
     * Write file in matrix format
     *
     * @param m matrix
     * @param filename file name
     * @throws FileNotFoundException
     */
    public static void writeMapToFile(List<List<Double>> m, String filename) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filename);
        Iterator[] iterators = getIterators(m);
        while (hasNext(iterators)) {
            for (int i = 0; i < iterators.length; i++) {
                out.print(iterators[i].next());
                out.print((i == iterators.length - 1) ? "\n" : "\t");
            }
        }
        out.close();
    }

    /**
     * Check each iterator has next.
     *
     * @param it
     * @return
     */
    static private boolean hasNext(Iterator[] it) {
        int ok = 0;
        for (int i = 0; i < it.length; i++) {
            if (it[i].hasNext()) {
                ok++;
            }
        }
        return ok == it.length;
    }

    /**
     * Returne a array of iterator
     *
     * @param matrix
     * @return array of iterator
     */
    static private Iterator<Double>[] getIterators(List<List<Double>> matrix) {
        Iterator<Double>[] iterators = new Iterator[matrix.size()];
        int i = 0;
        for (List<Double> l : matrix) {
            iterators[i++] = l.iterator();
        }
        return iterators;
    }

    /**
     * Write file the list item which grouped by nbAvg and the result is divided
     * by number div.
     *
     * @param list
     * @param filename
     * @param nbAvg
     * @param div
     * @throws FileNotFoundException
     */
    public static void writeListToFile(List<Double> list, String filename, int nbAvg, int div) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filename);
        double moy = 0;
        double nb = 0;
        for (Double o : list) {
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

    /**
     * Compute average for each indices in lists
     *
     * @param list
     * @return the average list
     */
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

    protected static class TraceParam {

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

        public TraceParam(String str,CmdLineParser parser) throws CmdLineException {
            try {
                str = str.replace(")", "");
                str = str.replace("(", "");
                String param[] = str.split(",");
                probability = Double.parseDouble(param[0]);
                delay = Long.parseLong(param[1]);
                sdv = Double.parseDouble(param[2]);
                replicas = Integer.parseInt(param[3]);
            } catch (Exception ex) {
                throw new CmdLineException(parser,"Parameter is invalid " + ex);
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
