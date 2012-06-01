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
package jbenchmarker.ot;

import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author oster
 */
public class TTFTransformations implements SOCT2TranformationInterface {

    public  TTFOperation transpose(TTFOperation op1, TTFOperation op2) {

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

    public  TTFOperation transposeBackward(TTFOperation op1, TTFOperation op2) {
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

    @Override
    public SOCT2OperationInterface transpose(SOCT2OperationInterface op1, SOCT2OperationInterface op2) {
        return this.transpose((TTFOperation)op1, (TTFOperation)op2);
    }

    @Override
    public SOCT2OperationInterface transposeBackward(SOCT2OperationInterface op1, SOCT2OperationInterface op2) {
        return this.transposeBackward((TTFOperation)op1, (TTFOperation)op2);
    }
}
