package jbenchmarker.ot;

import jbenchmarker.trace.TraceOperation.OpType;

/**
 *
 * @author oster
 */
public class TTFTransformations {

    public static TTFOperation transpose(TTFOperation op1, TTFOperation op2) {

        if (op1.getType() == OpType.ins && op2.getType() == OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else if (op1.getPosition() == op2.getPosition()
                    && op1.getSiteId() < op2.getSiteId()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        } else if (op1.getType() == OpType.del && op2.getType() == OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        }

        return op1;

    }

    public static TTFOperation transposeBackward(TTFOperation op1, TTFOperation op2) {
        if (op1.getType() == OpType.ins && op2.getType() == OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else if (op1.getPosition() == op2.getPosition()
                    && op1.getSiteId() < op2.getSiteId()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OpType.del && op2.getType() == OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        }

        return op1;
    }
}
