/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.sim;

import crdt.CRDT;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.List;
import jbenchmarker.core.*;

/**
 * Used to measure the base memory/time required to simulate a trace.
 *
 * @author urso
 */
public class PlaceboFactory extends ReplicaFactory {

    public static class PlaceboOperation implements Operation {

        public PlaceboOperation() {
        }

        @Override
        public Operation clone() {
            return new PlaceboOperation();
        }
    }

    public static class PlaceboDocument implements Document {

        @Override
        public String view() {
            return "";
        }

        @Override
        public void apply(Operation op) {
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
        protected void integrateRemote(crdt.Operation message) throws IncorrectTraceException {
            this.getDoc().apply(message);
        }

        protected List<Operation> generateLocal(SequenceOperation opt) throws IncorrectTraceException {
            int nbop = (opt.getType() == SequenceOperation.OpType.delete) ? opt.getLenghOfADel() : opt.getContent().size();
            List<Operation> l = new java.util.ArrayList<Operation>(nbop);
            for (int i = 0; i < nbop; i++) {
                l.add(new PlaceboOperation());
            }
            return l;
        }

        @Override
        public CRDT<String> create() {
            return new PlaceboMerge();
        }

        @Override
        protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
            return generateLocal(opt);
        }

        @Override
        protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
            return generateLocal(opt);
        }
    }

    @Override
    public MergeAlgorithm create(int r) {
        return new PlaceboMerge();
    }
}
