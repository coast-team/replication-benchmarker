/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

/**
 *
 * @author urso
 */
public interface UnorderedNode<T> extends Node<T> { 
    /**
     * Get the child with tag t
     * @param t
     * @return child Node at t
     */
    UnorderedNode<T> getChild(T t);
}
