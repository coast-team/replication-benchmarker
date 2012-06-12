/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import crdt.tree.orderedtree.PositionIdentifier;
import crdt.tree.orderedtree.Positioned;
import java.util.List;

/**
 *
 * @author urso
 */
public interface OrderedNode<T> { 
    /**
     * Number of children
     * @return number of visible children
     */
    int childrenNumber();
    
    /**
     * Gets the ith child of this node
     * @param p 
     * @return children Node at ith visible pos.
     */
    OrderedNode<T> getChild(int p);  
    
    /**
     * Gets the child of this node with this position identifier
     * @param p a poisitioned element
     * @return children Node at this position
     */
    OrderedNode<T> getChild(Positioned<T> p);
    
    /**
     * Value of the root of the tree
     * @return node value
     */
    T getValue();
    
    /**
     * Gets the position identifier of the pth element
     * @return the position identifier
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
    
    /**
     * Gets all children in order
     * @return the children
     */    
    List<? extends OrderedNode<T>> getElements();
    
    /**
     * Creates a new ordered node 
     * @param elem the content
     * @return the children
     */
    OrderedNode<T> createNode(T elem);

    /**
     * Sets the replica number 
     * @param replicaNumber number
     */
    public void setReplicaNumber(int replicaNumber);
    
    /**
     * Sets the replica number 
     * @param replicaNumber number
     */
    public boolean same(OrderedNode<T> other);
}
