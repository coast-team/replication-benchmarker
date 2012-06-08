/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree;

import collect.Node;
import crdt.set.*;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.Operation;

/**
 *
 * @author score
 */
public class TreeOperation<T> implements Operation {
    
    public enum OpType {add, del, move}; 
    private final OpType type;
    private final Node<T> node, dest;
    private final T content;

    public TreeOperation(OpType type, Node<T> node, T content) {
        this.type = type;
        this.node = node;
        this.dest = null;
        this.content = content;
    }
    
    // Add operation
    public TreeOperation(Node<T> obj, T elem) {
        this(OpType.add, obj, elem);
    }
    
    // Del operation
    public TreeOperation(Node<T> obj) {
        this(OpType.del, obj, null);
    }

    public TreeOperation(Node<T> node, Node<T> dest, T content) {
        this.type = OpType.move;
        this.node = node;
        this.dest = dest;
        this.content = content;
    }

    public OpType getType() {
        return type;
    }

    public T getContent() {
        return content;
    }

    public Node<T> getNode() {
        return node;
    }

    public Node<T> getDest() {
        return dest;
    }
    
    @Override
    public Operation clone() {
        try {
            return (Operation) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
