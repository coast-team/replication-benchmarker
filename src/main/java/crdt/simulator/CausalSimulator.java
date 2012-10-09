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
package crdt.simulator;

import collect.VectorClock;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.git.GitOperation;

/**
 *
 * @author urso
 */
public class CausalSimulator extends Simulator {

    public CausalSimulator(Factory<? extends CRDT> rf) {
        super(rf);
    }
    int tour = 0;
    boolean overhead = false;
    HashSet<CRDTMessage> setOp;
    private Map<Integer, List<TraceOperation>> history;
    private Map<Integer, List<CRDTMessage>> genHistory;
    private long localSum = 0L, nbLocal = 0L, remoteSum = 0L, nbRemote = 0L;
    private int nbrTrace = 0;
    private long sumMemory = 0L;
    public Map<Integer, List<CRDTMessage>> getGenHistory() {
        return genHistory;
    }

    public Map<Integer, List<TraceOperation>> getHistory() {
        return history;
    }
    
    public long getSumMem() {
        return sumMemory;
    }
    
    public long getAvgMem() {
        return sumMemory/this.getReplicas().keySet().size();
    }
    
    public long getLocalSum() {
        return localSum;
    }

    public long getNbLocal() {
        return nbLocal;
    }

    public long getNbRemote() {
        return nbRemote;
    }

    public long getRemoteSum() {
        return remoteSum;
    }
    public double getRemoteAvg(){
        return remoteSum/((double)nbRemote);
    }
    public double getLocalAvg(){
        return localSum/((double)nbLocal);
    }
    ObjectOutputStream writer = null;
    //Viewer view =null; /*new DiagSequence(20);*/
    /**
     * Runs a causally ordered trace. Throws exception if not causally ordered
     * trace or pb with classes.
     */
    
    public void clearStat(){
        localSum = 0L;
        nbLocal = 0L;
        remoteSum = 0L;
        nbRemote = 0L;
    }

    private HashMap<TraceOperation, Integer> orderTrace;
    private boolean detail;
      
    
    @Override
    public void run(Trace trace, boolean detail, boolean saveTrace, int nbrTrace, boolean o) throws IncorrectTraceException, PreconditionException, IOException {
        this.detail = detail;
        this.nbrTrace = nbrTrace;
        overhead = o;
        long tmp;
        final Map<Integer, VectorClock> clocks = new HashMap<Integer, VectorClock>();
        final VectorClock globalClock = new VectorClock();
        final List<TraceOperation> concurrentOps = new LinkedList<TraceOperation>();
        final Enumeration<TraceOperation> it = trace.enumeration();
        orderTrace = new HashMap();        
        int numTrace = 0;

        if (saveTrace){
            writer = new ObjectOutputStream(new FileOutputStream("trace"));
        }
        
        setOp = new HashSet();
        history = new HashMap<Integer, List<TraceOperation>>();
        genHistory = new HashMap<Integer, List<CRDTMessage>>();
        while (it.hasMoreElements()) {
            tour++;

            final TraceOperation opt = it.nextElement();  
 
            final int r = opt.getReplica();             
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

            if (saveTrace) {
                storeOp(opt);
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

        if (writer != null) {
            writer.close();
        }
    }

    public static void insertCausalOrder(List<TraceOperation> concurrentOps, TraceOperation opt) {
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
    
    void play(CRDT r, VectorClock vc, List<TraceOperation> concurrentOps) throws IOException, IncorrectTraceException {
        for (TraceOperation t : concurrentOps) {
            int e = t.getReplica();
            CRDTMessage op = genHistory.get(e).get(t.getVectorClock().get(e) - 1);
            CRDTMessage optime = op.clone();
            if (!vc.readyFor(e, t.getVectorClock())) {
                throw new IncorrectTraceException("replica " + r.getReplicaNumber() + " with vc " + vc + " not ready for " + t.getVectorClock());
            }
            long tmp = System.nanoTime();
            r.applyRemote(optime);
            long after = System.nanoTime();
            remoteSum += (after - tmp);
            if (detail) {
                int num = orderTrace.get(t);
                remoteTime.set(num, remoteTime.get(num) + after - tmp);
            }
            nbRemote++;
            vc.inc(e);
            ifSerializ();
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

    public void serializ(CRDT m) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteOutput);
        if(overhead)
            stream.writeObject(m);
        else
            stream.writeObject(m.lookup());
        sumMemory +=  byteOutput.size();
        
        stream.flush();
        stream.close();
        byteOutput.flush();
        byteOutput.close();    
    }
    
    public void ifSerializ() throws IOException {
        if (nbrTrace > 0 && tour == nbrTrace) {
            for (int rep : this.getReplicas().keySet()) {
                serializ(this.getReplicas().get(rep));
            }
            memUsed.add(this.getAvgMem());
            sumMemory = 0;
            tour = 0;
            
            //debug
            if (memUsed.size() % 10 == 0) {
                System.out.println("Serialized :" + memUsed.size()*100 + "x");
            }
        }
    }

    public List<Long> splittedGenTime() {
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
    

    public void storeOp(TraceOperation op) {
        // TODO : generalize for any kind of operation
       if (writer==null)
           return;
       try {
            writer.writeObject(op);
        } catch (IOException ex) {
            Logger.getLogger(CausalSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
