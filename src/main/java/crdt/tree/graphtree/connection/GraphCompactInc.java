/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.connection;

import crdt.tree.graphtree.Edge;

/**
 *
 * @author moi
 */
public class GraphCompactInc<T> extends GraphConnectionPolicyInc<T> {
    
    
    @Override
    void addEdge(Edge<T> edge) {
        
    }

    @Override
    void delEdge(Edge<T> edge) {

    }

    @Override
    GraphConnectionPolicyInc createSpe() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
