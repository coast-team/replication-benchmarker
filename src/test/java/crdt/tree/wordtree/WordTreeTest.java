/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree;

import collect.HashTree;
import collect.Node;
import collect.Tree;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.counter.CommutativeCounterSet;
import crdt.set.counter.ConvergentCounterSet;
import crdt.set.lastwriterwins.CommutativeLwwSet;
import crdt.set.lastwriterwins.ConvergentLwwSet;
import crdt.set.observedremove.CommutativeOrSet;
import crdt.set.observedremove.ConvergentOrSet;
import crdt.tree.CRDTTree;
import crdt.tree.CrdtTreeGeneric;
import crdt.tree.wordtree.policy.WordCompact;
import crdt.tree.wordtree.policy.WordIncrementalCompact;
import crdt.tree.wordtree.policy.WordIncrementalReappear;
import crdt.tree.wordtree.policy.WordIncrementalRoot;
import crdt.tree.wordtree.policy.WordIncrementalSkip;
import crdt.tree.wordtree.policy.WordIncrementalSkipOpti;
import crdt.tree.wordtree.policy.WordReappear;
import crdt.tree.wordtree.policy.WordRoot;
import crdt.tree.wordtree.policy.WordSkip;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class WordTreeTest {
    
    public WordTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    
    @Test(expected = PreconditionException.class)
    public void testNotIn() throws Exception {  
        WordTree t = new WordTree(new CommutativeCounterSet(), new WordSkip());
        t.add(t.getRoot(), 'a');
        Node a = t.getRoot().getChild('a');
        t.remove(a);
        t.add(a, 'b');
    } 
    
    public void testWordBasic(Factory<CRDTSet> sf) throws Exception {
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        test.runAllBasic(new WordTree(sf.create(), new WordSkip()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalSkip()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalSkipOpti()));
        test.runAllBasic(new WordTree(sf.create(), new WordReappear()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalReappear()));
        test.runAllBasic(new WordTree(sf.create(), new WordRoot()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalRoot()));
        test.runAllBasic(new WordTree(sf.create(), new WordCompact()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalCompact()));
        testRoot(sf, new WordRoot());
        testRootDouble(sf, new WordRoot());
//        testRoot(sf, new WordIncrementalRoot());
//        testRootDouble(sf, new WordIncrementalRoot());
        testCompact(sf, new WordCompact());
        testCompact(sf, new WordIncrementalCompact());
    }
        
    @Test
    public void testCmCWS() throws Exception {
        Tree tr, resultTree;
        Node a, b, c, x, y, z, k;
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        Factory<CRDTTree> tf = new WordTree(new CommutativeCounterSet(), new WordSkip());
        test.testAdopt(tf.create(), tf.create());
        
        tr = test.testConcurAddRmvFather(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        c = resultTree.add(null, 'c');
        z = resultTree.add(c, 'z');
        
        assertEquals(resultTree, tr);
        
        tr = test.testConcurAddRmvSameElement(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a'); 
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');
        
        assertEquals(resultTree, tr);
        
        tr = test.testTwoPath(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a'); 
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(a, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');
        resultTree.add(y, 'x');
        
        assertEquals(resultTree, tr);
                
        tr = test.testCycle(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a'); 
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        y = resultTree.add(b, 'y');
        resultTree.add(x, 'y');
        resultTree.add(y, 'x');    
        
        assertEquals(resultTree, tr);
    }    

    @Test
    public void testCmOWS() throws Exception {
        Tree tr, resultTree;
        Node a, b, c, x, y, z, k;
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        Factory<CRDTTree> tf = new WordTree(new CommutativeOrSet(), new WordSkip());
        test.testAdopt(tf.create(), tf.create());
        
        tr = test.testConcurAddRmvFather(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        c = resultTree.add(null, 'c');
        z = resultTree.add(c, 'z');
        
        assertEquals(resultTree, tr);
        
        tr = test.testConcurAddRmvSameElement(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a'); 
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');
        
        assertEquals(resultTree, tr);
        
        tr = test.testTwoPath(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a'); 
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(a, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');
        resultTree.add(y, 'x');
        
        assertEquals(resultTree, tr);
                
        tr = test.testCycle(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a'); 
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        y = resultTree.add(b, 'y');
        resultTree.add(x, 'y');
        resultTree.add(y, 'x');    
        
        assertEquals(resultTree, tr);
    }    
    
/*
        a = resultTree.innerAdd(null, 'a'); 
        b = resultTree.innerAdd(null, 'b');
        c = resultTree.innerAdd(null, 'c');
        x = resultTree.innerAdd(b, 'x');
        y = resultTree.innerAdd(b, 'y');
        z = resultTree.innerAdd(c, 'z');
*/
     
    @Test
    public void testCmCWR() throws Exception {
        Tree tr, resultTree;
        Node a, b, c, x, y, z, k;
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        CRDTSet s = new CommutativeCounterSet();
        Factory<CRDTTree> tf = new WordTree(s, new WordReappear());
        
        tr = test.testConcurAddRmvFather(tf.create(), tf.create());
        resultTree = new HashTree();
        
        a = resultTree.add(null, 'a'); 
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        k = resultTree.add(x, 'k');
        z = resultTree.add(c, 'z');
        
        assertEquals(resultTree, tr);
    }    
            
    @Test
    public void testCmLWR() throws Exception {
        Tree tr, resultTree;
        Node a, b, c, x, y, z, k;
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        Factory<CRDTTree> tf = new WordTree(new CommutativeLwwSet(), new WordReappear());
        
        tr = test.testConcurAddRmvSameElement(tf.create(), tf.create());
        resultTree = new HashTree();
        
        a = resultTree.add(null, 'a'); 
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
//        x = resultTree.innerAdd(b, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');
        
        assertEquals(resultTree, tr);
    }  
    
    @Test
    public void testBasicCmCounter() throws Exception {
        testWordBasic(new CommutativeCounterSet());
    }

    @Test
    public void testBasicCvCounter() throws Exception {
        testWordBasic(new ConvergentCounterSet());
    }

    @Test
    public void testBasicCmLww() throws Exception {
        testWordBasic(new CommutativeLwwSet());
    }

    @Test
    public void testBasicCvLww() throws Exception {
        testWordBasic(new ConvergentLwwSet());
    }
    
    @Test
    public void testBasicCmOr() throws Exception {
        testWordBasic(new CommutativeOrSet());
    }

    @Test
    public void testBasicCvOr() throws Exception {
        testWordBasic(new ConvergentOrSet());
    }

    private void testRoot(Factory<CRDTSet> sf, Factory<WordPolicy> rf) throws PreconditionException {
        WordTree wt1 = new WordTree(sf.create(), rf.create()),
                wt2 = new WordTree(sf.create(), rf.create());
        CRDTMessage m1 = wt1.add(wt1.getRoot(), 'a');
        m1.concat(wt1.add(wt1.getRoot(), 'c'));
        m1.concat(wt1.add(wt1.getRoot().getChild('a'), 'b'));
        wt2.applyRemote(m1);
        
        CRDTMessage m2 = wt2.remove(wt2.getRoot().getChild('a').getChild('b'));
        m1 = wt1.add(wt1.getRoot().getChild('a').getChild('b'), 'a');
        wt1.applyRemote(m2);
        wt2.applyRemote(m1);
        
        Tree rt = new HashTree();
        rt.add(null, 'a'); rt.add(null, 'c');
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());
        
        m1 = wt1.remove(wt1.getRoot().getChild('a'));
        wt2.applyRemote(m1);
        rt = new HashTree(); rt.add(null, 'c');       
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());        
    }
    
    private void testRootDouble(Factory<CRDTSet> sf, Factory<WordPolicy> rf) throws PreconditionException {
        WordTree wt1 = new WordTree(sf.create(), new WordRoot()),
                wt2 = new WordTree(sf.create(), new WordRoot());
        CRDTMessage m1 = wt1.add(wt1.getRoot(), 'a');
        m1.concat(wt1.add(wt1.getRoot(), 'c'));
        m1.concat(wt1.add(wt1.getRoot().getChild('a'), 'b'));
        wt2.applyRemote(m1);
        
        CRDTMessage m2 = wt2.remove(wt2.getRoot().getChild('a').getChild('b'));
        m1 = wt1.add(wt1.getRoot().getChild('a').getChild('b'), 'a');
        wt1.applyRemote(m2);
        wt2.applyRemote(m1);
        
        m1 = wt1.add(wt1.getRoot().getChild('a'), 'd');
        m2 = wt2.add(wt2.getRoot().getChild('a'), 'b');
        wt1.applyRemote(m2);
        wt2.applyRemote(m1);
        
        Tree rt = new HashTree(); 
        Node a = rt.add(null, 'a'), 
                a2 = rt.add(rt.add(a, 'b'), 'a');
        rt.add(null, 'c'); rt.add(a, 'd');         
        rt.add(a2, 'd'); rt.add(a2, 'b');
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());        
    }
    
    private void testCompact(Factory<CRDTSet> sf, Factory<WordPolicy> cf) throws PreconditionException {
        WordTree wt1 = new WordTree(sf.create(), cf.create()),
                wt2 = new WordTree(sf.create(), cf.create());
        CRDTMessage m1 = wt1.add(wt1.getRoot(), 'a');
        m1.concat(wt1.add(wt1.getRoot(), 'c'));
        m1.concat(wt1.add(wt1.getRoot().getChild('a'), 'a'));
        m1.concat(wt1.add(wt1.getRoot().getChild('a'), 'b'));
        wt2.applyRemote(m1);
        
        CRDTMessage m2 = wt2.remove(wt2.getRoot().getChild('a').getChild('b'));
        m1 = wt1.add(wt1.getRoot().getChild('a').getChild('b'), 'a');
        wt1.applyRemote(m2);
        wt2.applyRemote(m1);
        
        Tree rt = new HashTree();
        rt.add(rt.add(null, 'a'), 'a'); rt.add(null, 'c');
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());
        
        m1 = wt1.remove(wt1.getRoot().getChild('a').getChild('a'));
        wt2.applyRemote(m1);
        rt = new HashTree(); rt.add(null, 'a'); rt.add(null, 'c');       
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());        
    }
}
