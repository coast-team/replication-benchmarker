/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package crdt.simulator.random;

import crdt.simulator.*;
import collect.VectorClock;
import crdt.CRDT;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.Operation;

/**
 *
 * @author urso
 */
public class RandomOperation extends TraceOperation {

    final private OperationProfile opp;

    public RandomOperation(OperationProfile opp, int replica, VectorClock VC) {
        super(replica, VC);
        this.opp = opp;
    }

    @Override
    public Operation getOperation(CRDT replica) {
        return opp.nextOperation(replica, getVectorClock());
    }
      @Override
    public Operation clone() {
        try {
            return (Operation) super.clone();
        } catch (Exception ex) {
            Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
