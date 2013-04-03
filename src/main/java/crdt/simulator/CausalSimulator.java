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
package crdt.simulator;

import collect.VectorClock;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.sizecalculator.SizeCalculator;
import crdt.simulator.sizecalculator.StandardSizeCalculator;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;

/**
 *
 * @author urso
 */
public class CausalSimulator extends Simulator {

    int tour = 0;
    boolean overhead = false;
    HashSet<CRDTMessage> setOp;
    private Map<Integer, List<TraceOperation>> history;
    private Map<Integer, List<CRDTMessage>> genHistory;
    private long localSum = 0L, nbLocal = 0L, remoteSum = 0L, nbRemote = 0L;
    private int nbrTrace = 0;
    TraceStore writer = null;
    private HashMap<TraceOperation, Integer> orderTrace;
    private boolean detail = true;
    private SizeCalculator serializer;
    private int passiveReplica = 1;
    private boolean debugInformation=true;

    public CausalSimulator(Factory<? extends CRDT> rf) {
        super(rf);
        this.serializer = new StandardSizeCalculator(true);
    }

    public CausalSimulator(Factory<? extends CRDT> rf, boolean detail, int nbrTrace, SizeCalculator sizeCalc) {
        super(rf);
        this.detail = detail;
        this.nbrTrace = nbrTrace;
        this.serializer = sizeCalc;
    }

    /**
     *
     * @param rf
     * @param detail true for a detail of each execution time false for only the
     * overall sum.
     * @param nbrTrace frequency with which the calculated memory usage, 0 for
     * no memory measurement.
     * @param overhead true for replica memory memory measurement, false for
     * document measurement.
     *
     */
    public CausalSimulator(Factory<? extends CRDT> rf, boolean detail, int nbrTrace, boolean overhead) {
        super(rf);
        this.detail = detail;
        this.nbrTrace = nbrTrace;
        this.serializer = new StandardSizeCalculator(overhead);
    }
/*
 * Passive replica is a replicat added in simulation witch recieve all operations
 * 
 */

    public int getPassiveReplica() {
        return passiveReplica;
    }
    
    public void setPassiveReplica(int passiveReplica) {
        this.passiveReplica = passiveReplica;
    }

    public boolean isDebugInformation() {
        return debugInformation;
    }

    public void setDebugInformation(boolean debugInformation) {
        this.debugInformation = debugInformation;
    }

    
    /**
     * Lists of remote messages.
     *
     * @return a map replica id -> messages received.
     */
    public Map<Integer, List<CRDTMessage>> getGenHistory() {
        return genHistory;
    }

    /**
     * Lists of local operation.
     *
     * @return a map replica id -> operation generated.
     */
    public Map<Integer, List<TraceOperation>> getHistory() {
        return history;
    }

  
    
    /**
     * The whole time taken by appying local operations.
     *
     * @return time in nanoseconds.
     */
    public long getLocalTimeSum() {
        return localSum;
    }

  
    /**
     * The number of local operations.
     *
     * @return number.
     */
    public long getNbLocalOp() {
        return nbLocal;
    }

    /**
     * The number of remote operations.
     *
     * @return number.
     */
    public long getNbRemote() {
        return nbRemote;
    }

    /**
     * The whole time taken by appying remote operations.
     *
     * @return time in nanoseconds.
     */
    public long getRemoteSum() {
        return remoteSum;
    }

    /**
     * The average time taken by appying remote operations.
     *
     * @return time in nanoseconds.
     */
    public double getRemoteAvg() {
        return remoteSum / ((double) nbRemote);
    }

    //Viewer view =null; /*new DiagSequence(20);*/
    /**
     * Runs a causally ordered trace. Throws exception if not causally ordered
     * trace or pb with classes.
     */
    public void clearStat() {
        localSum = 0L;
        nbLocal = 0L;
        remoteSum = 0L;
        nbRemote = 0L;
    }

    public TraceStore getWriter() {
        return writer;
    }

    public void setWriter(TraceStore writer) {
        this.writer = writer;
    }

