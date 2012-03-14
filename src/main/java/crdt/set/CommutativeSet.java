/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.CommutativeMessage;
import crdt.CRDTMessage;
import crdt.PreconditionException;

/**
 *
 * @author urso
 */
public abstract class CommutativeSet<T> extends CRDTSet<T> {        
    @Override
    final public void applyRemote(CRDTMessage msg) {
        CommutativeSetMessage<T> op = (CommutativeSetMessage<T>) msg;
        applyOneRemoteNotify(op);
        for (CommutativeMessage<T> m : op.getMsgs()) {
            applyOneRemoteNotify((CommutativeSetMessage<T>) m);
        }
    }
    
    abstract protected void applyOneRemote(CommutativeSetMessage<T> op);
    
    @Override
    abstract public CommutativeMessage innerAdd(T t) throws PreconditionException;
    
    @Override
    abstract public CommutativeMessage innerRemove(T t) throws PreconditionException;

    private void applyOneRemoteNotify(CommutativeSetMessage<T> op) {
        T t = op.getContent();
        boolean before = lookup().contains(t);
        applyOneRemote(op);
        boolean after = lookup().contains(t);
        if (before && !after)
            notifyDel(t);
        else if (!before && after)
            notifyAdd(t);    
    }
}
