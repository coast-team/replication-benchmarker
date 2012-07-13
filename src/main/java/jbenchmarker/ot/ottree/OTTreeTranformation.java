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

import java.io.Serializable;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OTTreeTranformation implements SOCT2TranformationInterface<OTTreeRemoteOperation>, Serializable {
    /*
     * p1.size >= p2.size Check if the shortest path is in common path If two
     * operation are insertion
     */

    private void pathAdapt(OTTreeRemoteOperation op1, OTTreeRemoteOperation op2, int operator) {
        int i;
        /*
         * op2 must be an insertion
         */
        if (op2.getType() != OTTreeRemoteOperation.OpType.ins) {
            return;
        }
        /*
         * op2 must have short path or same size
         */
        if (op1.getPath().size() < op2.getPath().size()) {
            return;
        }
        /*
         * op1 and op2 must have same common path
         */
        for (i = 0; i < op2.getPath().size() - 1; i++) {
            if (!op1.getPath().get(i) .equals(op2.getPath().get(i))) {
                return;
            }
        }
        if ((Integer) op1.getPath().get(i) < (Integer) op2.getPath().get(i)) {
            return;
        }
        if (op1.getPath().get(i).equals(op2.getPath().get(i))
                && op1.getSiteId() < op2.getSiteId()
                && op1.getType() == OTTreeRemoteOperation.OpType.ins
                && op1.getPath().size() == op2.getPath().size()) {
            return;
        }
        op1.getPath().set(i, ((Integer) op1.getPath().get(i)) + operator);
    }

    @Override
    public OTTreeRemoteOperation transpose(OTTreeRemoteOperation op1, OTTreeRemoteOperation op2) {
        //System.out.print(""+op1.getType()+ op1.getPath()+op2.getType()+op2.getPath());
        pathAdapt(op1, op2, 1);
        //System.out.println("->" + op1.getPath());
        return op1;
        /*
         * if (op1.getType() == OTTreeRemoteOperation.OpType.ins &&
         * op2.getType() == OTTreeRemoteOperation.OpType.ins) { if
         * (op1.getPath().size()<op2.getPath().size()) return op1;
         *
         *
         *
         *
         * if (op1.getPosition() < op2.getPosition()) { return op1; } else if
         * (op1.getPosition() == op2.getPosition() && op1.getSiteId() <
         * op2.getSiteId()) { return op1; } else {
         * op1.setPosition(op1.getPosition() + 1); return op1; } } else if
         * (op1.getType() == OTTreeRemoteOperation.OpType.del && op2.getType()
         * == OTTreeRemoteOperation.OpType.ins) { if (op1.getPosition() <
         * op2.getPosition()) { return op1; } else {
         * op1.setPosition(op1.getPosition() + 1); return op1; } }else if
         * (op1.getType() == OTTreeRemoteOperation.OpType.chT && op2.getType()
         * == OTTreeRemoteOperation.OpType.ins) { if (op1.getPosition() <
         * op2.getPosition()) { return op1; } else {
         * op1.setPosition(op1.getPosition() + 1); return op1; } } return op1;
         */

    }

    @Override
    public OTTreeRemoteOperation transposeBackward(OTTreeRemoteOperation op1, OTTreeRemoteOperation op2) {
        //System.out.print("\n" + op1.getPath());
        pathAdapt(op1, op2, -1);
        //System.out.println("->" + op1.getPath());

        return op1;

        /*
         * if (op1.getType() == OTTreeRemoteOperation.OpType.ins &&
         * op2.getType() == OTTreeRemoteOperation.OpType.ins) { if
         * (op1.getPosition() < op2.getPosition()) { return op1; } else if
         * (op1.getPosition() == op2.getPosition() && op1.getSiteId() <
         * op2.getSiteId()) { return op1; } else {
         * op1.setPosition(op1.getPosition() - 1); return op1; } } else if
         * (op1.getType() == OTTreeRemoteOperation.OpType.del && op2.getType()
         * == OTTreeRemoteOperation.OpType.ins) { if (op1.getPosition() <
         * op2.getPosition()) { return op1; } else {
         * op1.setPosition(op1.getPosition() - 1); return op1; } } else if
         * (op1.getType() == OTTreeRemoteOperation.OpType.chT && op2.getType()
         * == OTTreeRemoteOperation.OpType.ins) { if (op1.getPosition() <
         * op2.getPosition()) { return op1; } else {
         * op1.setPosition(op1.getPosition() - 1); return op1; } }
         *
         * return op1;
         */
    }
}
