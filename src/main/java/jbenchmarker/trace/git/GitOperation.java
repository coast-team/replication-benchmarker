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
package jbenchmarker.trace.git;

import collect.VectorClock;
import crdt.CRDT;
import crdt.simulator.TraceOperation;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.trace.git.model.Edition;
import jbenchmarker.trace.git.model.FileEdition;

/**
 * Mapping between couch extracted git trace and SequenceOperation
 * @author urso
 */
public class GitOperation extends TraceOperation {
    SequenceOperation<String> sop;
    String commitRev;
        
    public GitOperation(int replica, VectorClock VC, FileEdition f, Edition e, String commit) {
        super(replica, new VectorClock(VC));
        int arg = 0;
        commitRev = commit;
        switch (e.getType()) {
            case delete: 
            case replace:
            case update:
                arg = e.sizeA();
                break;
            case move:
                arg = e.getDestMove();
                break;
        }
        sop = new SequenceOperation<String>(e.getType(), e.getBeginA(), arg, e.getCb()) {
            @Override // Must always be ok
            public LocalOperation adaptTo(CRDT replica) {
                return this;
            }
        };
    }

    @Override
    public LocalOperation getOperation() {
        return sop;
    }

    @Override
    public String toString() {
        return "GitOperation{super="+super.toString() + "sop=" + sop + '}';
    }

   
}
