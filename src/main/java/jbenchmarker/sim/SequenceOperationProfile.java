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
package jbenchmarker.sim;

import crdt.CRDT;
import crdt.Operation;
import crdt.simulator.random.OperationProfile;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.trace.TraceOperation.OpType;

/**
 * A profile that generates operation.
 * @author urso
 */
public abstract class SequenceOperationProfile implements OperationProfile {

    abstract public TraceOperation.OpType nextType();
    
    abstract public int nextPosition(int length);
    
    abstract public String nextContent();

    abstract public int nextOffset(int position, int l);

    @Override
    public Operation nextOperation(CRDT crdt) {
        Document replica = ((MergeAlgorithm) crdt).getDoc();

        int l = replica.view().length();
        int position = nextPosition(l);

        if (l == 0 || nextType() == OpType.ins) {
            String content = nextContent();
            return TraceOperation.insert(crdt.getReplicaNumber(), position, content, null);
        } else {
            int offset = nextOffset(position, l);
            return TraceOperation.delete(crdt.getReplicaNumber(), position, offset, null);
        }
    }
}
