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
package jbenchmarker.ot;

import crdt.CRDTMessage;
import java.util.Arrays;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.ot.otset.AddWinTransformation;
import jbenchmarker.ot.otset.DelWinTransformation;
import jbenchmarker.ot.otset.OTSet;
import jbenchmarker.ot.otset.OTSetOperations;
import jbenchmarker.ot.ottree.OTTree;
import jbenchmarker.ot.ottree.OTTreeRemoteOperation;
import jbenchmarker.ot.ottree.OTTreeTransformation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;
import jbenchmarker.ot.soct2.OTReplica;
import jbenchmarker.ot.ttf.MC.TTFMCMergeAlgorithm;
import jbenchmarker.ot.ttf.MC.TTFMCTransformations;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFTransformations;
import jbenchmarker.ot.ttf.update.TTFUDelWinsTransformations;
import jbenchmarker.ot.ttf.update.TTFUMergeAlgorithm;
import jbenchmarker.ot.ttf.update.TTFUTransformations;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TP2Test {

    OTReplica[] ots = {
        new OTSet(new SOCT2(new AddWinTransformation())),
        new OTSet(new SOCT2(new DelWinTransformation())),
        new TTFMergeAlgorithm(new SOCT2(new TTFTransformations())),
        new TTFUMergeAlgorithm(new SOCT2(new TTFUTransformations())),
        new TTFUMergeAlgorithm(new SOCT2(new TTFUDelWinsTransformations())),
        new OTTree(new SOCT2(new OTTreeTransformation())),
        new TTFMCMergeAlgorithm(new SOCT2(new TTFMCTransformations())),};
    Operation ops[][] = {
        {new OTSetOperations(OTSetOperations.OpType.Add, 1, 1),
            new OTSetOperations(OTSetOperations.OpType.Del, 1, 0)
        },
        {new OTSetOperations(OTSetOperations.OpType.Add, 1, 1),
            new OTSetOperations(OTSetOperations.OpType.Del, 1, 0)
        },
        {new TTFOperation(SequenceOperation.OpType.delete, 1, 1),
            new TTFOperation(SequenceOperation.OpType.insert, 1, 2),},
        {new TTFOperation(SequenceOperation.OpType.update, 1, 1),
            new TTFOperation(SequenceOperation.OpType.noop, 1, 2),
            new TTFOperation(SequenceOperation.OpType.insert, 1, 3),
            new TTFOperation(SequenceOperation.OpType.update, 2, 4),
            new TTFOperation(SequenceOperation.OpType.noop, 2, 5),
            new TTFOperation(SequenceOperation.OpType.insert, 2, 6),},
        {new TTFOperation(SequenceOperation.OpType.update, 1, null, 1),
            new TTFOperation(SequenceOperation.OpType.update, 1, 0, 2),
            new TTFOperation(SequenceOperation.OpType.noop, 1, 3),
            new TTFOperation(SequenceOperation.OpType.insert, 1, 4),
            new TTFOperation(SequenceOperation.OpType.update, 2, null, 5),
            new TTFOperation(SequenceOperation.OpType.update, 1, 0, 6),
            new TTFOperation(SequenceOperation.OpType.noop, 2, 7),
            new TTFOperation(SequenceOperation.OpType.insert, 2, 8),},
        {new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0}), 'a', 0, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0}), 'b', 1, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 0}), 'b', 2, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 1}), 'e', 3, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 2}), 'c', 4, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 2}), 'd', 5, OTTreeRemoteOperation.OpType.ins),},
        {new TTFOperation(SequenceOperation.OpType.insert, 3, 'x', 1),
            new TTFOperation(SequenceOperation.OpType.insert, 3, 'x', 2),
            new TTFOperation(SequenceOperation.OpType.insert, 3, 'y', 3),
            new TTFOperation(SequenceOperation.OpType.insert, 3, 'z', 4),
            new TTFOperation(SequenceOperation.OpType.insert, 3, 'y', 5),
            new TTFOperation(SequenceOperation.OpType.insert, 3, 'x', 6),
            new TTFOperation(SequenceOperation.OpType.noop, 2, 7),}
    };

    @Test
    public void tp2() {
        for (int i = 0; i < ots.length; i++) {
            SOCT2TranformationInterface<Operation> ot = ots[i].getTransformation();
            for (Operation op : ops[i]) {
                for (Operation op1 : ops[i]) {
                    for (Operation op2 : ops[i]) {
                        Operation res1;
                        res1 = ot.transpose(ot.transpose(op.clone(), op1), ot.transpose(op2.clone(), op1));
                        Operation res2;
                        res2 = ot.transpose(ot.transpose(op.clone(), op2), ot.transpose(op1.clone(), op2));
                        assertEquals("TP2 Fail : ot:" + ot + "  op: " + op + " op1: " + op1 + " op2:" + op2, res1, res2);
                    }
                }
            }
        }
    }
}
