/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.DecoratedNode;
import collect.DecoratedTree;
import collect.Node;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author score
 */
public class CounterTree<T> extends DecoratedTree<T,Set<List<T>>> {

    @Override
    protected DecoratedNode<T,Set<List<T>>> createNode(Node<T> father, T t) {
        DecoratedNode<T,Set<List<T>>> n = super.createNode(father, t);
        setAttached(n, new HashSet());
        return n;
    }

    @Override
    protected DecoratedNode<T,Set<List<T>>> createRoot() {
        DecoratedNode<T,Set<List<T>>> n = super.createRoot();
        Set<List<T>> s = new HashSet();
        s.add(Collections.EMPTY_LIST);
        setAttached(n, s);
        return n;
    }
    
    
    Node<T> add(Node<T> father, T t, List<T> word) {
        Node<T> node = super.add(father, t);
        if (!getAttached(node).add(word))          
            throw new IllegalStateException(); 
        return node;
    }

    public void remove(Node<T> node, List<T> word) {
        if (!getAttached(node).remove(word))
            throw new IllegalStateException();            
        if (getAttached(node).isEmpty()) {
            super.remove(node);
        }
    }    
}

