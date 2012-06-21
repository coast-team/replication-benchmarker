/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import crdt.simulator.TraceOperation;
import java.util.LinkedList;

/**
 *
 *  
 * @author Stephane Martin
 * 
 * A message contains many operations.
 */
public final class OperationBasedMessagesBag implements OperationBasedMessage,Cloneable {
    private LinkedList<OperationBasedOneMessage> ops = new LinkedList<OperationBasedOneMessage>();

    TraceOperation traceOperation;
    OperationBasedMessagesBag(OperationBasedMessage aThis, OperationBasedMessage msg) {

        
        addMessage(aThis);
        addMessage(msg);
    }

    private OperationBasedMessagesBag() {
    }
    
    void addMessage(OperationBasedMessage mess){
        if (mess instanceof OperationBasedMessagesBag){
            ops.addAll(((OperationBasedMessagesBag)mess).getOps());
        }else{
            ops.add((OperationBasedOneMessage )mess);
        }
            
    }

    /**
     * return all messages operations
     * @return
     */
    public LinkedList<OperationBasedOneMessage> getOps() {
        return ops;
    }
    
    /**
     * Concatenation of message.
     * @param msg
     * @return
     */
    @Override
    public CRDTMessage concat(CRDTMessage msg) {
        OperationBasedOneMessage cmsg = (OperationBasedOneMessage) msg;
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
        for (OperationBasedOneMessage o : ops) {
            clone.ops.add((OperationBasedOneMessage)o.clone());
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
        for (OperationBasedOneMessage m : ops) {
            s.append(" + ").append(m.toString());
        }
        return s.toString();
    }

    //abstract protected String toString();
    
    //abstract protected OperationBasedOneMessage clone();

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
        for (OperationBasedOneMessage o : ops) {
            cmrdt.applyOneRemote(o);
        }
    }

    @Override
    public void setTraceOperation(TraceOperation traceOperation) {
        this.traceOperation=traceOperation;
    }

    @Override
    public TraceOperation getTraceOperation() {
        return traceOperation;
    }

   

 

    
   
    
}
