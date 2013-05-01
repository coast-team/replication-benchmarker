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

import java.util.List;
import jbenchmarker.core.Operation;


/**
 *
 * @author urso
 */
public class SOCT2LogOptimizedLast<Op extends Operation> extends SOCT2Log<Op> {

    int lastSeparationIndex = 0;
    
    public SOCT2LogOptimizedLast(SOCT2TranformationInterface t) {
        super(t);
    }

    @Override
    public Op merge(OTMessage<Op> operation) {
        int startSeparation = 0;
        if (operations.size() > 0) {
            OTMessage<Op> last = getLast();
            if (last.getClock().get(last.getSiteId()) <= operation.getClock().getSafe(last.getSiteId())) {
                startSeparation = lastSeparationIndex;
            }
        }
        lastSeparationIndex = separatePrecedingAndConcurrentOperations(operation.getClock(), startSeparation);
        return placeOperation(operation, lastSeparationIndex);            
    }

    @Override
    void purge(int purgePoint) {
        super.purge(purgePoint);
        lastSeparationIndex = Math.max(0, lastSeparationIndex - purgePoint);
    }

    @Override
    void insertAll(List<OTMessage<Op>> purged) {
        super.insertAll(purged);
        lastSeparationIndex = 0;
    }

    @Override
    public SOCT2Log<Op> create() {
        return new SOCT2LogOptimizedLast<Op>(transforme);
    }

    protected OTMessage<Op> getLast() {
        return operations.get(operations.size()-1);
    }
}
