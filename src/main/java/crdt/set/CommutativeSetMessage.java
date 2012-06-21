/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.RemoteOperation;

/**
 *
 * @author urso
 */
public abstract class CommutativeSetMessage<T> implements RemoteOperation<T> {   
    public static enum OpType {add, del}; 
    
    protected T content;

    public T getContent() {
        return content;
    }

    public CommutativeSetMessage(T content) {
        this.content = content;
    }
    
    abstract public OpType getType();
    @Override
    public abstract CommutativeSetMessage<T>clone();
}
