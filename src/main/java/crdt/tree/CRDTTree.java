/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree;

import collect.Node;
import collect.Tree;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Operation;
import crdt.PreconditionException;
import java.util.List;

/**
 *
 * @author score
 */
public abstract class CRDTTree<T> extends CRDT<Tree<T>> {
    
   abstract public CRDTMessage add(Node<T> father, T element) throws PreconditionException;
    
   abstract public CRDTMessage remove(Node<T> subtree) throws PreconditionException;
    
   @Override
   final public CRDTMessage applyLocal(Operation op) throws PreconditionException {
       TreeOperation<T> top = (TreeOperation<T>) op;
       if (top.getType() == TreeOperation.OpType.add)
           return add(top.getNode(), top.getContent());
       else
           return remove(top.getNode());
   }
        
   abstract public Node<T> getRoot();
   
   public Node<T> getNode(T ... path) {
       Node<T> n = getRoot();
       for (T t : path) {
           n = n.getChild(t);
       }
       return n;
   }
   
   public Node<T> getNode(List<T> path) {
       Node<T> n = getRoot();
       for (T t : path) {
           n = n.getChild(t);
       }
       return n;
   }
}
