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

import crdt.CRDT;
import crdt.CRDTMessage;
import java.util.Arrays;
import crdt.Operation;
import crdt.OperationBasedOneMessage;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation.OpType;
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
import jbenchmarker.ot.ttf.MC.TTFUndoTransformations;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFOperationWithId;
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
        {new TTFOperationWithId(OpType.delete, 1, null, 1),
            new TTFOperationWithId(OpType.insert, 1, 'x', 2),},
        {new TTFOperationWithId(OpType.update, 1, 'x', 1),
            new TTFOperationWithId(OpType.noop, 1, 'y', 2),
            new TTFOperationWithId(OpType.insert, 1, 'y', 3),
            new TTFOperationWithId(OpType.update, 2, 'z', 4),
            new TTFOperationWithId(OpType.noop, 2, 'x', 5),
            new TTFOperationWithId(OpType.insert, 2, 'x', 6),},
        {new TTFOperationWithId(OpType.update, 1, null, 1),
            new TTFOperationWithId(OpType.update, 1, 0, 2),
            new TTFOperationWithId(OpType.noop, 1, 'x', 3),
            new TTFOperationWithId(OpType.insert, 1, 'x', 4),
            new TTFOperationWithId(OpType.update, 2, null, 5),
            new TTFOperationWithId(OpType.update, 1, 0, 6),
            new TTFOperationWithId(OpType.noop, 2, 'y', 7),
            new TTFOperationWithId(OpType.insert, 2, 'y', 8),},
        {new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0}), 'a', 0, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0}), 'b', 1, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 0}), 'b', 2, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 1}), 'e', 3, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 2}), 'c', 4, OTTreeRemoteOperation.OpType.ins),
            new OTTreeRemoteOperation(Arrays.asList(new Integer[]{0, 2}), 'd', 5, OTTreeRemoteOperation.OpType.ins),},
        {new TTFOperation(OpType.insert, 3, 'x'),
            new TTFOperation(OpType.insert, 3, 'x'),
            new TTFOperation(OpType.insert, 3, 'y'),
            new TTFOperation(OpType.insert, 3, 'z'),
            new TTFOperation(OpType.insert, 3, 'y'),
            new TTFOperation(OpType.insert, 3, 'x'),
            new TTFOperation(OpType.noop, 3, 'x'),}
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
    
    
    
    //CRDT[] replicas = { set(), ttf(), ttfu(), ttfmc(), ttfundo(), };
    Class operations[] = { OTSetOperations.class, TTFOperationWithId.class, TTFOperationWithId.class, 
        TTFOperation.class, TTFOperation.class, };
    Object arg[][][] = { {{OTSetOperations.OpType.Add, OTSetOperations.OpType.Del}, {0, 1, 2}, {0, 1, 2}},
        {{OpType.delete, OpType.insert}, {0, 1, 2},  {'x', 'y', 'z'}, {0, 1, 2}},
        {{OpType.insert, OpType.update, OpType.noop}, {0, 1, 2},  {'x', 'y', 'z'}, {0, 1, 2}},
        {{OpType.insert, OpType.delete, OpType.noop}, {0, 1, 2},  {'x', 'y', 'z'}},
        {{OpType.insert, OpType.undo, OpType.noop}, {0, 1, 2},  {'x', 'y', 'z'}},
    };
    SOCT2TranformationInterface transformations[] = {new AddWinTransformation(), new DelWinTransformation(),
        new TTFTransformations(), 
        new TTFUTransformations(), new TTFUDelWinsTransformations(), 
        new TTFMCTransformations(), 
        new TTFUndoTransformations(), };
    int []type = {0, 0, 1, 2, 2, 3, 4};
    
    
    
    @Test
    public void newTP2() throws Exception {
        for (int i = 0; i < transformations.length; i++) {
            SOCT2TranformationInterface<Operation> ot = transformations[i];
            Constructor c = operations[type[i]].getConstructors()[0];
            List<Operation> ops = new LinkedList<Operation>();
            launch(c, arg[type[i]], ops, null, 0);
            for (Operation op : ops) {
                for (Operation op1 : ops) {
                    for (Operation op2 : ops) {
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

    private void launch(Constructor cons, Object[][] par, List<Operation> ops, Object params[], int i) throws Exception {
        if (i == par.length) {
            ops.add((Operation) cons.newInstance(params));
            return;
        }
        for (int j = 0; j < par[i].length; ++j) {
            if (params == null) {
                params = new Object[par.length];
            }
            params[i] = par[i][j];
            launch(cons, par, ops, params, i+1);
        }
    }

    void apply(CRDT s, Operation o) {
        s.applyRemote(new OperationBasedOneMessage(o));
    }
    
    private CRDT set() {
        OTSet s = new OTSet(new SOCT2(new AddWinTransformation()));
        apply(s, new OTSetOperations(OTSetOperations.OpType.Add, 1, 1));
        apply(s, new OTSetOperations(OTSetOperations.OpType.Add, 2, 0));
        apply(s, new OTSetOperations(OTSetOperations.OpType.Del, 2, 0));
        return s;
    }
}
