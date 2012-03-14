/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.Operation;

/**
 *
 * @author score
 */
public class SetOperation<T> implements Operation {
    
    public enum OpType {add, del}; 
    private OpType type;
    private T content;
    
    public SetOperation(OpType type, T obj) {
        this.type = type;
        this.content = obj;
    }

    public OpType getType() {
        return type;
    }
    
    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "SetOperation{" + "type=" + type + ", content=" + content + '}';
    }
    
}
