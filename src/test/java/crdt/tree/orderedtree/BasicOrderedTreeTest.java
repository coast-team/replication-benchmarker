/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
