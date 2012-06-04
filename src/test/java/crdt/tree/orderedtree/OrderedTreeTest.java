/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.Node;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.observedremove.CommutativeOrSet;
import crdt.set.observedremove.ConvergentOrSet;
import crdt.tree.wordtree.WordConnectionPolicy;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.WordIncrementalSkip;
import crdt.tree.wordtree.policy.WordIncrementalSkipOpti;
import crdt.tree.wordtree.policy.WordSkip;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.logoot.LogootFactory;
import jbenchmarker.logoot.LogootMerge;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class OrderedTreeTest {
    private void assertSameTree(OrderedNodeImpl on, OrderedNodeImpl ot) {
        assertTrue(on.toString(), on.same(ot));
    }
    
    private void assertSameTree(OrderedNodeImpl on, PositionIdentifierTree ot) {
        assertTrue(ot.lookup().toString(), on.same(ot.lookup()));
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
    
    static OrderedNodeImpl tree(Character ch, Object ... cn) {
        OrderedNodeImpl on = new OrderedNodeImpl(ch, null, null);
        for (Object c : cn) {
            if (c instanceof Node) {
                on.getChildren().add(c);
            } else {
                on.getChildren().add(new OrderedNodeImpl(c, on, null));
            }
        }
        return on;
    }
  
    Factory<MergeAlgorithm> lf = new  LogootFactory();

    OrderedNodeImpl testLogoot(Factory<CRDTSet> sf, Factory<WordConnectionPolicy> wcp) throws PreconditionException {
        WordTree wt = new WordTree(sf.create(), wcp);
        PositionIdentifierTree ot = new PositionIdentifierTree((LogootMerge)lf.create(), wt),
                ot2 = ot.create();
        OrderedNodeImpl on;
        
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
        
        assertSameTree((OrderedNodeImpl) ot.lookup(), ot2);
        return (OrderedNodeImpl) ot.lookup();
    }
    
    void testLogootSkip(Factory<CRDTSet> sf) throws PreconditionException {
        OrderedNodeImpl r = testLogoot(sf, new WordIncrementalSkip()),
                on = tree(null, tree('b', 'y', 'y'), 'c');
        assertSameTree(on, r);
        
        r = testLogoot(sf, new WordIncrementalSkipOpti()); 
        assertSameTree(on, r);

//        r = testLogoot(sf, new WordSkip()); 
//        assertSameTree(on, r);
    }
    
    @Test
    public void testLogootOr() throws PreconditionException {
        testLogootSkip(new CommutativeOrSet());
        testLogootSkip(new ConvergentOrSet());
    }
}
