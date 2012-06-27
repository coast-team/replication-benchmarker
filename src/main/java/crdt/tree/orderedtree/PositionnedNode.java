/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public interface PositionnedNode<T> extends OrderedNode<T> {
    
    
    
    /**
     * Gets the child of this node with this position identifier
     * @param p a poisitioned element
     * @return children Node at this position
     */
    OrderedNode<T> getChild(Positioned<T> p);
       
    /**
     * Gets the position identifier of the pth element
     * @return the position identifier
     *
     */
    Positioned<T> getPositioned(int p);
        
    /**
     * Gets a position identifier for a new element at the pth position
     * @param p natural postion
     * @param element content
     * @return a new position indentifier
     */
    PositionIdentifier getNewPosition(int p, T element);
    
    /**
     * Adds an element with this position identifier
     * @param pi position identifier
     * @param element content 
     */
    void add(PositionIdentifier pi, T element);
        
    /**
     * Removes an element with this position identifier
     * @param pi position identifier
     * @param element content 
     */
    void remove(PositionIdentifier pi, T element);
    
}
