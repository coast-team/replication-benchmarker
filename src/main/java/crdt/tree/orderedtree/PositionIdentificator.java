/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.Node;

/**
 *
 * @author urso
 */
public interface PositionIdentificator {
    public PositionIdentifier generate(Node father, PositionIdentifier p, PositionIdentifier n);
    
    int getInteger(Node father, PositionIdentifier pi);
}
