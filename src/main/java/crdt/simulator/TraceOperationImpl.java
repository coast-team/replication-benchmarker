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
package crdt.simulator;

import collect.VectorClock;
import crdt.CRDT;
import java.io.Serializable;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
final public class TraceOperationImpl extends TraceOperation implements Serializable {
    LocalOperation op;
    
    public TraceOperationImpl() {
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(LocalOperation op) {
        this.op = op;
    }

    
    
    public TraceOperationImpl(LocalOperation op,int replica, VectorClock VC) {
        super(replica, VC);
        this.op=op;
    }
/**
 * I don't know with kind of algorithm.
 * Its place is on the algorithm itself
 * @param replica
 * @return 
 */
    @Override 
    public LocalOperation getOperation() {
        return op;
    }

    @Override
    public String toString() {
        return "TraceOperationImpl{" + "op=" + op +"N°Rep="+this.getReplica()+"VC="+this.getVectorClock()+ '}';
    }

   
    
}
