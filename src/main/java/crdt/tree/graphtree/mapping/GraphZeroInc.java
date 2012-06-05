/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.mapping;

import collect.Node;
import collect.Tree;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.HashMap;

/**
 *
 * @author Stephane Martin
 */
public class GraphZeroInc<T> extends GraphMappPolicyInc<T> {
    Tree tree ;
    HashMap <Node,Integer> nbFather;
    
    @Override
    void addEdge(Edge<T> e) {
            
        
    }

    @Override
    void delEdge(Edge<T> e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

    @Override
    public Tree<T> lookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GraphMappPolicy<T> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void moveEdge(Edge<T> OldFather, Edge<T> moved) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
    
}
