/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import collect.OrderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.tree.orderedtree.CRDTOrderedTree;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCTree<T> extends CRDTOrderedTree<T> {
    FCNode root;
    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void applyOneRemote(CRDTMessage op) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrderedNode<T> lookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDT<OrderedNode<T>> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
