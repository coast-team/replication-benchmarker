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

import collect.OrderedNode;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.NaiveSet;
import crdt.set.lastwriterwins.CommutativeLwwSet;
import crdt.set.lastwriterwins.ConvergentLwwSet;
import crdt.set.observedremove.CommutativeOrSet;
import crdt.set.observedremove.ConvergentOrSet;
import crdt.simulator.CausalDispatcherSetsAndTreesTest;
import crdt.simulator.IncorrectTraceException;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.OrderedTreeOperationProfile;
import crdt.tree.fctree.FCTree;
import static crdt.tree.orderedtree.OrderedNodeMock.tree;
import crdt.tree.wordtree.WordConnectionPolicy;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.factories.LogootFactory;
import jbenchmarker.logoot.BoundaryStrategy;
import jbenchmarker.logoot.LogootStrategy;
import jbenchmarker.ot.ottree.OTTree;
import jbenchmarker.ot.ottree.OTTreeTranformation;
import jbenchmarker.ot.ottree.TreeOPT;
import jbenchmarker.ot.ottree.TreeOPTTTFTranformation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2Log;
import jbenchmarker.ot.soct2.SOCT2LogTTFOpt;
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

    public static PositionIdentifierTree createTree(OrderedNode root, Factory<CRDTSet> sf, Factory<WordConnectionPolicy> wcp) {
        WordTree wt = new WordTree(sf.create(), wcp);
        return new PositionIdentifierTree((PositionnedNode)root.createNode(null), wt);       
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
        new WordIncrementalRoot(), 
        new WordIncrementalCompact(), 
        new WordIncrementalSkipOpti()};
    
    public static OperationProfile otreeop = new OrderedTreeOperationProfile(0.6, 0.7) {

        @Override
        public Object nextElement() {
            return (char) ('a'+(int) (Math.random() * 26));
        }
    };
    
    
    public void testRoot(Factory<CRDTOrderedTree> f) throws PreconditionException {
        CRDTOrderedTree<Character> t0 = f.create(), t1 = f.create();
        t0.setReplicaNumber(0); t1.setReplicaNumber(1);
        CRDTMessage m11, m12, m01, m02, m13, m03;
        m11 = t1.applyLocal(new OrderedTreeOperation(path(), 0, 'r'));
        m12 = t1.applyLocal(new OrderedTreeOperation(path(0)));
        t0.applyRemote(m11);
        m01 = t0.applyLocal(new OrderedTreeOperation(path(), 0, 'n'));
        m02 = t0.applyLocal(new OrderedTreeOperation(path(1), 0, 'b'));
        t0.applyRemote(m12);
        t1.applyRemote(m01);
        m13 = t1.applyLocal(new OrderedTreeOperation(path(0), 0, 'y'));
        m03 = t0.applyLocal(new OrderedTreeOperation(path(0)));
        
        t0.applyRemote(m13);
        t1.applyRemote(m02);
        t1.applyRemote(m03);
        assertTrue(t0.sameLookup(t1));
    } 
    
    
    @Test
    public void testRunsNaiveLogootSkip() throws PreconditionException, IncorrectTraceException, IOException {
        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new LogootTreeNode(null, 0, 
                    32, new BoundaryStrategy(100)), new NaiveSet(), new WordIncrementalSkip()), 2000, 100, 5, otreeop);
    }
    
    @Test
    public void testRunsNaiveLogootSkipOpti() throws PreconditionException, IncorrectTraceException, IOException {
        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new LogootTreeNode(null, 0, 
                    32, new BoundaryStrategy(100)), new NaiveSet(), new WordIncrementalSkipOpti()), 2000, 100, 5, otreeop);
    }
    
    @Test
    public void testRunsNaiveLogootSkipUnique() throws PreconditionException, IncorrectTraceException, IOException {
        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new LogootTreeNode(null, 0, 
                    32, new BoundaryStrategy(100)), new NaiveSet(), new WordIncrementalSkipUnique()), 2000, 100, 5, otreeop);
    }
    
    @Test
    public void testRunsNaiveLogootReappear() throws PreconditionException, IncorrectTraceException, IOException {
        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new LogootTreeNode(null, 0, 
                    32, new BoundaryStrategy(100)), new NaiveSet(), new WordIncrementalReappear()), 2000, 100, 5, otreeop);
    }
    