    /**
     * Runs a trace of operations. Iterates trough trace and apply each
     * operation. Instanciate replica when needed using the replica factory. For
     * each operation apply (localy or remotely) store execution time.
     * Optionally, computes the memory usage (costly operation)
     *
     * @param trace a trace, i.e. a enumeration of TraceOperation
     * @throws Exception if the Trace is incorrect (non causal, etc..)
     * @see Trace, TraceOperation, crdt.CRDT
     */
    @Override
    public void run(Trace trace) throws IncorrectTraceException, PreconditionException, IOException {

        long tmp;
        final Map<Integer, VectorClock> clocks = new HashMap<Integer, VectorClock>();
        final VectorClock globalClock = new VectorClock();
        final List<TraceOperation> concurrentOps = new LinkedList<TraceOperation>();
        final Enumeration<TraceOperation> it = trace.enumeration();
        orderTrace = new HashMap();
        int numTrace = 0;

        // Passive replica that only receive operations
        for(int i=1;i<=passiveReplica;i++) {
            this.newReplica(-i);
            clocks.put(-i, new VectorClock());
        }

        setOp = new HashSet();
        history = new HashMap<Integer, List<TraceOperation>>();
        genHistory = new HashMap<Integer, List<CRDTMessage>>();
        while (it.hasMoreElements()) {
            tour++;
            final TraceOperation opt = it.nextElement();

            final int r = opt.getReplica();

            if (r == -1) {
                throw new IncorrectTraceException("Incorrect replica value (-1) : " + opt);
            }

            CRDT localReplica = this.getReplicas().get(r);

            if (localReplica == null) {
                localReplica = this.newReplica(r);
                clocks.put(r, new VectorClock());
                genHistory.put(r, new ArrayList<CRDTMessage>());
                history.put(r, new ArrayList<TraceOperation>());
            }

            VectorClock vc = clocks.get(r);
            if (!vc.readyFor(r, opt.getVectorClock())) {
                // applyRemote concurrent operations
                Iterator<Integer> i = opt.getVectorClock().keySet().iterator();

                concurrentOps.clear();
                while (i.hasNext()) {
                    int e = i.next();
                    if (e != r) {
                        for (int j = opt.getVectorClock().get(e); j > vc.getSafe(e); j--) {
                            insertCausalOrder(concurrentOps, history.get(e).get(j - 1));
                        }
                    }
                }
                play(localReplica, vc, concurrentOps);
            }
            LocalOperation op = opt.getOperation();
            op = op.adaptTo(localReplica);
            if (writer != null) {
                writer.storeOp(opt);
            }

            history.get(r).add(opt);
            
            if (detail) {
                orderTrace.put(opt, numTrace++);
            }
            if (!vc.readyFor(r, opt.getVectorClock())) {
                throw new IncorrectTraceException("replica " + r + " with vc " + vc + " not ready for " + opt.getVectorClock());
            }
            tmp = System.nanoTime();

            final CRDTMessage m = localReplica.applyLocal(op);

            long after = System.nanoTime();
            localSum += (after - tmp);
            if (detail) {
                genTime.add(after - tmp);
                //stat(opt, after - tmp, 0);
                genSize.add(m.size());
                remoteTime.add(0L);
            }

            nbLocal++;
            final CRDTMessage msg = m.clone();

            genHistory.get(r).add(msg);
            clocks.get(r).inc(r);
            globalClock.inc(r);
            ifSerializ();
        }
        ifSerializ();

        // Final : applyRemote all pending remote CRDTMessage (not the best complexity)
        for (CRDT r : replicas.values()) {
            int n = r.getReplicaNumber();

            concurrentOps.clear();
            VectorClock vc = clocks.get(n);
            for (Entry<Integer, Integer> e : globalClock.entrySet()) {
                for (int j = vc.getSafe(e.getKey()); j < e.getValue(); ++j) {
                    insertCausalOrder(concurrentOps, history.get(e.getKey()).get(j));
                }
            }
            play(r, vc, concurrentOps);
        }
        

    }

    private static void insertCausalOrder(List<TraceOperation> concurrentOps, TraceOperation opt) {
        final ListIterator<TraceOperation> it = concurrentOps.listIterator();
        boolean cont = true;
        while (it.hasNext() && cont) {
            TraceOperation t = it.next();
            if (t.getVectorClock().greaterThan(opt.getVectorClock())) {
                cont = false;
                it.previous();
            }
        }
        it.add(opt);
    }

