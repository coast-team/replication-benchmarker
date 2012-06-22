/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ottree;

import collect.OrderedNode;
import collect.UnorderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.tree.CRDTTree;
import crdt.tree.orderedtree.CRDTOrderedTree;
import java.util.List;

/**
 *
 * @author Stephane Martin
 */
public class OPTTree<T> extends CRDTOrderedTree<T> {
    OTTreeNode root;
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
        OTTreeRemoteOperation opt=(OTTreeRemoteOperation) ((OperationBasedOneMessage)op).getOperation();
        
    }

    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    @Override
    public CRDT<OrderedNode<T>> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

    
}
