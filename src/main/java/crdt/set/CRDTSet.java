/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;
import crdt.CRDT;
import crdt.CRDTMessage;
import jbenchmarker.core.Operation;
import java.util.*;
import crdt.PreconditionException;

/**
 *
 * @author score
 */
public abstract class CRDTSet<T> extends CRDT<Set<T>>  {

    public CRDTSet() {
    }

    public CRDTSet(int replicaNumber) {
        super(replicaNumber);
    }
    
    @Override
    public abstract CRDTSet<T> create();
    
    abstract protected CRDTMessage innerAdd(T t) throws PreconditionException;
    
    abstract protected CRDTMessage innerRemove(T t) throws PreconditionException;
        
    abstract public boolean contains (T t);
    
    @Override
    final public CRDTMessage applyLocal(Operation op) throws PreconditionException {
        
        SetOperation<T> s = (SetOperation<T>) op;
        CRDTMessage msg;
        if (s.getType() == SetOperation.OpType.add) {
            msg = add(s.getContent());
        }else{
            msg = remove(s.getContent()); 
        }
        return msg;
    }

    /**
     * Adds an element to the set
     * @param t the element
     * @return a message
     * @throws PreconditionException if elment is already in lookup (or other crdt specific condition)
     */
    final public CRDTMessage add(T t) throws PreconditionException {
        if (lookup().contains(t)) {
            throw new PreconditionException("Add : the element " + t + " already exists in the set");
        }
        CRDTMessage msg;
        msg = innerAdd(t);
        notifyAdd(t);
        return msg;        
    }
    
    /**
     * Removes an element to the set
     * @param t the element
     * @return a message
     * @throws PreconditionException if elment is already in lookup (or other crdt specific condition)
     */
    final public CRDTMessage remove(T t) throws PreconditionException {
        if (!lookup().contains(t)) {
            throw new PreconditionException("Remove : the element " + t + " does not exist in the set");
        }
        CRDTMessage msg;
        msg = innerRemove(t);
        notifyDel(t);
        return msg; 
    }

    /**
     * Notifies the observers that an element is added.
     * Should be called when lookup is changed by applyRemote.
     * @param t the element added
     */
    protected void notifyAdd(T t) {
        this.setChanged();
        this.notifyObservers(new SetOperation<T>(SetOperation.OpType.add, t));
    }
    
    /**
     * Notifies the observers that an element is remove.
     * Should be called when lookup is changed by applyRemote.
     * @param t the element removed
     */
    protected void notifyDel(T t) {      
        this.setChanged();
        this.notifyObservers(new SetOperation<T>(SetOperation.OpType.del, t));
    }
}
