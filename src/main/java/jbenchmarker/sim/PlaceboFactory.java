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

import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;


/**
 * Used to measure the base memory/time required to simulate a trace.
 * @author urso
 */
public class PlaceboFactory implements ReplicaFactory {

    public static class PlaceboOperation extends Operation {

        public PlaceboOperation(TraceOperation o) {
            super(o);
        }

        @Override
        public Operation clone() {
            return new PlaceboOperation(this.getOriginalOp());
        }        
    }
    
    public MergeAlgorithm createReplica(int r) {
        return new MergeAlgorithm(new Document() {
            public String view() {
                return "";
            }
            public void apply(Operation op) {
            }
        }, r) {
            protected void integrateLocal(Operation op) throws IncorrectTrace {
                this.getDoc().apply(op);
            }
            protected List<Operation> generateLocal(TraceOperation opt) throws IncorrectTrace {
                int nbop = (opt.getType() == TraceOperation.OpType.del) ? opt.getOffset() : opt.getContent().length();
                List<Operation> l = new java.util.ArrayList<Operation>(nbop);
                for (int i = 0; i < nbop; i++)
                    l.add(new PlaceboOperation(opt));
                return l;
            }
        };
    }

}
