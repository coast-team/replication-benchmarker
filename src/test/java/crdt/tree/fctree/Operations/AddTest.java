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
package crdt.tree.fctree.Operations;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.tree.fctree.FCIdentifier;
import crdt.tree.fctree.FCNode;
import crdt.tree.fctree.FCPosition;
import crdt.tree.fctree.FCTree;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class AddTest {

    FCTree<String> tree;

    public AddTest() {
    }

    @Before
    public void setUp() {
        tree = new FCTree();
    }

    @Test
    public void add() {
        FCIdentifier id1 = tree.getIdFactory().createId();
        FCIdentifier id2 = tree.getIdFactory().createId();
        FCIdentifier id3 = tree.getIdFactory().createId();
        FCIdentifier id4 = tree.getIdFactory().createId();
        FCIdentifier id5 = tree.getIdFactory().createId();
        FCIdentifier id6 = tree.getIdFactory().createId();


        FCPosition pos1 = tree.getPositionFactory().createBetweenPosition(null, null, id1);
        FCPosition pos2 = tree.getPositionFactory().createBetweenPosition(pos1, null, id2);
        FCPosition pos3 = tree.getPositionFactory().createBetweenPosition(pos1, pos2, id3);

        FCPosition pos4 = tree.getPositionFactory().createBetweenPosition(null, null, id4);
        FCPosition pos5 = tree.getPositionFactory().createBetweenPosition(null, pos4, id5);

        FCPosition pos6 = tree.getPositionFactory().createBetweenPosition(null, null, id6);


        CRDTMessage mess1 = new OperationBasedOneMessage(new Add("a", pos1, tree.getRoot().getId(), id1));
        CRDTMessage mess2 = new OperationBasedOneMessage(new Add("c", pos2, tree.getRoot().getId(), id2));
        CRDTMessage mess3 = new OperationBasedOneMessage(new Add("b", pos3, tree.getRoot().getId(), id3));

        CRDTMessage mess4 = new OperationBasedOneMessage(new Add("e", pos4, id1, id4));
        CRDTMessage mess5 = new OperationBasedOneMessage(new Add("d", pos5, id1, id5));
        
        CRDTMessage mess6 = new OperationBasedOneMessage(new Add("f", pos6, id4, id6));
        
        tree.applyOneRemote(mess1);
        tree.applyOneRemote(mess3);
        
        tree.applyOneRemote(mess4);
        
        tree.applyOneRemote(mess2);
        
        
        tree.applyOneRemote(mess6);
        tree.applyOneRemote(mess5);

        FCNode node = tree.getRoot();
        assertEquals(3, node.getChildrenNumber());
        assertEquals("a",node.getChild(0).getValue());
        assertEquals("b",node.getChild(1).getValue());
        assertEquals("c",node.getChild(2).getValue());
        assertEquals(0, node.getChild(1).getChildrenNumber());
        assertEquals(0, node.getChild(2).getChildrenNumber());
        
        node=node.getChild(0);
        assertEquals(2, node.getChildrenNumber());
        assertEquals("d",node.getChild(0).getValue());
        assertEquals("e",node.getChild(1).getValue());
        assertEquals(0, node.getChild(0).getChildrenNumber());
        
        node=node.getChild(1);
        assertEquals(1, node.getChildrenNumber());
        assertEquals("f",node.getChild(0).getValue());
        assertEquals(0, node.getChild(0).getChildrenNumber());
        
        
        

    }
}
