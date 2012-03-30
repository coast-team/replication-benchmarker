/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

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
