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

package jbenchmarker.ot;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.VectorClock;

/**
 *
 * @author oster
 */
public class OperationGarbageCollector {

    private SOCT2MergeAlgorithm mergeAlgorithm;
    private Map<Integer, VectorClock> clocksOfAllSites = new TreeMap<Integer, VectorClock>();
    private static final int GC_FREQUENCY_IN_OPERATIONS = 20;
    private int countdownBeforeGC = GC_FREQUENCY_IN_OPERATIONS;

    public OperationGarbageCollector(SOCT2MergeAlgorithm merger) {
        this.mergeAlgorithm = merger;
    }

    public void collect(TTFOperation op) {
        this.clocksOfAllSites.put(op.getSiteId(), op.getClock());

        this.countdownBeforeGC--;
        if (this.countdownBeforeGC == 0) {
            gc();
            this.countdownBeforeGC = GC_FREQUENCY_IN_OPERATIONS;
        }
    }

    private void gc() {
        VectorClock commonAncestorVectorClock = mergeAlgorithm.getClock().min(this.clocksOfAllSites.values());

        Iterator<TTFOperation> it = this.mergeAlgorithm.getHistoryLog().iterator();
        int count = 0;
        while (it.hasNext()) {
            if (!it.next().getClock().greaterThan(commonAncestorVectorClock)) {
                it.remove();
                count++;
            }
        }

//        Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "gc removed {0} operation(s) from a total of {1} operation(s)", new Object[]{count, count + this.mergeAlgorithm.getHistoryLog().getSize()});
    }
}
