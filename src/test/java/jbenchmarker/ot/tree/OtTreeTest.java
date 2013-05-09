/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package jbenchmarker.ot.tree;

import collect.OrderedNode;
import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.tree.orderedtree.CRDTOrderedTree;
import crdt.tree.orderedtree.PositionIdentifierTree;
import jbenchmarker.ot.ottree.OTTree;
import jbenchmarker.ot.ottree.OTTreeTransformation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2Log;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.Arrays;
import junit.framework.Assert;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OtTreeTest {

    private void assertSameTree(OrderedNode on, OrderedNode ot) {
        assertTrue(ot + " expected : " + on, CRDTOrderedTree.sameNode(on, ot));
    }

    private void assertSameTree(OrderedNode on, PositionIdentifierTree ot) {
        assertTrue(ot.lookup() + " expected : " + on, CRDTOrderedTree.sameNode(on, ot.lookup()));
    }

    @Test
    public void otTreeTestBasic() throws Exception {
        OTTree tree1 = new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTransformation()), null));
        OTTree tree2 = new OTTree(new SOCT2(1, new SOCT2Log(new OTTreeTransformation()), null));
        CRDTMessage mess = tree1.add(Arrays.asList(new Integer[]{}), 0, 'a');
        mess = mess.concat(tree1.add(Arrays.asList(new Integer[]{0}), 0, 'm')).clone();
        
        
        
        assertEquals(tree1.lookup().getChildrenNumber(), 1);
        assertEquals(tree1.lookup().getChild(0).getChildrenNumber(), 1);
        assertEquals(tree1.lookup().getChild(0).getChild(0).getChildrenNumber(), 0);
        assertEquals(tree1.lookup().getChild(0).getValue(), 'a');
        assertEquals(tree1.lookup().getChild(0).getChild(0).getValue(), 'm');

        tree2.applyRemote(mess);

        assertEquals(tree1, tree2);
        CRDTMessage mess2=tree1.add(Arrays.asList(new Integer[]{0,0}), 0, 'x').clone();
        mess = tree2.add(Arrays.asList(new Integer[]{0}), 0, 'c');
        mess = mess.concat(tree2.add(Arrays.asList(new Integer[]{0, 0}), 0, 'd')).clone();
        Assert.assertFalse(tree1.equals(tree2));
        
        
        tree1.applyRemote(mess);
       
        
        Assert.assertFalse(tree1.equals(tree2));
        
        tree2.applyRemote(mess2);
        
        assertEquals(tree1, tree2);
        assertEquals(tree1.lookup().getChildrenNumber(), 1);
        assertEquals(tree1.lookup().getChild(0).getChildrenNumber(), 2);
        assertEquals(getFromPath(tree1.lookup(),0,0).getChildrenNumber(), 1);
        assertEquals(tree1.lookup().getChild(0).getValue(), 'a');
        assertEquals(getFromPath(tree1.lookup(),0,1).getValue(), 'm');
        assertEquals(getFromPath(tree1.lookup(),0,1,0).getValue(), 'x');
        assertEquals(getFromPath(tree1.lookup(),0,0).getValue(), 'c');
        assertEquals(getFromPath(tree1.lookup(),0,0,0).getValue(), 'd');

    }
    private OrderedNode getFromPath(OrderedNode n,int... p){
        
        for (int a:p){
            n=n.getChild(a);
        }
        return n;
    }
}
