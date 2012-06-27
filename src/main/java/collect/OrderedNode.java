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
     *
     * @return number of visible children
     */
    int childrenNumber();

    /**
     * Value of the root of the tree
     *
     * @return node value
     */
    T getValue();

    /**
     * Gets the ith child of this node
     *
     * @param p
     * @return children Node at ith visible pos.
     */
    OrderedNode<T> getChild(int p);

    /**
     * Gets all children in order
     *
     * @return the children
     */
    List<? extends OrderedNode<T>> getElements();

    /**
     * Creates a new ordered node
     *
     * @param elem the content
     * @return the children
     */
    OrderedNode<T> createNode(T elem);

    /**
     * Sets the replica number
     *
     * @param replicaNumber number
     */
    public void setReplicaNumber(int replicaNumber);
}
