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
public class PreemptiveGarbageCollector extends SOCT2GarbageCollector {
    private int clock = 0;
    private final Map<Integer, Integer> lastCollect = new HashMap<Integer, Integer>(); 
    private final int delay;
    private final Set<Integer> ghosts = new HashSet<Integer>();
    private final List<OTMessage<? extends Operation>> purged = new LinkedList<OTMessage<? extends Operation>>(); 
    
    public PreemptiveGarbageCollector(int numberOfReplica, int delay) {
        super(numberOfReplica);
        this.delay = delay;
    }

    public PreemptiveGarbageCollector(int frequencyGC, int numberOfReplica, int delay) {
        super(frequencyGC, numberOfReplica);
        this.delay = delay;
    }

    @Override
    protected void setRemoteClock(int siteId, VectorClock vclock) {
        super.setRemoteClock(siteId, vclock);
        lastCollect.put(siteId, clock++);
    }

    @Override
    protected VectorClock computeMin(OTAlgorithm otAlgorithm) {
        Map<Integer, VectorClock> clocks = new TreeMap<Integer, VectorClock>(clocksOfAllSites);
        Iterator<Entry<Integer, VectorClock>> it = clocks.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, VectorClock> e = it.next();
            int replica = e.getKey();
            if (lastCollect.get(replica) < clock - delay) {
                it.remove();
                ghosts.add(replica);
            } else if (ghosts.contains(replica)) {
                ghosts.remove(replica);
                otAlgorithm.getLog().insertAll(purged);
                purged.clear();
            }
        }
        return otAlgorithm.getSiteVC().min(otAlgorithm.getReplicaNumber(), clocks);
    }

    @Override
    protected void purge(SOCT2Log log, int garbagePoint) {
        purged.addAll(log.begin(garbagePoint));
        super.purge(log, garbagePoint);
    }
}
