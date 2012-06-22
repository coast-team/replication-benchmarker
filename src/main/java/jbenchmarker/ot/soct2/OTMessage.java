/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import crdt.RemoteOperation;
import java.io.Serializable;
import jbenchmarker.core.Operation;

/**
 * This Object is sent to another replicas.
 * Message contains remote operation with vector clock.
 * @param <Op> type of operation managed 
 * @author Stephane Martin 
 */
public class OTMessage<Op extends Operation> implements RemoteOperation, Serializable {
    

    VectorClock vc;
    int siteID;
    Op operation;

    /**
     * Construct of this object with followed elements 
     * @param vc Vector clock of the operation 
     * @param siteID Site id of sender
     * @param operation the operation 
     */
    public OTMessage(VectorClock vc, int siteID, Op operation) {
        //super(op);
        this.vc = vc;
        this.siteID = siteID;
        this.operation = operation;
    }

    /**
     * 
     * @return the operation
     */
    public Op getOperation() {
        return operation;
    }

    /**
     * Change the operation of message
     * @param operation
     */
    public void setOperation(Op operation) {
        this.operation = operation;
    }

    /**
     * 
     * @return site id of sender
     */
    public int getSiteId() {
        return siteID;
    }

    /**
     * change site id
     * @param siteID
     */
    public void setSiteId(int siteID) {
        this.siteID = siteID;
    }


    /**
     * @return the vector clock of the operation
     */
    public VectorClock getClock() {
        return vc;
    }

    /**
     * Change vector clock of messages
     * @param vc Vector Clock
     */
    public void setVc(VectorClock vc) {
        this.vc = vc;
    }
    
    /**
     * Clone the message with operation
     * @return new operation
     */
    @Override
    public OTMessage clone(){
        return new OTMessage(new VectorClock(vc),siteID,operation.clone() );
    }
    
    /**
     * String representation of SOCT2Message
     * @return
     */
    @Override
    public String toString(){
       return "SOCT2Message ("+ operation +", from:"+siteID+" vc:"+vc.toString()+" )";
    }

   

   

    
}
