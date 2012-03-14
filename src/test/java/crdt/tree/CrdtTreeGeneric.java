/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree;

import crdt.CRDTMessage;
import collect.HashTree;
import crdt.PreconditionException;
import collect.Tree;
import collect.Node;
import crdt.Factory;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CrdtTreeGeneric<T> {

    final static Tree basicTree = new HashTree();
    final static Node a = basicTree.add(null, 'a'), 
            b = basicTree.add(null, 'b'),
            c = basicTree.add(null, 'c'),
            x = basicTree.add(b, 'x'),
            y = basicTree.add(b, 'y'),
            z = basicTree.add(c, 'z');
    
    public CRDTMessage populateTreeABC(CRDTTree crdttree) throws PreconditionException {
        CRDTMessage ABC = crdttree.add(crdttree.getRoot(), 'a');
        ABC.concat(crdttree.add(crdttree.getRoot(), 'b'));
        ABC.concat(crdttree.add(crdttree.getRoot(), 'c'));
        return ABC;
    }
        
    public CRDTMessage populateTreeXYZ(CRDTTree crdttree) throws PreconditionException {
        CRDTMessage XYZ = crdttree.add(crdttree.getNode('b'), 'x');
        XYZ.concat(crdttree.add(crdttree.getNode('b'), 'y'));
        XYZ.concat(crdttree.add(crdttree.getNode('c'), 'z'));
        return XYZ;
    }
    
    
    /**
     * Runs all basic test (without return)
     * @param treeFactory a CRDT tree factory
     * @throws Exception if test fails
     */
    public void runAllBasic(Factory<CRDTTree> treeFactory) throws Exception {
        this.testAdd(treeFactory.create());
        this.testRemove(treeFactory.create());
        this.testApplyRemoteAdd(treeFactory.create(), treeFactory.create());
        this.testAddConcurDiffFather(treeFactory.create(), treeFactory.create());
        this.testAddConcurSameFather(treeFactory.create(), treeFactory.create());
        this.testRmvConcurDiffFather(treeFactory.create(), treeFactory.create());
        this.testRmvConcurSameFather(treeFactory.create(), treeFactory.create());
        this.testConcuAddRmvDiffFather(treeFactory.create(), treeFactory.create());
    }
    
    
    // Simple Add    
    public void testAdd(CRDTTree crdttree) throws Exception {

        //creatTree
        populateTreeABC(crdttree);
        populateTreeXYZ(crdttree);
        
        //tree to compare
        assertEquals(basicTree, crdttree.lookup());
    }

    // Add + remove   
    public void testRemove(CRDTTree crdttree) throws Exception {
        //creatTree
        populateTreeABC(crdttree);
        populateTreeXYZ(crdttree);
        //simple remove

        //tree to compare
        Tree tree = new HashTree();
        tree.add(null, 'a');
        tree.add(tree.add(null, 'c'), 'z');

        crdttree.remove(crdttree.getNode('b'));
        assertEquals(tree, crdttree.lookup());
    }

    // Add 1 -> 2   
    public void testApplyRemoteAdd(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {

        //TestApply add sequentiel     
        crdttree2.applyRemote(populateTreeABC(crdttree1));
        crdttree2.applyRemote(populateTreeXYZ(crdttree1));

        //tree to compare
        assertEquals(basicTree, crdttree1.lookup());
        assertEquals(crdttree2.lookup(), crdttree1.lookup());

    }

    // Concurrent insertion under two diffrent father      
    public void testAddConcurDiffFather(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {
      
        crdttree2.applyRemote(populateTreeABC(crdttree1));

        //Add concurrently        
        CRDTMessage m1 = crdttree2.add(crdttree2.getNode('b'), 'x'),//in crdtTree2
            m2 = crdttree2.add(crdttree2.getNode('b'), 'y'),//in crdtTree2
            m3 = crdttree1.add(crdttree1.getNode('c'), 'z');//in crdtTree1
        
        m1.concat(m2);
        crdttree1.applyRemote(m1);
        crdttree2.applyRemote(m3);        

        //tree to compare
        assertEquals(basicTree, crdttree1.lookup());
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
    }

    /*
     * Concurrently insertion under same father
     * crdt1 add 'x' and 'y' under crdttree.getNode('b') and crdt2 add 'z' under crdttree.getNode('c') concurrently
     */    
    public void testAddConcurSameFather(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {
    
        crdttree2.applyRemote(populateTreeABC(crdttree1));

        //Add concurrently
        CRDTMessage m1 = crdttree2.add(crdttree2.getNode('b'), 'x'),//in crdtTree2
            m2 = crdttree1.add(crdttree1.getNode('b'), 'y'),//in crdtTree2
            m3 = crdttree2.add(crdttree2.getNode('c'), 'z');//in crdtTree1
        
        m1.concat(m3);
        crdttree1.applyRemote(m1);
        crdttree2.applyRemote(m2); 
        
        assertEquals(basicTree, crdttree1.lookup());
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
    }
    
    /*
     * delete concurrently under two different father
     * crdttree1 delete crdttree.getNode('b') and crdttree2 delete crdttree.getNode('c') concurrently
     * result is juste root --> a
     */   
    public void testRmvConcurDiffFather(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {
        
       
        crdttree2.applyRemote(populateTreeABC(crdttree1)); 
        crdttree2.applyRemote(populateTreeXYZ(crdttree1));

        //del concurrently
        CRDTMessage m1 = crdttree2.remove(crdttree2.getNode('b'));//in crdtTree2
        CRDTMessage m2 = crdttree1.remove(crdttree1.getNode('c'));//in crdtTree1
        
        crdttree1.applyRemote(m1);
        crdttree2.applyRemote(m2);
        
        //tree to compare 
        Tree tree = new HashTree();
        tree.add(null, 'a');

        assertEquals(tree, crdttree1.lookup());
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
    }
    
    /*
     * delete concurrently the same element under same father
     * both crdt delete crdttree.getNode('b') concurrently
     */    
    public void testRmvConcurSameFather(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {
        
        crdttree2.applyRemote(populateTreeABC(crdttree1));
        crdttree2.applyRemote(populateTreeXYZ(crdttree1));

         //del concurrently
        CRDTMessage m1 = crdttree1.remove(crdttree1.getNode('b'));//in crdtTree1
        CRDTMessage m2 = crdttree2.remove(crdttree2.getNode('b'));//in crdtTree2

        crdttree1.applyRemote(m2);
        crdttree2.applyRemote(m1);
        
        //tree to compare
        Tree tree = new HashTree();
        tree.add(null, 'a');
        Node nodeC = tree.add(null, 'c');
        tree.add(nodeC, 'z');

        assertEquals(tree, crdttree1.lookup());
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
    }
    
    /*
     *  Add/delete concurrently under two different father 
     * crdt1 remove crdttree.getNode('c') when crdt2 add 'm' under crdttree.getNode('b') 
     */    
    public void testConcuAddRmvDiffFather(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {

        crdttree2.applyRemote(populateTreeABC(crdttree1));
        crdttree2.applyRemote(populateTreeXYZ(crdttree1));

         //del concurrently
        CRDTMessage m1 = crdttree1.remove(crdttree1.getNode('c'));//in crdtTree1
        CRDTMessage m2 = crdttree2.add(crdttree2.getNode('b'), 'm');//in crdtTree2
        
        crdttree1.applyRemote(m2);
        crdttree2.applyRemote(m1);

        //tree to compare
        Tree tree = new HashTree();
        tree.add(null, 'a');
        Node nodeB = tree.add(null, 'b');
        tree.add(nodeB, 'x');
        tree.add(nodeB, 'y');
        tree.add(nodeB, 'm');

        assertEquals(tree, crdttree1.lookup());
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
    }
    
    /*
     * Add/delete father 
     * crdt2 add 'k' under crdttree.getNode('b') when crdt1 delete crdttree.getNode('b')
     */    
    public Tree testConcurAddRmvFather(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {

        crdttree2.applyRemote(populateTreeABC(crdttree1));
        crdttree2.applyRemote(populateTreeXYZ(crdttree1));

         //del concurrently         
        CRDTMessage m1 = crdttree1.remove(crdttree1.getNode('b'));//delete father of y in crdtTree1
        CRDTMessage m2 = crdttree2.add(crdttree2.getNode('b', 'x'), 'k');//insertion y in crdtTree2

        crdttree1.applyRemote(m2);
        crdttree2.applyRemote(m1);
        
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
        return (Tree) crdttree1.lookup();
    }
    
     /*
      * concurrent Addition and delete of the same element
      * crdt1 insert 'x' under crdttree.getNode('b') ther delete it, crdt2 add crdttree.getNode('b') concurrently
      */ 
    public Tree testConcurAddRmvSameElement(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {
         
        crdttree2.applyRemote(populateTreeABC(crdttree1));
        
        CRDTMessage m1 = crdttree1.add(crdttree1.getNode('b'), 'y');
        CRDTMessage m2 = crdttree1.add(crdttree1.getNode('c'), 'z');
        
        m1.concat(m2);
        crdttree2.applyRemote(m1);

         //del concurrently
        CRDTMessage m3 = crdttree1.add(crdttree1.getNode('b'), 'x');
        CRDTMessage m4 = crdttree2.add(crdttree2.getNode('b'), 'x');//insertion x under B2
        CRDTMessage m5 = crdttree1.remove(crdttree1.getNode('b', 'x'));//delete x

        m3.concat(m5);
        crdttree1.applyRemote(m4);
        crdttree2.applyRemote(m3);
        
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
        return (Tree) crdttree1.lookup();
    } 
    
    /*
     * Creat two path diffrent for same element
     * crdt1 add x under Y and concurrently crdt2 add x under A
     * degre(x) from A = 2 and degre(x) from crdttree.getNode('b') = 3  
     */
    public Tree testTwoPath(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {
         
        crdttree2.applyRemote(populateTreeABC(crdttree1));
        
        CRDTMessage m1 = crdttree1.add(crdttree1.getNode('b'), 'y');
        CRDTMessage m2 = crdttree1.add(crdttree1.getNode('c'), 'z');
        
        m1.concat(m2);
        crdttree2.applyRemote(m1);

         //add concurrently
        CRDTMessage m3 = crdttree1.add(crdttree1.getNode('b', 'y'), 'x');//insertion x under crdttree.getNode('b')
        CRDTMessage m4 = crdttree2.add(crdttree2.getNode('a'), 'x');//insertion x under crdttree.getNode('c')

        crdttree1.applyRemote(m4);
        crdttree2.applyRemote(m3);
        
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
        return (Tree) crdttree1.lookup();         
     }
     /*
      * Creat Cycle
      * crdt1 add x under y and concurrently crdt2 add y under x
      */     
    public Tree testCycle(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {
         
        crdttree2.applyRemote(populateTreeABC(crdttree1));
        
        CRDTMessage m1 = crdttree1.add(crdttree1.getNode('b'), 'y');
        CRDTMessage m2 = crdttree1.add(crdttree1.getNode('b', 'y'), 'x'); 
        m1.concat(m2);
        
        CRDTMessage m3 = crdttree2.add(crdttree2.getNode('b'), 'x');
        CRDTMessage m4 = crdttree2.add(crdttree2.getNode('b', 'x'), 'y');
        m3.concat(m4);
        
        crdttree2.applyRemote(m1);
        crdttree1.applyRemote(m3);
        
        assertEquals(crdttree2.lookup(), crdttree1.lookup());
        return (Tree) crdttree1.lookup();         
    }  
    
    /**
     * Add/delete father 
     * crdt2 add 'k' under crdttree.getNode('b') when crdt1 delete crdttree.getNode('b') then readd father
     */    
    public void testAdopt(CRDTTree crdttree1, CRDTTree crdttree2) throws Exception {
         CrdtTreeGeneric test = new CrdtTreeGeneric();       
         
         crdttree2.applyRemote(test.populateTreeABC(crdttree1));
         crdttree2.applyRemote(test.populateTreeXYZ(crdttree1));


         //del concurrently         
        CRDTMessage m1 = crdttree1.remove(crdttree1.getNode('b'));//delete father of x in crdtTree1
        CRDTMessage m2 = crdttree2.add(crdttree2.getNode('b', 'x'), 'k');//insertion k in crdtTree2
        m2.concat(crdttree2.add(crdttree2.getNode('b', 'x', 'k'), 'f'));//insertion z in crdtTree2

        crdttree1.applyRemote(m2);
        crdttree2.applyRemote(m1);
        
        CRDTMessage m3 = crdttree2.add(crdttree2.getRoot(), 'b');
        m3.concat(crdttree2.add(crdttree2.getNode('b'), 'x')); //reinsertion crdttree.getNode('b') and x in crdtTree2

        crdttree1.applyRemote(m3);
        
        Tree tree = new HashTree();
        tree.add(null, 'a');
        Node b2 = tree.add(null, 'b');
        Node c2 = tree.add(null, 'c');
        Node x2 = tree.add(b2, 'x');
        tree.add(tree.add(x2, 'k'),'f');
        tree.add(c2, 'z');
        assertEquals(tree, crdttree1.lookup());
        assertEquals(tree, crdttree2.lookup());
    }  
}
