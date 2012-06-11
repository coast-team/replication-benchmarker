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
package jbenchmarker.core;

import collect.VectorClock;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 * 
 * @author oster
 */
public class UnsupportedOperation<T> extends SequenceMessage {

    //private VectorClock clock;
    //private final int siteId;

    public UnsupportedOperation(SequenceOperation o) {
        super(o);
        //this.siteId = this.getOriginalOp().getReplica();
    }

    // FIXME: should be moved to SequenceMessage class?
    public OpType getType() {
        return this.getOriginalOp().getType();
    }

   /* public int getSiteId() {
        return this.siteId;
    }

    public VectorClock getClock() {
        return this.clock;
    }*/

    @Override
    public SequenceMessage copy() {
        UnsupportedOperation op = new UnsupportedOperation(getOriginalOp());
        //op.clock = new VectorClock(this.clock);

        return op;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getType());
        sb.append('(');
        sb.append(')');
        return sb.toString();
    }

    public static UnsupportedOperation create(SequenceOperation o/*, VectorClock vc*/) {
        UnsupportedOperation op = new UnsupportedOperation(o);
        //op.clock = vc;
        return op;
    }

}
