/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree;

import collect.Node;
import collect.Tree;
import crdt.Factory;
import java.util.Collection;
import java.util.List;
import java.util.Observer;

/**
 *
 * @author urso
 */
public interface WordPolicy<T> extends Factory<WordPolicy<T>>, Observer {
    
    /**
     * The lookup computed by the policy
     * @return a tree
     */
    public Tree<T> lookup();

    /**
     * Mapping between tree lookup node and words
     * @return a bimap
     */
    public Collection<List<T>> addMapping(Node<T> node);

    public Collection<List<T>> delMapping(Node<T> node);
}
