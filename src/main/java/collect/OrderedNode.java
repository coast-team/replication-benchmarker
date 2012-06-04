/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import crdt.tree.orderedtree.PositionIdentifier;

/**
 *
 * @author urso
 */
public interface OrderedNode<T> extends Node<T> { 
    /**
     * Get the ith child of this node
     * @param p 
     * @return children Node at ith pos.
     */
    OrderedNode<T> getChild(int p);  
    
    /**
     * Get the position identifier
     * @return the position identifier
     */
    PositionIdentifier getPosition();
}
