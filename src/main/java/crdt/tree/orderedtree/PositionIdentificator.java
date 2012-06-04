/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;

/**
 *
 * @author urso
 */
public interface PositionIdentificator {
    public PositionIdentifier generate(OrderedNode father, PositionIdentifier p, PositionIdentifier n);
    
    int getInteger(OrderedNode father, PositionIdentifier pi);
}
