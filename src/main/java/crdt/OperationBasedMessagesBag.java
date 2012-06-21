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
public final class OperationBasedMessagesBag extends CommutativeMessage implements Cloneable {
    private LinkedList<CommutativeMessage> ops = new LinkedList<CommutativeMessage>();

    OperationBasedMessagesBag(CommutativeMessage aThis, CommutativeMessage msg) {

        
        addMessage(aThis);
        addMessage(msg);
    }

    private OperationBasedMessagesBag() {
    }
    
    void addMessage(CommutativeMessage mess){
        if (mess instanceof OperationBasedMessagesBag){
            ops.addAll(((OperationBasedMessagesBag)mess).getOps());
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
        OperationBasedMessagesBag ret=new OperationBasedMessagesBag();
        ret.addMessage(this);
        ret.addMessage(cmsg);
        return ret;
    } 
    
    /**
     * 
     * @return cloned operationBased message
     */
    @Override
    public OperationBasedMessagesBag clone() {
        OperationBasedMessagesBag clone = new OperationBasedMessagesBag();
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
