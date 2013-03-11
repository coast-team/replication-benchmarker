/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package crdt.tree.fctree.policy;

import crdt.CRDTMessage;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.tree.fctree.FCTree;
import crdt.tree.orderedtree.OrderedTreeOperation;
import crdt.tree.orderedtree.OrderedTreeOperation;
import crdt.tree.orderedtree.OrderedTreeOperation.OpType;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FastCycleBreakingTest {

    FCTree<String> tree;
    FCTree<String> tree2;

    @Before
    public void setUp() throws PreconditionException {
        tree = new FCTree(new FastCycleBreaking("Garbage"));

        tree2 = new FCTree(new FastCycleBreaking("Garbage"));

        CRDTMessage mess1 = tree.add(new ArrayList(), 0, "a");
        CRDTMessage mess2 = tree.add(new ArrayList(), 1, "b");
        CRDTMessage mess3 = tree.add(new ArrayList(), 2, "c");
        CRDTMessage mess4 = tree.add(Arrays.asList(0), 0, "d");
        CRDTMessage mess5 = tree.add(Arrays.asList(0), 1, "e");
        CRDTMessage mess6 = tree.add(Arrays.asList(0, 1), 1, "f");
        CRDTMessage mess7 = tree.add(Arrays.asList(2), 0, "g");
        CRDTMessage mess8 = tree.add(Arrays.asList(2), 1, "h");
        tree2.applyOneRemote(mess1);
        tree2.applyOneRemote(mess4);
        tree2.applyOneRemote(mess5);
        tree2.applyOneRemote(mess6);
        tree2.applyOneRemote(mess3);
        tree2.applyOneRemote(mess7);
        tree2.applyOneRemote(mess8);
        tree2.applyOneRemote(mess2);
    }

    public FastCycleBreakingTest() {
    }

    @Test
    public void afterAddAndDelConcurency() throws PreconditionException {
        CRDTMessage mess1 = tree.add(Arrays.asList(0), 1, "TEST");
        CRDTMessage mess2 = tree2.remove(Arrays.asList(0));
        tree.applyRemote(mess2);
        tree2.applyRemote(mess1);
        String result = "null{b,c{g,h,},Garbage{d,TEST,e{f,},},}";
        assertEquals(result, tree.getRoot().nodetail());
        assertEquals(result, tree2.getRoot().nodetail());

    }

    @Test
    public void afterDelRescue() throws PreconditionException {
        CRDTMessage mess2 = tree2.remove(Arrays.asList(0));
        tree.applyRemote(mess2);
        String result = "null{b,c{g,h,},Garbage{d,e{f,},},}";
        assertEquals(result, tree.getRoot().nodetail());
        assertEquals(result, tree2.getRoot().nodetail());
    }

    @Test
    public void afterMoveCycleBreak() {
        CRDTMessage mess1 = tree.move(Arrays.asList(0, 1), Arrays.asList(2, 1), 0);
        CRDTMessage mess2 = tree2.move(Arrays.asList(2, 1), Arrays.asList(0, 1, 0), 0);
        assertEquals("null{a{d,},b,c{g,h{e{f,},},},Garbage,}", tree.getRoot().nodetail());
        assertEquals("null{a{d,e{f{h,},},},b,c{g,},Garbage,}", tree2.getRoot().nodetail());
        tree.applyRemote(mess2);
        tree2.applyRemote(mess1);
        String result = "null{a{d,},b,c{g,},Garbage{h{e{f,},},},}";
        assertEquals(result, tree.getRoot().nodetail());
        assertEquals(result, tree2.getRoot().nodetail());
        CRDTMessage mess3 = tree.move(Arrays.asList(3, 0, 0, 0), Arrays.asList(0), 1);
        tree2.applyRemote(mess3);
        result = "null{a{d,f{h{e,},},},b,c{g,},Garbage,}";
        assertEquals(result, tree.getRoot().nodetail());
        assertEquals(result, tree2.getRoot().nodetail());
    }

    @Test
    public void afterAddAndDelConcurencyop() throws PreconditionException {
        OrderedTreeOperation<String> op1 = new OrderedTreeOperation<String>(Arrays.asList(0), 1, "TEST");
        OrderedTreeOperation<String> op2 = new OrderedTreeOperation<String>(Arrays.asList(0));
        CRDTMessage mess1 = tree.applyLocal(op1);
        CRDTMessage mess2 = tree2.applyLocal(op2);
        tree.applyRemote(mess2);
        tree2.applyRemote(mess1);
        String result = "null{b,c{g,h,},Garbage{d,TEST,e{f,},},}";
        assertEquals(result, tree.getRoot().nodetail());
        assertEquals(result, tree2.getRoot().nodetail());

    }

    @Test
    public void afterDelRescueop() throws PreconditionException {
        CRDTMessage mess2 = tree2.applyLocal(new OrderedTreeOperation(Arrays.asList(0)));
        tree.applyRemote(mess2);
        String result = "null{b,c{g,h,},Garbage{d,e{f,},},}";
        assertEquals(result, tree.getRoot().nodetail());
        assertEquals(result, tree2.getRoot().nodetail());
    }
    final static OpType MOVE = OrderedTreeOperation.OpType.move;

    @Test
    public void afterMoveCycleBreakop() throws PreconditionException {
        OrderedTreeOperation<String> op1 = new OrderedTreeOperation<String>(MOVE, Arrays.asList(0, 1), Arrays.asList(2, 1), 0, null);
        OrderedTreeOperation<String> op2 = new OrderedTreeOperation<String>(MOVE, Arrays.asList(2, 1), Arrays.asList(0, 1, 0), 0, null);
        CRDTMessage mess1 = tree.applyLocal(op1);
        CRDTMessage mess2 = tree2.applyLocal(op2);
        assertEquals("null{a{d,},b,c{g,h{e{f,},},},Garbage,}", tree.getRoot().nodetail());
        assertEquals("null{a{d,e{f{h,},},},b,c{g,},Garbage,}", tree2.getRoot().nodetail());
        tree.applyRemote(mess2);
        tree2.applyRemote(mess1);
        String result = "null{a{d,},b,c{g,},Garbage{h{e{f,},},},}";
        assertEquals(result, tree.getRoot().nodetail());
        assertEquals(result, tree2.getRoot().nodetail());
        CRDTMessage mess3 = tree.move(Arrays.asList(3, 0, 0, 0), Arrays.asList(0), 1);
        tree2.applyRemote(mess3);
        result = "null{a{d,f{h{e,},},},b,c{g,},Garbage,}";
        assertEquals(result, tree.getRoot().nodetail());
        assertEquals(result, tree2.getRoot().nodetail());
    }
}
