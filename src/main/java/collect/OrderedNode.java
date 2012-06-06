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
    
    int childrenNumber();
    
    /**
     * Get the ith child of this node
     * @param p 
     * @return children Node at ith pos.
     */
    OrderedNode<T> getChild(int p);  

    OrderedNode<T> getChild(Positioned<T> p);
    
    T getValue();
    
    /**
     * Get the position identifier
     * @return the position identifier
     */
    Positioned<T> getPositioned(int p);
        
    PositionIdentifier getNewPosition(int p, T element);
    
    void add(PositionIdentifier pi, T element);
    
    void remove(PositionIdentifier pi, T element);
    
    List<? extends OrderedNode<T>> getElements();
    
    OrderedNode<T> createNode(T elem);

    public void setReplicaNumber(int replicaNumber);
}
