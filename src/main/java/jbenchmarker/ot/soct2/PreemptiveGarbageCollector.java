/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import java.util.*;
import java.util.Map.Entry;
import jbenchmarker.core.Operation;

/**
 *
 * @author urso
 */
public class PreemptiveGarbageCollector extends AbstractGarbageCollector {
    private int clock = 0;
    private final Map<Integer, Integer> lastCollect = new HashMap<Integer, Integer>(); 
    private final int delay;
    private final Set<Integer> alive = new HashSet<Integer>();
    private final List<OTMessage<? extends Operation>> purged = new LinkedList<OTMessage<? extends Operation>>(); 
    
    public PreemptiveGarbageCollector(int delay) {
        super();
        this.delay = delay;
    }

    public PreemptiveGarbageCollector(int frequencyGC, int delay) {
        super(frequencyGC);
        this.delay = delay;
    }

    @Override
    public void collect(OTAlgorithm soct2Algorithm, OTMessage mess) {
        super.collect(soct2Algorithm, mess);
        lastCollect.put(mess.getSiteId(), clock++);
        if (!alive.contains(mess.getSiteId())) {
            alive.add(mess.getSiteId());
            soct2Algorithm.getLog().insertAll(purged);
            purged.clear();
        }
    }

    @Override
    protected void gc(OTAlgorithm otAlgorithm) {
        Map<Integer, VectorClock> clocks = new TreeMap<Integer, VectorClock>();
        Iterator<Integer> it = alive.iterator();
        while (it.hasNext()) {
            int replica = it.next();
            if (lastCollect.get(replica) < clock - delay) {
                it.remove();
            } else {
                clocks.put(replica, clocksOfAllSites.get(replica));
            }
        }
        VectorClock commonAncestorVectorClock = otAlgorithm.getSiteVC().min(otAlgorithm.getReplicaNumber(), clocks);
        int garbagePoint = otAlgorithm.getLog().separatePrecedingAndConcurrentOperations(commonAncestorVectorClock, 0);
        
        purged.addAll(otAlgorithm.getLog().begin(garbagePoint));
        otAlgorithm.getLog().purge(garbagePoint);
    }

    @Override
    public GarbageCollector create() {
        return new PreemptiveGarbageCollector(frequencyGC, delay);
    }
}
