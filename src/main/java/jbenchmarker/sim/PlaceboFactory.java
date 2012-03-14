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
import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;


/**
 * Used to measure the base memory/time required to simulate a trace.
 * @author urso
 */
public class PlaceboFactory implements ReplicaFactory {
    public static class PlaceboOperation extends SequenceMessage {

        public PlaceboOperation(TraceOperation o) {
            super(o);
        }

        @Override
        public SequenceMessage clone() {
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
    }
    
    private static class PlaceboMerge extends MergeAlgorithm {

        PlaceboMerge() {
            super(new PlaceboDocument(), 0);
        }

        @Override
        protected void integrateLocal(SequenceMessage op) throws IncorrectTrace {
            this.getDoc().apply(op);
        }

        @Override
        protected List<SequenceMessage> generateLocal(TraceOperation opt) throws IncorrectTrace {
            int nbop = (opt.getType() == TraceOperation.OpType.del) ? opt.getOffset() : opt.getContent().length();
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
    public MergeAlgorithm createReplica(int r) {
        return new PlaceboMerge();
    }
}
