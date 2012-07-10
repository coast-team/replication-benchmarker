/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree;

import collect.Tree;
import collect.UnorderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import java.util.List;
import jbenchmarker.core.LocalOperation;

/**
 *
 * @author score
 */
public abstract class CRDTTree<T> extends CRDT<Tree<T>>  {
    
   abstract public CRDTMessage add(UnorderedNode<T> father, T element) throws PreconditionException;
    
   abstract public CRDTMessage remove(UnorderedNode<T> subtree) throws PreconditionException;
    
   @Override
   final public CRDTMessage applyLocal(LocalOperation op) throws PreconditionException {
       TreeOperation<T> top = (TreeOperation<T>) op;
       if (top.getType() == TreeOperation.OpType.add)
           return add((UnorderedNode<T>) top.getNode(), top.getContent());
       else
           return remove((UnorderedNode<T>) top.getNode());
   }
        
   abstract public UnorderedNode<T> getRoot();
   
   public UnorderedNode<T> getNode(T ... path) {
       UnorderedNode<T> n = getRoot();
       for (T t : path) {
           n = n.getChild(t);
       }
       return n;
   }
   
   public UnorderedNode<T> getNode(List<T> path) {
       UnorderedNode<T> n = getRoot();
       for (T t : path) {
           n = n.getChild(t);
       }
       return n;
   }
}
