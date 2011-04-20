/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package jbenchmarker.ot;

import jbenchmarker.core.Operation;
import jbenchmarker.core.VectorClock;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.trace.TraceOperation.OpType;

/**
 *
 * @author oster
 */
public class TTFOperation extends Operation {

    private int pos;
    private char content;
    private VectorClock clock;
    private final int siteId;

    public TTFOperation(TraceOperation o) {
        super(o);
        this.siteId = this.getOriginalOp().getReplica();
    }

    // FIXME: should be moved to Operation class?
    public OpType getType() {
        return this.getOriginalOp().getType();
    }

    public int getPosition() {
        return this.pos;
    }

    public void setPosition(int pos) {
        this.pos = pos;
    }

    public char getChar() {
        return this.content;
    }

    public int getSiteId() {
        return this.siteId;
    }

    public VectorClock getClock() {
        return this.clock;
    }

    @Override
    public Operation clone() {
        TTFOperation op = new TTFOperation(getOriginalOp());
        op.pos = this.pos;
        op.content = this.content;
        op.clock = new VectorClock(this.clock);

        return op;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getType());
        sb.append('(');
        sb.append(this.pos);
        if (OpType.ins == this.getType()) {
            sb.append(',');
            sb.append(this.content);
        }
        sb.append(')');
        return sb.toString();
    }

    public static TTFOperation delete(TraceOperation o, int pos, VectorClock vc) {
        TTFOperation op = new TTFOperation(o);
        op.pos = pos;
        op.clock = vc;
        return op;
    }

    public static TTFOperation insert(TraceOperation o, int pos, char content, VectorClock vc) {
        TTFOperation op = new TTFOperation(o);
        op.pos = pos;
        op.content = content;
        op.clock = vc;
        return op;
    }

    public static TTFOperation from(TraceOperation opt) {
        TTFOperation op = new TTFOperation(opt);
        op.clock = opt.getVC();
        op.pos = opt.getPosition();
        if (opt.getType() == OpType.ins) {
            op.content = opt.getContent().charAt(0);
        }
        return op;
    }
}
