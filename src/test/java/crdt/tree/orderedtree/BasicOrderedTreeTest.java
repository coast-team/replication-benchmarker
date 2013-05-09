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
package crdt.tree.orderedtree;

import crdt.CRDTMessage;
import crdt.Factory;
import crdt.tree.fctree.FCTreeGf;
import crdt.tree.fctree.FCTreeT;
import org.junit.Test;
import static crdt.tree.orderedtree.OrderedNodeMock.tree;
import java.util.Arrays;
import jbenchmarker.ot.ottree.OTTree;
import jbenchmarker.ot.ottree.OTTreeTransformation;
import jbenchmarker.ot.ottree.TreeOPT;
import jbenchmarker.ot.ottree.TreeOPTTTFTranformation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2Log;
import jbenchmarker.ot.soct2.SOCT2LogTTFOpt;
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
       sequentialTest(new FCTreeT());
    }
    @Test
    public void insertion2Test()throws Exception {
       sequentialTest(new FCTreeGf());
    }
    @Test
    public void TreeOPTTest()throws Exception {
       sequentialTest(new TreeOPT(new SOCT2(0, new SOCT2Log(new TreeOPTTTFTranformation()), null)));
    }
     @Test
    public void TreeOPTOTest()throws Exception {
       sequentialTest(new TreeOPT(new SOCT2(0, new SOCT2LogTTFOpt(new TreeOPTTTFTranformation()), null)));
    }
    @Test
    public void OTTREEeOTest()throws Exception {
       sequentialTest(new OTTree(new SOCT2(0, new SOCT2LogTTFOpt(new OTTreeTransformation()), null)));
    }
}