    /**
     *
     * @param r
     * @param vc
     * @param concurrentOps
     * @throws IOException
     * @throws IncorrectTraceException
     */
    private void play(CRDT r, VectorClock vc, List<TraceOperation> concurrentOps) throws IOException, IncorrectTraceException {
        for (TraceOperation t : concurrentOps) {
            int e = t.getReplica();
            CRDTMessage op = genHistory.get(e).get(t.getVectorClock().get(e) - 1);
            CRDTMessage optime = op.clone();
            if (!vc.readyFor(e, t.getVectorClock())) {
                throw new IncorrectTraceException("replica " + r.getReplicaNumber() + " with vc " + vc + " not ready for " + t.getVectorClock());
            }

            long before = System.nanoTime();
            r.applyRemote(optime);
            long after = System.nanoTime();

            remoteSum += (after - before);
            if (detail) {
                int num = orderTrace.get(t);
                remoteTime.set(num, remoteTime.get(num) + after - before);
                //stat(t, after - tmp, 1);
            }
            nbRemote++;
            vc.inc(e);
        }
    }

    /**
     * Reset all replicas
     */
    public void reset() {
        replicas.clear();
    }

    private void causalCheck(TraceOperation opt, Map<Integer, VectorClock> vcs) throws IncorrectTraceException {
        int r = opt.getReplica();
        if (opt.getVectorClock().getSafe(r) == 0) {
            throw new IncorrectTraceException("Zero/no entry in VC for replica" + opt);
        }
        VectorClock clock = vcs.get(r);
        if (clock.getSafe(r) > opt.getVectorClock().get(r) - 1) {
            throw new IncorrectTraceException("Already seen clock operation " + opt);
        }
        if (clock.getSafe(r) < opt.getVectorClock().getSafe(r) - 1) {
            throw new IncorrectTraceException("Missing operation before " + opt);
        }
        for (int i : opt.getVectorClock().keySet()) {
            if ((i != r) && (opt.getVectorClock().get(i) > 0)) {
                if (vcs.get(i) == null) {
                    throw new IncorrectTraceException("Missing replica " + i + " for " + opt);
                }
                if (vcs.get(i).getSafe(i) < opt.getVectorClock().get(i)) {
                    throw new IncorrectTraceException("Missing causal operation before " + opt + " on replica " + i);
                }
            }
        }
    }

    private void ifSerializ() throws IOException {
        if (nbrTrace > 0 && tour == nbrTrace && serializer != null) {
            long SumMemory = 0;
            for (int rep : this.getReplicas().keySet()) {
                SumMemory += serializer.serializ(this.getReplicas().get(rep));
            }
            memUsed.add(new Long(SumMemory / this.getReplicas().keySet().size()));
            tour = 0;

            //debug
            if (debugInformation && memUsed.size() % 10 == 0) {
                System.out.println("Serialized :" + memUsed.size() * 100 + "x");
            }
        }
    }
    
    public List<Double> getAvgPerRemoteMessage(){
        List<Double> l = new LinkedList();
        double div=(double)(this.genHistory.size()-1);
        for (int i = 0; i < remoteTime.size(); ++i) {
            l.add((double)remoteTime.get(i) / div);
        }
        return l;
    }
     public List<Long> getAvgLongPerRemoteMessage(){
        List<Long> l = new LinkedList();
        long div=this.genHistory.size()-1;
        for (int i = 0; i < remoteTime.size(); ++i) {
            l.add(remoteTime.get(i) / div);
        }
        return l;
    }
/**
 * Return list of integration by remote operation
 * One message can contain many operations
 * @return list of long
 */
     public List<Long> getAvgLongPerRemoteOperation() {
        List<Long> l = new ArrayList();
        for (int i = 0; i < remoteTime.size(); ++i) {
            int gs = genSize.get(i);
            long t = remoteTime.get(i) / gs;
            for (int j = 0; j < gs; ++j) {
                l.add(t);
            }
        }
        return l;
    }
}
