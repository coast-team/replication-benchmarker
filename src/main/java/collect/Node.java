/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 *
 * @param <T> 
 * @author Stephane Martin
 */
public interface Node<T> {
    
    /**
     * 
     * @return Node value
     */
    T getValue();
    
    /**
     * 
     * @return Father node.
     */
    Node<T> getFather();
    
    /**
     * 
     * @return Father node.
     */
    Node<T> getRoot();
    
    /**
     * 
     * @return Path to this node.
     */
    List<T> getPath();
    
    /**
     * 
     * @return an iterator of Children
     */
    Iterator<?extends Node<T>> getChildrenIterator();

    /**
     * 
     * @return a copy of Children
     */
    Collection<?extends Node<T>> getChildrenCopy();

    /**
     * 
     * @return number of children
     */
    int getChildrenNumber();
    
    /**
     * Check if n is directly children of this node
     * @param n
     * @return true if n is childre of this
     */
    boolean isChildren(Node<T> n);
    
    // Node<T> clone();
    
    /*
     * delete children of node
     * @param current: node father
     * @param node to delete
     */
    void deleteChild( Collection<? extends Node<T>> nodeToDelet);
    /**
     * 
     * @return the level of this node on tree.
     */
    int getLevel();
}
