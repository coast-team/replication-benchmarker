/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;

/**
 *
 * @author urso
 */
public abstract class ConvergentSet<T> extends CRDTSet<T> implements CRDTMessage {        
    @Override
    public CRDTMessage concat(CRDTMessage msg) {
       return msg;
    }
    
    @Override
    public void execute(CRDT crdt){
        crdt.applyOneRemote(this);
    }
    
    @Override
    abstract public ConvergentSet<T> innerAdd(T t) throws PreconditionException;
    
    @Override
    abstract public ConvergentSet<T> innerRemove(T t) throws PreconditionException;
    
    @Override
    abstract public ConvergentSet<T> clone();

    @Override
    public int size() {
        return 1;
    }
}
