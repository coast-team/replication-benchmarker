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
import crdt.simulator.IncorrectTraceException;
import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.core.SequenceOperation;


/**
 * Used to measure the base memory/time required to simulate a trace.
 * @author urso
 */
public class PlaceboFactory extends ReplicaFactory {
    public static class PlaceboOperation extends SequenceMessage {

        public PlaceboOperation(SequenceOperation o) {
            super(o);
        }

        @Override
        public SequenceMessage copy() {
            return new PlaceboOperation(this.getOriginalOp());
        }
    }
    
    public static class PlaceboDocument implements Document {

        @Override
        public String view() {
            return "";
        }

        @Override
        public void apply(SequenceMessage op) {
        }

        @Override
        public int viewLength() {
            return 0;
        }
    }
    
    private static class PlaceboMerge extends MergeAlgorithm {

        PlaceboMerge() {
            super(new PlaceboDocument(), 0);
        }

        @Override
        protected void integrateLocal(SequenceMessage op) throws IncorrectTraceException {
            this.getDoc().apply(op);
        }

        @Override
        protected List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException {
            int nbop = (opt.getType() == SequenceOperation.OpType.del) ? opt.getOffset() : opt.getContent().size();
            List<SequenceMessage> l = new java.util.ArrayList<SequenceMessage>(nbop);
            for (int i = 0; i < nbop; i++) {
                l.add(new PlaceboOperation(opt));
            }
            return l;
        }

        @Override
        public CRDT<String> create() {
            return new PlaceboMerge();
        }
    }

    @Override
    public MergeAlgorithm create(int r) {
        return new PlaceboMerge();
    }
}
