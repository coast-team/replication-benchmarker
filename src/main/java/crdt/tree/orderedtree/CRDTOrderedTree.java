/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Operation;
import crdt.PreconditionException;
import java.util.List;

/**
 *
 * @author score
 */
public abstract class CRDTOrderedTree<T> extends CRDT<OrderedNode<T>> {
    
   abstract public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException;
    
   abstract public CRDTMessage remove(List<Integer> path) throws PreconditionException;
    
   @Override
   final public CRDTMessage applyLocal(Operation op) throws PreconditionException {
       OrderedTreeOperation<T> top = (OrderedTreeOperation<T>) op;
       if (top.getType() == OrderedTreeOperation.OpType.add)
           return add(top.getPath(), top.hashCode(), top.getContent());
       else
           return remove(top.getPath());
   }
}
