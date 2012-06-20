/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import java.util.LinkedList;

/**
 *
 *  
 * @author Stephane Martin
 * 
 * A message contains many operations.
 */
public final class OperationBasedMessages extends CommutativeMessage implements Cloneable {
    private LinkedList<CommutativeMessage> ops = new LinkedList<CommutativeMessage>();

    OperationBasedMessages(CommutativeMessage aThis, CommutativeMessage msg) {

        
        addMessage(aThis);
        addMessage(msg);
    }

    private OperationBasedMessages() {
    }
    
    void addMessage(CommutativeMessage mess){
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
    public LinkedList<CommutativeMessage> getOps() {
        return ops;
    }
    
    /**
     * Concatenation of message.
     * @param msg
     * @return
     */
    @Override
    public CRDTMessage concat(CRDTMessage msg) {
        CommutativeMessage cmsg = (CommutativeMessage) msg;
        OperationBasedMessages ret=new OperationBasedMessages();
        ret.addMessage(this);
        ret.addMessage(cmsg);
        return ret;
    } 
    
    /**
     * 
     * @return cloned operationBased message
     */
    @Override
    public OperationBasedMessages clone() {
        OperationBasedMessages clone = new OperationBasedMessages();
        for (CommutativeMessage o : ops) {
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
        s.append("BAG:");
        for (CommutativeMessage m : ops) {
            s.append(" + ").append(m.toString());
        }
        return s.toString();
    }

    //abstract protected String toString();
    
    //abstract protected CommutativeMessage clone();

    /**
     * 
     * @return number of messages.
     */
    @Override
    public int size() {
        return ops.size();
    }

   

    @Override
   public void execute(CRDT cmrdt){
        for (CommutativeMessage o : ops) {
            cmrdt.applyOneRemote(o);
        }
    }

    
   
    
}
