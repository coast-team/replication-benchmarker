/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree;

import collect.Node;
import collect.Tree;
import collect.UnorderedNode;
import crdt.Factory;
import java.util.*;

/**
 *
 * @author urso
 */
public abstract class WordPolicy<T> implements Factory<WordPolicy<T>>, Observer {
    
    /**
     * The lookup computed by the policy
     * @return a tree
     */
    abstract public Tree<T> lookup();

    /**
     * Mapping between tree lookup node and words
     * @return a bimap
     */
    abstract public Collection<List<T>> addMapping(UnorderedNode<T> node);

    abstract protected Collection<List<T>> delMapping(UnorderedNode<T> node);
    
    public Collection<List<T>> toBeRemoved(UnorderedNode<T> subtree) {       
        Iterator<? extends Node<T>> subtreeIt = lookup().getBFSIterator(subtree);
        List<List<T>> toBeRemoved = new LinkedList<List<T>>();
        while (subtreeIt.hasNext()) {
            UnorderedNode<T> n = (UnorderedNode<T>) subtreeIt.next();
            Collection<List<T>> w = delMapping(n);
            toBeRemoved.addAll(0, w);
        }
        return toBeRemoved;
    }
}
