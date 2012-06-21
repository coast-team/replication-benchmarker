/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.RemoteOperation;

/**
 *
 * @author urso
 */
public abstract class CommutativeSet<T> extends CRDTSet<T>  {        
    //@Override
    /*final public void applyRemote(CRDTMessage msg) {
        CommutativeSetMessage<T> op = (CommutativeSetMessage<T>) msg;
        applyOneRemoteNotify(op);
        for (OperationBasedOneMessage<T> m : op.getMsgs()) {
            applyOneRemoteNotify((CommutativeSetMessage<T>) m);
        }
    }*/
    
    
    abstract protected void applyOneInRemote(CommutativeSetMessage<T> op);
    
    @Override
    abstract public OperationBasedOneMessage innerAdd(T t) throws PreconditionException;
    
    @Override
    abstract public OperationBasedOneMessage innerRemove(T t) throws PreconditionException;

    @Override
    public void applyOneRemote(CRDTMessage opm) {
        CommutativeSetMessage<T> op =(CommutativeSetMessage)((OperationBasedOneMessage)opm).getOperation();
        T t = op.getContent();
        boolean before = lookup().contains(t);
        applyOneInRemote(op);
        boolean after = lookup().contains(t);
        if (before && !after)
            notifyDel(t);
        else if (!before && after)
            notifyAdd(t);    
    }
}
