/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.mapping;

import collect.HashMapSet;
import collect.HashTree;
import crdt.tree.graphtree.Edge;
import collect.Tree;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.Observable;

/**
 *
 * @author score
 */
public class GraphNewer<T> extends GraphMappPolicy<T>{

//    @Override
    protected Tree<T> getTreeFromMapping(HashMapSet<T, Edge<T>> tree) {
        Tree t = new HashTree();
        
        return t;
    }

    @Override
    public GraphMappPolicy<T> create() {
        return new GraphNewer<T>();
    }

    @Override
    public Tree<T> lookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Observable o, Object o1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}