/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.mapping;

import collect.HashMapSet;
import collect.HashTree;
import collect.Tree;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.Observable;

/**
 *
 * @author score
 */
public class GraphHigher<T> extends GraphMappPolicy<T>{

//    @Override
    protected Tree<T> getTreeFromMapping(HashMapSet<T, Edge<T>> setTree) {
        Tree tree = new HashTree();

        
        return tree;
    }

    @Override
    public GraphMappPolicy<T> create() {
        return new GraphHigher<T>();
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
