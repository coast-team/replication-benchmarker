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
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Garbage collector for soct2 log
 * TODO : Test Me !!!!! 
 * @author oster
 */
public class SOCT2GarbageCollector extends AbstractGarbageCollector {

    private int numberOfReplica;
    
    /**
     * New garbage collector instance
     * @param merger soct2 algorithme instance
     * @param frequencyGC  operation frequency to run the garbage collection >0
     */
    public SOCT2GarbageCollector(int frequencyGC, int numberOfReplica) {
        super(frequencyGC);
        this.numberOfReplica = numberOfReplica;
    }
    
    /**
     * New garbage collector instance with RECOMMANDED_GC_FREQUENCY_VALUE (20 operations)
     * Get soct2 algorithm instance
     * @param merger
     */
    public SOCT2GarbageCollector(int numberOfReplica) {
        this(RECOMMANDED_GC_FREQUENCY_VALUE, numberOfReplica);
    }

    /**
     * Not to be used after gc.
     * @param numberOfReplica 
     */
    void setNumberOfReplica(int numberOfReplica) {
        this.numberOfReplica = numberOfReplica;
    }

    @Override
    protected void gc(OTAlgorithm otAlgorithm) {
        if (clocksOfAllSites.entrySet().size() == numberOfReplica-1) {
            VectorClock commonAncestorVectorClock = 
                    otAlgorithm.getSiteVC().min(otAlgorithm.getReplicaNumber(), this.clocksOfAllSites);
            int garbagePoint = 
                    otAlgorithm.getLog().separatePrecedingAndConcurrentOperations(commonAncestorVectorClock, 0);
            
            otAlgorithm.getLog().purge(garbagePoint);
        }
//        Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "gc removed {0} operation(s) from a total of {1} operation(s)", new Object[]{count, count + this.mergeAlgorithm.getHistoryLog().getSize()});
    }
 
    @Override
    public GarbageCollector create() {
        return new SOCT2GarbageCollector(frequencyGC, numberOfReplica);
    }
}
