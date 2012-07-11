/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package results;

import collect.OrderedNode;
import crdt.Factory;
import crdt.set.CRDTSet;
import crdt.set.NaiveSet;
import crdt.simulator.CausalSimulator;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.OrderedTreeOperationProfile;
import crdt.simulator.random.RandomTrace;
import crdt.tree.orderedtree.LogootTreeNode;
import crdt.tree.orderedtree.PositionIdentifierTree;
import crdt.tree.orderedtree.PositionnedNode;
import crdt.tree.wordtree.WordConnectionPolicy;
import crdt.tree.wordtree.WordTree;
import crdt.tree.wordtree.policy.WordIncrementalSkip;
import crdt.tree.wordtree.policy.WordIncrementalSkipUnique;
import jbenchmarker.logoot.BoundaryStrategy;

/**
 *
 * @author urso
 */
public class Profiling {
    public static OperationProfile otreeop = new OrderedTreeOperationProfile(0.6, 0.7) {

        @Override
        public Object nextElement() {
            return (char) ('a'+(int) (Math.random() * 26));
        }
    };
    
    public static PositionIdentifierTree createTree(OrderedNode root, Factory<CRDTSet> sf, Factory<WordConnectionPolicy> wcp) {
        WordTree wt = new WordTree(sf.create(), wcp);
        return new PositionIdentifierTree((PositionnedNode)root.createNode(null), wt);       
    }
    
    public static void main(String args[]) throws Exception {
        int times = 100, 
                duration = 1000,
                nbreplica = 5;
   
         Factory f = createTree(new LogootTreeNode(null, 0, 32, new BoundaryStrategy(100)), new NaiveSet(), new WordIncrementalSkipUnique());
         for (int t = 0; t < times; ++t) {
            //System.out.println(t);
            CausalSimulator cd = new CausalSimulator(f);
            cd.run(new RandomTrace(duration, RandomTrace.FLAT, otreeop, 0.4, 3, 2, nbreplica), false);
            System.out.println(t);
        }    
    }
}
