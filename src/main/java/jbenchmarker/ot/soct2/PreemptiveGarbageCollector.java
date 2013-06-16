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
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import java.util.*;
import java.util.Map.Entry;
import crdt.Operation;

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
