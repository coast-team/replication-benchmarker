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

import collect.VectorClock;
import crdt.CRDT;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 * A profile that generates operation.
 * T is either a character or a string 
 * @author urso
 */
public abstract class SequenceOperationProfile<T> implements OperationProfile {
    protected final RandomGauss r = new RandomGauss();

    abstract public SequenceOperation.OpType nextType();
    
    public int nextPosition(int length) {
       return (int) (length*r.nextDouble());
    }
    
    abstract public List<T> nextContent();

    abstract public int nextOffset(int position, int l);

    @Override
    public Operation nextOperation(CRDT crdt, VectorClock vectorClock) {
        Document replica = ((MergeAlgorithm) crdt).getDoc();

        int l = replica.viewLength();
        int position = nextPosition(l);
        OpType type = (l == 0) ? OpType.ins : nextType();
        int offset = (type == OpType.ins) ? 0 : nextOffset(position, l);
        List<T> content = (type == OpType.del) ? null : nextContent(); 

        return new SequenceOperation<T>(type, crdt.getReplicaNumber(), position, offset, content, vectorClock);
    }
}
