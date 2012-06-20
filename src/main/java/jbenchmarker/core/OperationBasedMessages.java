/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;

import crdt.CRDTMessage;
import java.util.LinkedList;

/**
 *
 *  
 * @author Stephane Martin
 * 
 * A message contains many operations.
 */
public final class OperationBasedMessages extends OperationBasedMessage implements ReplicatedMessage, Cloneable {
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

    /**
     * return all messages operations
     * @return
     */
    public LinkedList<OperationBasedMessage> getOps() {
        return ops;
    }
    
    /**
     * Concatenation of message.
     * @param msg
     * @return
     */
    @Override
    public OperationBasedMessages concat(ReplicatedMessage msg) {
        OperationBasedMessage cmsg = (OperationBasedMessage) msg;
        OperationBasedMessages ret=new OperationBasedMessages();
        ret.addMessage(this);
        ret.addMessage(cmsg);
        return this;
    } 
    
    /**
     * 
     * @return cloned operationBased message
     */
    @Override
    public OperationBasedMessages clone() {
        OperationBasedMessages clone = new OperationBasedMessages();
        for (OperationBasedMessage o : ops) {
            clone.ops.add(o.clone());
        }
        return clone;
    }

    /**
     * 
     * @return a representation of message
     */
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

    /**
     * 
     * @return number of messages.
     */
    @Override
    public int size() {
        return ops.size();
    }

   
    
}
