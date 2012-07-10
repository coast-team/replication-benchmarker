/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdttest.tree;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author urso
 */
public class PseudoSet extends CRDTSet {

    Set s;

    public PseudoSet() {
        this.s = new HashSet();
    }

    public PseudoSet(Set s) {
        this.s = s;
    }

    @Override
    public Object lookup() {
        return s;
    }

    @Override
    public CRDTSet create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage innerAdd(Object t) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage innerRemove(Object t) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void applyOneRemote(CRDTMessage msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void adding(Object t) {
        s.add(t);
        notifyAdd(t);
    }

    public void removing(Object t) {
        s.remove(t);
        notifyDel(t);
    }
}
