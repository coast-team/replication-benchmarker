/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @param <T> 
 * @author moi
 */
public interface Tree<T> {
    /**
     * Adds an element in the tree 
     * @param father a node of the tree, null for root
     * @param t the element to add
     * @return the created node
     */
    public Node<T> add(Node<T> father, T t)  ;
    
    /**
     * Creates a node without father
     * @param t the element of the node
     * @return the created node
     */
    public Node<T> createOrphan(T t)  ;

    /**
     * Deletes a subtree in the tree.
     * @param node the root of the subtree.
     */
    public void remove(Node<T> node);
    
    /**
     * Moves a node t under node father.
     * @param father
     * @param node
     */
    public void move(Node<T> father, Node <T> node);
    /**
     * Creates an iterator traversing the tree in BFS order 
     * @param node a node in the tree (null for root)
     * @return the iterator
     */
    public Iterator<? extends Node<T>> getBFSIterator(Node<T> node);

    /**
     * Creates an iterator traversing the tree in DFS order 
     * @param node a node in the tree (null for root)
     * @return the iterator
     */
    public Iterator<? extends Node<T>> getDFSIterator(Node<T> node);

    /**
     * The root of the tree 
     * @return the root of the tree 
     */
    public Node<T> getRoot();
    
    /**
     * A node in the tree at a path
     * @param path the path to the node
     * @return a node at this path
     */
    public Node<T> getNode(List<T> path);
    
    /**
     * Says if the node belong to the tree.
     * @param n the node
     * @return true iff the node belong to the tree.
     */
    public boolean contains(Node<T> n);

    /**
     * Removes all nodes except root.
     */
    public void clear();
}
