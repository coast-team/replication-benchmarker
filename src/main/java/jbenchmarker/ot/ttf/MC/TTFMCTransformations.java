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
package jbenchmarker.ot.ttf.MC;

import java.io.Serializable;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;
import jbenchmarker.ot.ttf.TTFOperation;

/**
 * Is transformation and backward transformation for TTF model
 *
 * @author Mehdi
 */
public class TTFMCTransformations implements SOCT2TranformationInterface<TTFOperation>, Serializable {

    @Override
    public TTFOperation transpose(TTFOperation op1, TTFOperation op2) {
        if (op1.getType() == OpType.insert && op2.getType() == OpType.insert) {
            if (op1.getPosition() == op2.getPosition() && op1.getContent().equals(op2.getContent())) {
                op1.setType(OpType.noop);
                return op1;
            } else if (op1.getPosition() > op2.getPosition()
                    || (op1.getPosition() == op2.getPosition() && op1.getContent().hashCode()> op2.getContent().hashCode())) {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        } else if ((op1.getType() == OpType.noop || op1.getType() == OpType.delete)
                && op2.getType() == OpType.insert) {
            if (op1.getPosition() >= op2.getPosition()) {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        }
        return op1;
    }

    @Override
    public TTFOperation transposeBackward(TTFOperation op1, TTFOperation op2) {
        if (op1.getType() == OpType.insert && op2.getType() == OpType.insert) {
            if (op1.getPosition() > op2.getPosition()
                    || (op1.getPosition() == op2.getPosition() && op1.getContent().hashCode()> op2.getContent().hashCode())) {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OpType.noop && op2.getType() == OpType.insert) {
            if (op1.getPosition() == op2.getPosition() && op1.getContent().equals(op2.getContent())) {
                op1.setType(OpType.insert);
                return op1;
            } else if (op1.getPosition() >= op2.getPosition()) {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OpType.delete && op2.getType() == OpType.insert) {
            if (op1.getPosition() >= op2.getPosition()) {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        }
        return op1;
    }
}
