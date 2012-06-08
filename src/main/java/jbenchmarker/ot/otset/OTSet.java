/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import java.util.Set;

/**
 *
 * @author stephane martin
 * Un solution serait de serparer les truc avec plusieurs messages de ceux 
 */
public class OTSet<T> extends CRDTSet<T> {
    Set set;

    
    
    @Override
    public CRDTSet create() {       
        return new OTSet();
    }

    @Override
    protected CRDTMessage innerAdd(T t) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected CRDTMessage innerRemove(T t) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(T t) {
        return set.contains(t);
    }

    @Override
    public void applyRemote(CRDTMessage msg) {
        
    }

    @Override
    public Set<T> lookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    
}
