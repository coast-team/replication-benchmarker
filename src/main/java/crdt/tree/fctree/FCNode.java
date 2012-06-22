/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import collect.OrderedNode;
import crdt.tree.orderedtree.PositionIdentifier;
import crdt.tree.orderedtree.Positioned;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCNode implements OrderedNode{
    
    @Override
    public int childrenNumber() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrderedNode getChild(int p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrderedNode getChild(Positioned p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Positioned getPositioned(int p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PositionIdentifier getNewPosition(int p, Object element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(PositionIdentifier pi, Object element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(PositionIdentifier pi, Object element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List getElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrderedNode createNode(Object elem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
    
}
