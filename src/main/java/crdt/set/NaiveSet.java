/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.set.CommutativeSetMessage.OpType;
import crdt.set.lastwriterwins.TypedMessage;
import java.util.HashSet;
import java.util.Set;
import jbenchmarker.ot.otset.OTSetOperations;

/**
 *
 * @author urso
 */
public class NaiveSet<T> extends CommutativeSet<T> {
    HashSet<T> set = new HashSet<T>();

    @Override
    protected void applyOneInRemote(CommutativeSetMessage<T> op) {
        if (op.getType() == OpType.add) {
            set.add(op.content);
        } else {
            set.remove(op.content);
        }
    }

    @Override
    public OperationBasedOneMessage innerAdd(T t) throws PreconditionException {
        set.add(t);
        return new OperationBasedOneMessage(new TypedMessage<T>(OpType.add, t));
    }

    @Override
    public OperationBasedOneMessage innerRemove(T t) throws PreconditionException {
        set.remove(t);
        return new OperationBasedOneMessage(new TypedMessage<T>(OpType.del, t));
    }

    @Override
    public CRDTSet<T> create() {
        return new NaiveSet<T>();
    }

    @Override
    public boolean contains(T t) {
        return set.contains(t);
    }

    @Override
    public Set<T> lookup() {
        return set;
    }

   

}
