/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.mapping;

import collect.Tree;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphMappPolicy;

/**
 *
 * @author Stephane Martin
 */
public class GraphServeralInc<T> extends GraphMappPolicyInc<T> {

    @Override
    void addEdge(Edge<T> e) {
        throw new UnsupportedOperationException("Not supported yet.");
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
