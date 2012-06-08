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
package jbenchmarker.trace;

import jbenchmarker.core.SequenceOperation;
import crdt.simulator.IncorrectTraceException;
import crdt.CRDT;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.ReplicaFactory;
import collect.VectorClock;
import jbenchmarker.sim.PlaceboFactory.PlaceboDocument;

/**
 * Check that operation are received in causal order.
 * @author urso
 */
public class CausalCheckerFactory extends ReplicaFactory {
//   @Override
    private static class CCMerge extends MergeAlgorithm {

        public CCMerge(int r) {
            super(new PlaceboDocument(), r);
        }
        
        private VectorClock vc = new VectorClock();

        @Override
        protected void integrateLocal(SequenceMessage op) throws IncorrectTraceException {
            check(op.getOriginalOp());
            this.getDoc().apply(op);
            vc.inc(op.getOriginalOp().getReplica());
        }

        @Override
        protected List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException {
            check(opt);
            List<SequenceMessage> l = new ArrayList<SequenceMessage>(1);
            SequenceMessage op = new SequenceMessage(opt) {

                public SequenceMessage copy() {
                    return this;
                }
            };
            l.add(op);
            this.getDoc().apply(op);
            vc.inc(this.getReplicaNumber());
            return l;
        }

        private void check(SequenceOperation op) throws IncorrectTraceException {
            if (!vc.readyFor(op.getReplica(), op.getVectorClock())) {
                throw new IncorrectTraceException("Replica " + this.getReplicaNumber() + "[vc:"+this.vc+"] not ready for " + op);
            }
        }

        @Override
        public CRDT<String> create() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    @Override
    public MergeAlgorithm create(int r) {
        return new CCMerge(r);                
    }
}
