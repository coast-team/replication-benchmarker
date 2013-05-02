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
package crdt.tree.fctree;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.simulator.sizecalculator.StandardSizeCalculator;
import crdt.tree.orderedtree.OrderedTreeOperation;
import crdt.tree.orderedtree.OrderedTreeOperation.OpType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FCTreeGfTest {

    FCTree<String> tree;
    FCTree<String> tree2;
    FCTree<String> tree3;

    
    @Before
    public void setUp() throws PreconditionException {
        tree = new FCTreeGf();
        tree2 = new FCTreeGf();
        tree3 = new FCTreeGf();
        tree.setReplicaNumber(1);
        tree2.setReplicaNumber(2);
        tree3.setReplicaNumber(3);
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

    @Test
    public void addTest() {

        assertEquals("null{a{d,e{f,},},b,c{g,h,},}", tree.getRoot().nodetail());
        assertEquals("null{a{d,e{f,},},b,c{g,h,},}", tree2.getRoot().nodetail());

    }

    @Test
    public void removeTest() throws PreconditionException {
        CRDTMessage m = tree2.remove(Arrays.asList(0, 1));
        tree.applyOneRemote(m);

        assertEquals("null{a{d,},b,c{g,h,},}", tree.getRoot().nodetail());
        assertEquals("null{a{d,},b,c{g,h,},}", tree2.getRoot().nodetail());
    }

    @Test
    public void removeAllTest() throws PreconditionException, IOException {
        StandardSizeCalculator sdc=new StandardSizeCalculator(true);
        int s1=sdc.serializ(tree);
        int s2=sdc.serializ(tree2);
        int s3=sdc.serializ(tree3);
        assertTrue(s3<s1);
        assertTrue(s3<s2);
        CRDTMessage mess1 = tree2.remove(Arrays.asList(0, 1, 0));//f
        CRDTMessage mess2 = tree2.remove(Arrays.asList(2, 1));//h
        CRDTMessage mess3 = tree2.remove(Arrays.asList(2, 0));//g
        CRDTMessage mess4 = tree2.remove(Arrays.asList(0, 1));//e
        CRDTMessage mess5 = tree2.remove(Arrays.asList(0, 0));//d
        CRDTMessage mess6 = tree2.remove(Arrays.asList(2));//c
        CRDTMessage mess7 = tree2.remove(Arrays.asList(1));//b
        CRDTMessage mess8 = tree2.remove(Arrays.asList(0));//a
        tree.applyOneRemote(mess1);
        tree.applyOneRemote(mess4);
        tree.applyOneRemote(mess5);
        tree.applyOneRemote(mess6);
        tree.applyOneRemote(mess3);
        tree.applyOneRemote(mess7);
        tree.applyOneRemote(mess8);
        tree.applyOneRemote(mess2);
        assertEquals("null", tree.getRoot().nodetail());
        assertEquals("null", tree2.getRoot().nodetail());
        int sa1=sdc.serializ(tree);
        int sa2=sdc.serializ(tree2);
        assertEquals(s3, sa1);
        assertEquals(s3, sa2);
    }

    
    @Test 
    public void removeAll2Test()throws Exception{
        tree.setRemoveEntireSubtree(true);
        tree2.setRemoveEntireSubtree(true);
        StandardSizeCalculator sdc=new StandardSizeCalculator(true);
        int s1=sdc.serializ(tree);
        int s2=sdc.serializ(tree2);
        int s3=sdc.serializ(tree3);
        assertTrue(s3<s1);
        assertTrue(s3<s2);
        CRDTMessage mess1 = tree2.remove(Arrays.asList(0));//f
        CRDTMessage mess2 = tree2.remove(Arrays.asList(0));//f
        CRDTMessage mess3 = tree2.remove(Arrays.asList(0));//f
        tree.applyRemote(mess1);
        tree.applyRemote(mess3);
        tree.applyRemote(mess2);
        assertEquals("null", tree2.getRoot().nodetail());
        assertEquals("null", tree.getRoot().nodetail());
        int sa1=sdc.serializ(tree);
        int sa2=sdc.serializ(tree2);
        assertEquals(1,tree.map.size());
        assertEquals(1,tree2.map.size());
        assertEquals(s3, sa1);
        assertEquals(s3, sa2);
    }
    @Test
    public void ChLabelTest() {
        CRDTMessage m = tree2.rename(Arrays.asList(0), "ZoidBerg");
        tree.applyOneRemote(m);

        assertEquals("null{ZoidBerg{d,e{f,},},b,c{g,h,},}", tree.getRoot().nodetail());
        assertEquals("null{ZoidBerg{d,e{f,},},b,c{g,h,},}", tree2.getRoot().nodetail());


    }

    @Test
    public void switchTest() {
        CRDTMessage m = tree2.move(Arrays.asList(0), new LinkedList(), 1);
        tree.applyOneRemote(m);

        assertEquals("null{b,a{d,e{f,},},c{g,h,},}", tree.getRoot().nodetail());
        assertEquals("null{b,a{d,e{f,},},c{g,h,},}", tree2.getRoot().nodetail());
    }

    @Test
    public void moveTest() {
        CRDTMessage m = tree2.move(Arrays.asList(0, 1), Arrays.asList(2), 1);
        tree.applyRemote(m);

        assertEquals("null{a{d,},b,c{g,e{f,},h,},}", tree.getRoot().nodetail());
        assertEquals("null{a{d,},b,c{g,e{f,},h,},}", tree2.getRoot().nodetail());

    }

    @Test
    public void removeTestop() throws PreconditionException {

        CRDTMessage m = tree2.applyLocal(new OrderedTreeOperation<String>(Arrays.asList(0, 1)));
        tree.applyOneRemote(m);

        assertEquals("null{a{d,},b,c{g,h,},}", tree.getRoot().nodetail());
        assertEquals("null{a{d,},b,c{g,h,},}", tree2.getRoot().nodetail());
    }

    @Test
    public void ChLabelTestop() throws PreconditionException {
        OpType rename = OrderedTreeOperation.OpType.chContent;
        OrderedTreeOperation<String> op1 = new OrderedTreeOperation<String>(rename, Arrays.asList(0), null, 0, "ZoidBerg");
        CRDTMessage m = tree2.applyLocal(op1);
        tree.applyOneRemote(m);

        assertEquals("null{ZoidBerg{d,e{f,},},b,c{g,h,},}", tree.getRoot().nodetail());
        assertEquals("null{ZoidBerg{d,e{f,},},b,c{g,h,},}", tree2.getRoot().nodetail());


    }
    final static OpType MOVE = OrderedTreeOperation.OpType.move;

    @Test
    public void switchTestop() throws PreconditionException {
        OrderedTreeOperation<String> op1 = new OrderedTreeOperation<String>(MOVE, Arrays.asList(0), new LinkedList(), 1, null);
        CRDTMessage m = tree2.applyLocal(op1);
        tree.applyOneRemote(m);

        assertEquals("null{b,a{d,e{f,},},c{g,h,},}", tree.getRoot().nodetail());
        assertEquals("null{b,a{d,e{f,},},c{g,h,},}", tree2.getRoot().nodetail());
    }

    @Test
    public void moveTestop() throws PreconditionException {
        OrderedTreeOperation<String> op1 = new OrderedTreeOperation<String>(MOVE, Arrays.asList(0, 1), Arrays.asList(2), 1, null);
        CRDTMessage m = tree2.applyLocal(op1);
        tree.applyRemote(m);
        assertEquals("null{a{d,},b,c{g,e{f,},h,},}", tree.getRoot().nodetail());
        assertEquals("null{a{d,},b,c{g,e{f,},h,},}", tree2.getRoot().nodetail());

    }

    public FCTreeGfTest() {
    }
}
