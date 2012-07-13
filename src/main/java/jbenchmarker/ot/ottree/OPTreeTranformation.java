/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package jbenchmarker.ot.ottree;

import jbenchmarker.ot.soct2.SOCT2TranformationInterface;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OPTreeTranformation implements SOCT2TranformationInterface <OPTTreeNodeOperation>{

 @Override
    public  OPTTreeNodeOperation transpose(OPTTreeNodeOperation op1, OPTTreeNodeOperation op2) {

        if (op1.getType() == OPTTreeNodeOperation.OpType.ins && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else if (op1.getPosition() == op2.getPosition()
                    && op1.getSiteId() < op2.getSiteId()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.del && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.transpose && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        }else if (op1.getType() == OPTTreeNodeOperation.OpType.chT && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        }
        return op1;

    }

    @Override
    public  OPTTreeNodeOperation transposeBackward(OPTTreeNodeOperation op1, OPTTreeNodeOperation op2) {
        if (op1.getType() == OPTTreeNodeOperation.OpType.ins && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else if (op1.getPosition() == op2.getPosition()
                    && op1.getSiteId() < op2.getSiteId()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.del && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.transpose && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.chT && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
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
