/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree.Operations;

import crdt.tree.fctree.FCIdentifier;
import crdt.tree.fctree.FCOperation;
import crdt.tree.fctree.FCTree;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class Move extends FCOperation {
    
    public Move(FCIdentifier id) {
        super(id);
    }

    @Override
    public Operation clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void apply(FCTree tree) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FCIdentifier[] DependOf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
