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
