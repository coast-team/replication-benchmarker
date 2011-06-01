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
