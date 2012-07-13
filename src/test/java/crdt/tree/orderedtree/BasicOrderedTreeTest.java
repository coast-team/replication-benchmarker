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
package crdt.tree.orderedtree;

import crdt.CRDTMessage;
import crdt.Factory;
import crdt.tree.fctree.FCTree;
import org.junit.Test;
import static crdt.tree.orderedtree.OrderedNodeMock.tree;
import java.util.Arrays;
import junit.framework.Assert;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class BasicOrderedTreeTest {
    
    void sequentialTest(Factory<CRDTOrderedTree> ftree) throws Exception {
        OrderedNodeMock t=tree(null, tree('a'),tree('b','c','d'));
        CRDTOrderedTree<Character> tree1=ftree.create();
        tree1.setReplicaNumber(1);
        CRDTMessage mess=OrderedNodeMock.makeOrderedTreeByMock(t, tree1);
        if (!CRDTOrderedTree.sameNode(tree1.lookup(), t)){
            Assert.failNotEquals("Two tree is different",t,tree1.lookup());
        }
        
        
        CRDTOrderedTree<Character> tree2=ftree.create();
        tree2.setReplicaNumber(2);
        tree2.applyRemote(mess);
        if (!tree1.lookup().equals(tree2.lookup())){
            Assert.fail("Tree are not integrated correctly :\n"+mess
                    +"\n\nexpected Tree :\n "+t
                    +"\n\n tree found :\n"+tree2.lookup());
        }
        mess=tree1.remove(Arrays.asList(1,0));
        OrderedNodeMock t2=tree(null, tree('a'),tree('b','d'));
        if (!CRDTOrderedTree.sameNode(tree1.lookup(), t2)){
            Assert.failNotEquals("Two tree is different",t2,tree1.lookup());
        }
        tree2.applyRemote(mess);
         if (!tree1.lookup().equals(tree2.lookup())){
            Assert.fail("Tree are not integrated correctly :\n"+mess
                    +"\n\nexpected Tree :\n "+t
                    +"\n\n tree found :\n"+tree2.lookup());
        }
         
    }
    @Test
    public void insertionTest()throws Exception {
        sequentialTest(new FCTree());
    }
}
