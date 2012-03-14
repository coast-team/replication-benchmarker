/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree;

import collect.Node;
import crdt.set.*;
import crdt.Operation;

/**
 *
 * @author score
 */
public class TreeOperation<T> implements Operation {
    
    public enum OpType {add, del}; 
    private OpType type;
    private Node<T> node;
    private T content;
    
    // Add operation
    public TreeOperation(Node<T> obj, T elem) {
        this.type = OpType.add;
        this.node = obj;
        this.content = elem;
    }
    
    // Del operation
    public TreeOperation(Node<T> obj) {
        this.type = OpType.del;
        this.node = obj;
        this.content = null;
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
}
