/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ottree;

import collect.UnorderedNode;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.tree.CRDTTree;

/**
 *
 * @author Stephane Martin
 */
public class OTTree extends CRDTTree{
    
    @Override
    public CRDTMessage add(UnorderedNode father, Object element) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage remove(UnorderedNode subtree) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UnorderedNode getRoot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void applyOneRemote(CRDTMessage msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object lookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
