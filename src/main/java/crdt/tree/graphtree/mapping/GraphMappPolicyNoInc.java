/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.mapping;

import collect.HashMapSet;
import collect.Tree;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.Observable;

/**
 *
 * @author Stephane Martin
 */
public abstract class GraphMappPolicyNoInc<T> extends GraphMappPolicy {

    boolean fresh = false;
    Tree<T> tree;

    @Override
    public Tree lookup() {
        if (!fresh) {
            tree = getTreeFromMapping(this.getGcp().lookup());
        }

        return tree;
    }

    @Override
    public void update(Observable o, Object o1) {
        fresh = false;
    }

    abstract protected Tree<T> getTreeFromMapping(HashMapSet<T, Edge<T>> tree);
}
