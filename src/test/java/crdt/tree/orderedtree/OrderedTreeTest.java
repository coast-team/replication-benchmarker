/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.NaiveSet;
import crdt.set.observedremove.CommutativeOrSet;
import crdt.set.observedremove.ConvergentOrSet;
import crdt.simulator.CausalDispatcherSetsAndTreesTest;
import crdt.simulator.IncorrectTraceException;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.OrderedTreeOperationProfile;
import static crdt.tree.orderedtree.OrderedNodeMock.tree;
import crdt.tree.wordtree.WordConnectionPolicy;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.WordIncrementalReappear;
import crdt.tree.wordtree.policy.WordIncrementalSkip;
import crdt.tree.wordtree.policy.WordIncrementalSkipOpti;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.logoot.BoundaryStrategy;
import jbenchmarker.factories.LogootFactory;
import jbenchmarker.logoot.LogootStrategy;
import jbenchmarker.ot.ottree.OTTree;
import jbenchmarker.ot.ottree.OTTreeTranformation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2Log;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class OrderedTreeTest {
    
    private void assertSameTree(OrderedNode on, OrderedNode ot) {
        assertTrue(ot + " expected : " + on, CRDTOrderedTree.sameNode(on, ot));
        System.out.println("->"+ot+on);
    }
    
    private void assertSameTree(OrderedNode on, PositionIdentifierTree ot) {
        assertTrue(ot.lookup() + " expected : " + on, CRDTOrderedTree.sameNode(on, ot.lookup()));
    }
    
    static class IntegerPos implements PositionIdentifier {
        int p;

        public IntegerPos(int p) {
            this.p = p;
        }
    }
    
    static List<Integer> path(int ... p) {
        List<Integer> l = new LinkedList<Integer>();
        for (int i : p) {
            l.add(i);
        }
        return l;
    }

    PositionIdentifierTree createTree(OrderedNode root, Factory<CRDTSet> sf, Factory<WordConnectionPolicy> wcp) {
        WordTree wt = new WordTree(sf.create(), wcp);
        return new PositionIdentifierTree(root.createNode(null), wt);       
    }
    
    Factory<MergeAlgorithm> lf = new  LogootFactory();

    OrderedNode testTree(PositionIdentifierTree tf) throws PreconditionException {
        PositionIdentifierTree ot = tf.create(),
                ot2 = tf.create();
        ot.setReplicaNumber(1); ot2.setReplicaNumber(2);
        
        OrderedNodeMock on;
        
        CRDTMessage m1 = ot.add(path(), 0, 'x');
        ot2.applyRemote(m1);
        
        on = tree(null, 'x');
        assertSameTree(on, ot); 
        assertSameTree(on, ot2);
        
        CRDTMessage m2 = ot2.add(path(), 0, 'a'), m3 = ot.add(path(), 1, 'c');
        ot.applyRemote(m2); ot2.applyRemote(m3);

        on = tree(null, 'a', 'x', 'c');
        assertSameTree(on, ot); 
        assertSameTree(on, ot2);
        
        CRDTMessage m4 = ot.remove(path(1)), m5 = ot2.add(path(), 1, 'b');
        ot2.applyRemote(m4); ot.applyRemote(m5);
        
        on = tree(null, 'a', 'b', 'c');
        assertSameTree(on, ot); 
        assertSameTree(on, ot2);     

        CRDTMessage m6 = ot.add(path(1), 0, 'y'), m7 = ot2.add(path(1), 0, 'y');
        ot2.applyRemote(m6); ot.applyRemote(m7);
        
        on = tree(null, 'a', tree('b', 'y', 'y'), 'c');
        assertSameTree(on, ot); 
        assertSameTree(on, ot2);     

        CRDTMessage m8 = ot.remove(path(0)), m9 = ot2.add(path(0), 0, 'y');
        ot2.applyRemote(m8); ot.applyRemote(m9);
        
        assertSameTree(ot.lookup(), ot2.lookup());
        return (OrderedNode) ot.lookup();
    }
    
    void testSkip(OrderedNode root, Factory<CRDTSet> sf) throws PreconditionException {
        OrderedNode r = testTree(createTree(root, sf, new WordIncrementalSkip()));
        OrderedNodeMock on = tree(null, tree('b', 'y', 'y'), 'c');
        assertSameTree(on, r);
        
        r = testTree(createTree(root, sf, new WordIncrementalSkipOpti())); 
        assertSameTree(on, r);
//        r = testLogoot(sf, new WordSkip()); 
//        assertSameTree(on, r);
    }
    
    public void testSetSkips(OrderedNode root) throws PreconditionException {
        testSkip(root, new CommutativeOrSet());
        testSkip(root, new ConvergentOrSet());
        testSkip(root, new NaiveSet());
    }
    
    @Test 
    public void testFoo() throws PreconditionException {
        List a = new ArrayList(), b = new LinkedList();
        a.add('a'); b.add('c'); b.add('a'); b.add('b');
        assertEquals(a, b.subList(1, 2));
    }
    
    @Test 
    public void testLogootTree() throws PreconditionException {
        LogootStrategy st = new BoundaryStrategy(100);
        OrderedNode root = new LogootTreeNode(null, 0, 32, st);
        testSetSkips(root);
    }
        
    @Test 
    public void testLogootTreeBoundary() throws PreconditionException {
        LogootStrategy st = new BoundaryStrategy(1);
        OrderedNode root = new LogootTreeNode(null, 0, 2, st);
        testSetSkips(root);
    }
    
    @Test 
    public void testWootHTree() throws PreconditionException {
        OrderedNode root = new WootHashTreeNode(null, 0);
        testSetSkips(root);
    }
    
    Factory policies[] = {// new WordSkip(), new WordReappear(), new WordRoot(), new WordCompact(),
        new WordIncrementalSkip(), 
        new WordIncrementalReappear(),
        // new WordIncrementalRoot(), new WordIncrementalCompact(), 
        new WordIncrementalSkipOpti()};
    
    private OperationProfile otreeop = new OrderedTreeOperationProfile(0.6, 0.4) {

        @Override
        public Object nextElement() {
            return (char) ('a'+(int) (Math.random() * 26));
        }
    };
    
    @Test
    public void testRunsNaiveLogoot() throws PreconditionException, IncorrectTraceException, IOException {
        for (Factory p : policies) {
            CausalDispatcherSetsAndTreesTest.testRun(createTree(new LogootTreeNode(null, 0, 
                    32, new BoundaryStrategy(100)), new NaiveSet(), p), 1000, 5, otreeop);
        }
    }
    
    @Test
    public void testRunsNaiveWootH() throws PreconditionException, IncorrectTraceException, IOException {
        for (Factory p : policies) {
            CausalDispatcherSetsAndTreesTest.testRun(createTree(new WootHashTreeNode(null, 0), new NaiveSet(), p), 1000, 5, otreeop);
        }
    }
    
    public void testRunsOTTree() throws PreconditionException, IncorrectTraceException, IOException {
        //new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()), null));
        CausalDispatcherSetsAndTreesTest.testRun(new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()), null)), 1000, 5, otreeop);
    }
}
