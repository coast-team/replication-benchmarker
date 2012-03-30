/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2011
 * INRIA / LORIA / SCORE Team
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
 * You should have received a clone of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.simulator;

import collect.VectorClock;
import crdt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author urso
 */
public class CausalSimulator extends Simulator {

    public CausalSimulator(Factory<CRDT> rf) {
        super(rf);
    }
    int tour = 0;
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
    
    @Override
    public void run(Trace trace) throws IncorrectTraceException, PreconditionException, IOException {
        long tmp;
        final Map<Integer, VectorClock> clocks = new HashMap<Integer, VectorClock>();
        final VectorClock globalClock = new VectorClock();
        final List<TraceOperation> concurrentOps = new LinkedList<TraceOperation>();
        final Enumeration<TraceOperation> it = trace.enumeration();
        
        setOp = new HashSet();
        history = new HashMap<Integer, List<TraceOperation>>();
        genHistory = new HashMap<Integer, List<CRDTMessage>>();
        while (it.hasMoreElements()) {
            tour++;
            final TraceOperation opt = it.nextElement();
            final int r = opt.getReplica();
            CRDT a = this.getReplicas().get(r);
            
            if (a == null) {
                a = this.newReplica(r);
                clocks.put(r, new VectorClock());
                genHistory.put(r, new ArrayList<CRDTMessage>());
                history.put(r, new ArrayList<TraceOperation>());
            }
            history.get(r).add(opt);

            // For testing can be removed
            causalCheck(opt, clocks);

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
                for (TraceOperation t : concurrentOps) {

                    int e = t.getReplica();
                    CRDTMessage op = genHistory.get(e).get(t.getVectorClock().get(e) - 1);
                    CRDTMessage optime = op.clone();

                    tmp = System.nanoTime();
                    a.applyRemote(optime);
                    insertRemoteTime(r, e, System.nanoTime() - tmp);
                    remoteSum += (System.nanoTime() - tmp);
                    nbRemote++;
                    vc.inc(e);
                }
            }

            Operation op = opt.getOperation(a);

            tmp = System.nanoTime();
            final CRDTMessage m = a.applyLocal(op);
            insertRemoteTime(r, r, 0L);
            genTime.add(System.nanoTime() - tmp);
            localSum += (System.nanoTime() - tmp);
            nbLocal++;

            final CRDTMessage msg = m.clone();
            genHistory.get(r).add(msg);
            clocks.get(r).inc(r);
            globalClock.inc(r);
            
            ifSerializ();
        }
        ifSerializ();
        
        Set<Integer> notComplete = new TreeSet<Integer>();
        notComplete.addAll(replicas.keySet());

        // Final : applyRemote all pending remote CRDTMessage (not the best complexity)
        while (!notComplete.isEmpty()) {

            Iterator<Integer> i = notComplete.iterator();
            while (i.hasNext()) {
                int r = i.next();
                CRDT a = this.getReplicas().get(r);
                VectorClock vc = clocks.get(r);
                if (vc.equals(globalClock)) {
                    i.remove();
                } else {
                    for (int s : replicas.keySet()) {
                        for (int j = vc.getSafe(s); (j < globalClock.get(s))
                                && vc.readyFor(s, history.get(s).get(j).getVectorClock()); j++) {
                            CRDTMessage op = genHistory.get(s).get(j);
                            CRDTMessage optime = op.clone();

                            tmp = System.nanoTime();
                            a.applyRemote(optime);
                            insertRemoteTime(r, s, System.nanoTime() - tmp);
                            remoteSum += (System.nanoTime() - tmp);
                            nbRemote++;
                            tour++;
                            vc.inc(s);
                            ifSerializ();                                
                        }
                    }
                }
            }
        }
        ifSerializ();
    }

    void insertRemoteTime(Integer r, Integer e, Long t) {
        if (remoteTime.containsKey(r)) {
            ArrayList p = remoteTime.get(r);
            Hashtable hs = new Hashtable();
            hs.put(e, t);
            p.add(hs);
        } else {
            ArrayList<Hashtable<Integer, Long>> l = new ArrayList();
            Hashtable<Integer, Long> hs = new Hashtable();
            hs.put(e, t);
            l.add(hs);
            remoteTime.put(r, l);
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
    
    public void runWithMemory(Trace trace, int nbrTrace) throws IncorrectTraceException, PreconditionException, IOException
    {
        this.nbrTrace = nbrTrace;
        run(trace);
    }
    
    public void serializ(CRDT m ) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteOutput);
        stream.writeObject(m);
        sumMemory +=  byteOutput.toByteArray().length;
        
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
            if (memUsed.size() % 100 == 0) {
                System.out.println("Serialized :" + memUsed.size() + " x");
            }
        }
    }

//    public void serializ(CRDT m) throws IOException {
//
//        FileOutputStream fichier = new FileOutputStream("File.ser");
//        //long s = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//        ObjectOutputStream oos = new ObjectOutputStream(fichier);
//        oos.writeObject(m);
//        sumMemory +=  fichier.getChannel().size();
//        oos.flush();
//        oos.close();
//    }
    
    
//    void insertRemoteTime(Integer r, Integer e, Long t) {
//        Map m = new Hashtable();
//        m.put(e, t);
//        if (remoteTime.containsKey(r)) {
//            ArrayList p = remoteTime.get(r);
//            p.add(m);
//        } else {
//            ArrayList<Hashtable<Integer, Long>> ar = new ArrayList();
//            Hashtable<Integer, Long> h = new Hashtable();
//            h.put(e, t);
//            ar.add(h);
//            remoteTime.put(r, ar);
//        }
//    }    
    
//    public void addOpLocal(int e) {
//        Hashtable<Integer, Long> table = new Hashtable();
//        table.put(e, (long) 0);
//        if (remoteTime.containsKey(e)) {
//            ArrayList p = remoteTime.get(e);
//            p.add(table);
//        } else {
//            ArrayList<Hashtable<Integer, Long>> ar = new ArrayList();
//            ar.add(table);
//            remoteTime.put(e, ar);
//        }
//    }
}
