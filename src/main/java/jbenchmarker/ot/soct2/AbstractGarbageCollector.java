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
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author urso
 */
public abstract class AbstractGarbageCollector implements GarbageCollector, Serializable {
    /**
     * Recommanded number operation before garbage collecting (20)
     */
    public static final int RECOMMANDED_GC_FREQUENCY_VALUE = 20;
    final protected int frequencyGC;
    private int countdownBeforeGC;
    protected final Map<Integer, VectorClock> clocksOfAllSites = new TreeMap<Integer, VectorClock>();


    public AbstractGarbageCollector(int frequencyGC) {
        this.frequencyGC = frequencyGC;
        this.countdownBeforeGC = frequencyGC;
    }
    
    public AbstractGarbageCollector() {
        this(RECOMMANDED_GC_FREQUENCY_VALUE);
    }

    /**
     * register the soct2message to get anothers vector clock and count before run
     * @param mess Soct2messages
     */
    @Override
    public void collect(OTAlgorithm soct2Algorithm, OTMessage mess) {
         clocksOfAllSites.put(mess.getSiteId(), mess.getClock());
    }
    
    @Override
    public void garbage(OTAlgorithm soct2Algorithm) {
        this.countdownBeforeGC--;
        if (this.countdownBeforeGC == 0) {
            gc(soct2Algorithm);
            this.countdownBeforeGC = frequencyGC;
        }
    }
    
    abstract protected void gc(OTAlgorithm soct2Algorithm);
}
