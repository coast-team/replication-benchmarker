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
package crdt.simulator.random;

import crdt.CRDT;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.core.LocalOperation;

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
    public LocalOperation nextOperation(CRDT crdt) {
        Document replica = ((MergeAlgorithm) crdt).getDoc();

        int l = replica.viewLength();
        int position = nextPosition(l);
        OpType type = (l == 0) ? OpType.insert : nextType();
        int offset = (type == OpType.insert) ? 0 : nextOffset(position, l);
        List<T> content = (type == OpType.delete) ? null : nextContent(); 

        return new SequenceOperation<T>(type,  position, offset, content);
    }
}
