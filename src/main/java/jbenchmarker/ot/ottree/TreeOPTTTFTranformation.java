/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2012
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
package jbenchmarker.ot.ottree;

import java.io.Serializable;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;
import jbenchmarker.ot.soct2.SOCT2TranformationInterfaceOpt;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TreeOPTTTFTranformation implements SOCT2TranformationInterfaceOpt <TreeOPTTTFNodeOperation>, Serializable {

    private void tTF(TreeOPTTTFNodeOperation op1, TreeOPTTTFNodeOperation op2, int operator) {

        /*
         * op2 must be an insertion
         */
        if (op2.getType() != TreeOPTTTFNodeOperation.OpType.ins) {
            return;
        }

        /*
         * position of Op1 must after position of op2
         */ 
        if (op2.getPosition() > op1.getPosition()) {
            return;
        }

        if (op1.getPosition() == op2.getPosition()
                && op1.getSiteId() < op2.getSiteId()
                && op1.getType() == TreeOPTTTFNodeOperation.OpType.ins) {
            return;
        }
        op1.setPosition(op1.getPosition() + operator);
    }

    @Override
    public TreeOPTTTFNodeOperation transpose(TreeOPTTTFNodeOperation op1, TreeOPTTTFNodeOperation op2) {
        tTF(op1, op2, 1);
        return op1;

    }

    @Override
    public TreeOPTTTFNodeOperation transposeBackward(TreeOPTTTFNodeOperation op1, TreeOPTTTFNodeOperation op2) {
        tTF(op1, op2, -1);
        return op1;
    }

    @Override
    public boolean isLogInterest(TreeOPTTTFNodeOperation o) {
        return  o.type.equals(TreeOPTTTFNodeOperation.OpType.ins);
    }
}
