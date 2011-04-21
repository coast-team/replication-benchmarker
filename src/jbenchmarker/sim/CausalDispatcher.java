/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import jbenchmarker.core.*;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

/**
 *
 * @author urso
 */
public class CausalDispatcher  extends Simulator {
    
    public CausalDispatcher(ReplicaFactory rf) {
        super(rf);
    }

    
    /**
     * Runs a causally ordered trace. Throws exception if not causally ordered 
     * trace or pb with classes.
     */
    public void run(Iterator<TraceOperation> trace) throws IncorrectTrace {        
        final Map<Integer, List<TraceOperation>> history = new HashMap<Integer, List<TraceOperation>>();
        final Map<Integer, List<List<Operation>>> genHistory = new HashMap<Integer, List<List<Operation>>>();
        final Map<Integer, VectorClock> clocks = new HashMap<Integer, VectorClock>();
        final VectorClock globalClock = new VectorClock();
        final List<TraceOperation> concurrentOps = new LinkedList<TraceOperation>();
        
        while (trace.hasNext()) {
            final TraceOperation opt = trace.next();
            final int r = opt.getReplica();
            MergeAlgorithm a = this.getReplicas().get(r);

            if (a == null) {     
                a = this.newReplica(r); 
                clocks.put(r, new VectorClock());
                genHistory.put(r, new ArrayList<List<Operation>>());
                history.put(r, new ArrayList<TraceOperation>());
            }
            history.get(r).add(opt);
            // TraceGenerator.causalCheck(opt, clocks);
            
            VectorClock vc = clocks.get(r);

            if (!vc.readyFor(r, opt.getVC())) {
                // Integrate concurrent operations
                Iterator<Integer> i = opt.getVC().keySet().iterator();
                concurrentOps.clear();
                while (i.hasNext()) {
                    int e = i.next();
                    if (e != r) {
                        for (int j = opt.getVC().get(e); j > vc.getSafe(e); j--) {
                            insertCausalOrder(concurrentOps,  history.get(e).get(j-1));
                        }
                    }
                }
                for (TraceOperation t : concurrentOps) {
                    int e = t.getReplica();
                    for (Operation op : genHistory.get(e).get(t.getVC().get(e)-1)) {
                        a.integrate(op.clone()); 
                    }
                    vc.inc(e);
                }
            }
            final List<Operation> lop = duplicate(a.generate(opt));
            genHistory.get(r).add(lop);
            clocks.get(r).inc(r);
            globalClock.inc(r);
            memUsed.add(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
            genTime.add(a.lastExecTime());
        }
          
        Set<Integer> notComplete = new TreeSet<Integer>();
        notComplete.addAll(replicas.keySet());
   
        // Final : integrate all pending remote operation (not the best complexity)
        while (!notComplete.isEmpty()) {
            Iterator<Integer> i = notComplete.iterator();
            while (i.hasNext()) {
                int r = i.next();
                MergeAlgorithm a = this.getReplicas().get(r);
                VectorClock vc = clocks.get(r);
                if (vc.equals(globalClock)) {
                    i.remove();
                } else {
                    for (int s : replicas.keySet()) {
                        for (int j = vc.getSafe(s); (j < globalClock.get(s))
                                && vc.readyFor(s, history.get(s).get(j).getVC()); j++) {
                            for (Operation op : genHistory.get(s).get(j)) {
                                a.integrate(op.clone());
                            }
                            vc.inc(s);
                        }
                    }
                }
            }
        }
    }

    public static void insertCausalOrder(List<TraceOperation> concurrentOps, TraceOperation opt) {
        final ListIterator<TraceOperation> it = concurrentOps.listIterator();
        boolean cont = true;
        while (it.hasNext() && cont) {
            TraceOperation t = it.next();
            if (t.getVC().greaterThan(opt.getVC())) {
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
    
    
    public static List<Operation> duplicate(List<Operation> list) {
        ArrayList<Operation> res = new ArrayList<Operation>();
        for (Operation elt : list) {
            res.add(elt.clone());
        }
        return res;
    }

}
