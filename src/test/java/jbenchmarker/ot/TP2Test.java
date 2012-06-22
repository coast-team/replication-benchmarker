/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot;

import java.util.Arrays;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.ot.otset.AddWinTransformation;
import jbenchmarker.ot.otset.DelWinTransformation;
import jbenchmarker.ot.otset.OTSetOperations;
import jbenchmarker.ot.ottree.OTTreeRemoteOperation;
import jbenchmarker.ot.ottree.OTTreeTranformation;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFTransformations;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.junit.Assert.fail;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TP2Test {

    SOCT2TranformationInterface[] ots = {
        new AddWinTransformation(),
        new DelWinTransformation(),
        new TTFTransformations(),
        new OTTreeTranformation()
    };
    Operation ops[][] = {
        {new OTSetOperations(OTSetOperations.OpType.Add, 1, 1),
            new OTSetOperations(OTSetOperations.OpType.Del, 1, 0)
        },
        {new OTSetOperations(OTSetOperations.OpType.Add, 1, 1),
            new OTSetOperations(OTSetOperations.OpType.Del, 1, 0)
        },
        {new TTFOperation(SequenceOperation.OpType.del, 1, 1),
            new TTFOperation(SequenceOperation.OpType.ins, 1, 2),},
        {new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0}), 'a', 0, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0}), 'b', 1, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 0}), 'b', 2, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 1}), 'e', 3, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 2}), 'c', 4, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 2}), 'd', 5, OTTreeRemoteOperation.OpType.ins),
        }
    };

    @Test
    public void transformations() {

        for (int i = 0; i < ots.length; i++) {
            SOCT2TranformationInterface<Operation> ot = ots[i];

            for (Operation op : ops[i]) {
                for (Operation op1 : ops[i]) {
                    for (Operation op2 : ops[i]) {
                        Operation res1;
                        res1 = ot.transpose(ot.transpose(op, op1), ot.transpose(op2, op1));
                        Operation res2;
                        res2 = ot.transpose(ot.transpose(op, op2), ot.transpose(op1, op2));
                        if (res1 != res2) {
                            fail("TP2 Fail : ot:" + ot + " " + res1 + "!=" + res2 + " op: " + op + " op1: " + op1 + " op2:" + op2);
                        }
                        //assertEquals(res1,res2);
                    }
                }
            }
        }
    }
}
