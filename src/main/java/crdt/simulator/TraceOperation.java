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
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @author urso
 */
abstract public class TraceOperation implements Serializable{
    
     private int replica;
    private VectorClock vectorClock;

    public TraceOperation() {
    }
    
    public TraceOperation(int replica, VectorClock VC) {
        this.replica = replica;
        this.vectorClock = VC;
    }

   /* public VectorClock getVC() {
        return VC;
    }*/

    public void setVectorClock(VectorClock VC) {
        this.vectorClock = VC;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public void setReplica(int replica) {
        this.replica = replica;
    }

    public int getReplica() {
        return replica;
    }
    
    public abstract LocalOperation getOperation();
    //abstract public Operation getOperation(CRDT replica);/* Why an operation on a trace depend on replica ?*/

    @Override
    public String toString() {
        return "TO{N°Rep="+replica+", VC=" + vectorClock +'}';
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
        if (this.vectorClock != other.vectorClock && (this.vectorClock == null || !this.vectorClock.equals(other.vectorClock))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.replica;
        hash = 23 * hash + (this.vectorClock != null ? this.vectorClock.hashCode() : 0);
        return hash;
    }
      @Override
    public TraceOperation clone() {
        try {
            return (TraceOperation) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
