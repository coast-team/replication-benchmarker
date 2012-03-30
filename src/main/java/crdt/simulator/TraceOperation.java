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
package crdt.simulator;

import collect.VectorClock;
import crdt.CRDT;
import crdt.Operation;

/**
 *
 * @author urso
 */
abstract public class TraceOperation implements Operation {
    
    final private int replica;
    final private VectorClock VC;

    public TraceOperation(int replica, VectorClock VC) {
        this.replica = replica;
        this.VC = VC;
    }

    public VectorClock getVectorClock() {
        return VC;
    }

    public int getReplica() {
        return replica;
    }

    abstract public Operation getOperation(CRDT replica);

    @Override
    public String toString() {
        return "TO{VC=" + VC + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TraceOperation other = (TraceOperation) obj;
        if (this.replica != other.replica) {
            return false;
        }
        if (this.VC != other.VC && (this.VC == null || !this.VC.equals(other.VC))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.replica;
        hash = 23 * hash + (this.VC != null ? this.VC.hashCode() : 0);
        return hash;
    }
}