//    @Test
//    public void testRunsNaiveLogootRoot() throws PreconditionException, IncorrectTraceException, IOException {
//        PositionIdentifierTree tree = createTree(new LogootTreeNode(null, 0, 32, 
//                new BoundaryStrategy(10)), new NaiveSet(), new WordIncrementalRoot());
//        for (int i = 0; i < 100; ++i) { 
//            testRoot(tree);
//        }
//        CausalDispatcherSetsAndTreesTest.testRunX(tree, 2000, 100, 5, otreeop);        
//    }
//    
//    @Test
//    public void testRunsNaiveLogootCompact() throws PreconditionException, IncorrectTraceException, IOException {
//        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new LogootTreeNode(null, 0, 
//                    32, new BoundaryStrategy(100)), new NaiveSet(), new WordIncrementalCompact()), 2000, 100, 5, otreeop);
//    }
//    
//    @Test
//    public void testRunsNaiveWootHashSkip() throws PreconditionException, IncorrectTraceException, IOException {
//        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new WootHashTreeNode(null, 0), 
//                new NaiveSet(), new WordIncrementalSkip()), 2000, 100, 5, otreeop);
//    }
//    
//    @Test
//    public void testRunsNaiveWootHashSkipOpti() throws PreconditionException, IncorrectTraceException, IOException {
//        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new WootHashTreeNode(null, 0), 
//                new NaiveSet(), new WordIncrementalSkipOpti()), 2000, 100, 5, otreeop);
//    }
//    
//    @Test
//    public void testRunsNaiveWootHashReappear() throws PreconditionException, IncorrectTraceException, IOException {
//        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new WootHashTreeNode(null, 0), 
//                new NaiveSet(), new WordIncrementalReappear()), 2000, 100, 5, otreeop);
//    }
//    
//    @Test
//    public void testRunsNaiveWootHashRoot() throws PreconditionException, IncorrectTraceException, IOException {
//        PositionIdentifierTree tree = createTree(new WootHashTreeNode(null, 0), new CommutativeLwwSet(), new WordIncrementalRoot());
//        for (int i = 0; i < 100; ++i) { 
//            testRoot(tree);
//        }
//        CausalDispatcherSetsAndTreesTest.testRunX(tree, 2000, 100, 5, otreeop);        
//    }
//    
//    @Test
//    public void testRunsNaiveWootHashCompact() throws PreconditionException, IncorrectTraceException, IOException {
//        CausalDispatcherSetsAndTreesTest.testRunX(createTree(new WootHashTreeNode(null, 0), 
//                new NaiveSet(), new WordIncrementalCompact()), 2000, 100, 5, otreeop);
//    }

    
    @Test
    public void testRunsOTTree() throws PreconditionException, IncorrectTraceException, IOException {
        //new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()), null));
        //for(int p=0;p<20000;p++){
            
            CausalDispatcherSetsAndTreesTest.testRunX(new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()), null)), 2000, 100, 5, otreeop);
        //}
    }
     @Test
    public void testRunsFCTree() throws PreconditionException, IncorrectTraceException, IOException {
        //new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()), null));
        //for(int p=0;p<20000;p++){
            
            CausalDispatcherSetsAndTreesTest.testRunX(new FCTree(), 2000, 100, 5, otreeop);
        //}
    }
      @Test
    public void testRunsTreeOPT() throws PreconditionException, IncorrectTraceException, IOException {
        //new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()), null));
        //for(int p=0;p<20000;p++){
            
            CausalDispatcherSetsAndTreesTest.testRunX(new TreeOPT(new SOCT2(0, new SOCT2Log(new TreeOPTTTFTranformation()), null)), 2000, 100, 5, otreeop);
        //}
    }
         @Test
    public void testRunsTreeOPT2() throws PreconditionException, IncorrectTraceException, IOException {
        //new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()), null));
        //for(int p=0;p<20000;p++){
            
            CausalDispatcherSetsAndTreesTest.testRunX(new TreeOPT(new SOCT2(0, new SOCT2LogTTFOpt(new TreeOPTTTFTranformation()), null)), 2000, 100, 5, otreeop);
        //}
    }
          @Test
    public void testRunsOTTree2() throws PreconditionException, IncorrectTraceException, IOException {
        //new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()), null));
        //for(int p=0;p<20000;p++){
            
            CausalDispatcherSetsAndTreesTest.testRunX(new OTTree(new SOCT2(0, new SOCT2LogTTFOpt(new OTTreeTranformation()), null)), 2000, 100, 5, otreeop);
        //}
    }
}
