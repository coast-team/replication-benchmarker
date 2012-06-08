/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;

import java.util.LinkedList;

/**
 *
 * @author Stephane Martin
 * 
 * A message contains many operations.
 */
public final class OperationBasedMessages<T> extends OperationBasedMessage implements ReplicatedMessage, Cloneable {
    private LinkedList<OperationBasedMessage> ops = new LinkedList<OperationBasedMessage>();

    OperationBasedMessages(OperationBasedMessage aThis, OperationBasedMessage msg) {

        addMessage(msg);
        addMessage(aThis);
    }

    private OperationBasedMessages() {
    }
    
    void addMessage(OperationBasedMessage mess){
        if (mess instanceof OperationBasedMessages){
            ops.addAll(((OperationBasedMessages)mess).getOps());
        }else{
            ops.add(mess);
        }
            
    }

    public LinkedList<OperationBasedMessage> getOps() {
        return ops;
    }
    
    @Override
    public OperationBasedMessages concat(ReplicatedMessage msg) {
        OperationBasedMessage cmsg = (OperationBasedMessage) msg;
        OperationBasedMessages ret=new OperationBasedMessages();
        ret.addMessage(this);
        ret.addMessage(cmsg);
        return this;
    } 
    
    @Override
    public OperationBasedMessages clone() {
        OperationBasedMessages clone = new OperationBasedMessages();
        for (OperationBasedMessage o : ops) {
            clone.ops.add(o.clone());
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (OperationBasedMessage m : ops) {
            s.append(" + ").append(m.toString());
        }
        return s.toString();
    }

    //abstract protected String visu();
    
    //abstract protected OperationBasedMessage copy();

    @Override
    public int size() {
        return ops.size();
    }
}
