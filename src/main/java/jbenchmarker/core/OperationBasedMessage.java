/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;

import crdt.CRDTMessage;



/**
 * This is message by operation based
 * For future reunifications of commutative messages
 * @author Martin stephane
 */
public abstract class OperationBasedMessage implements  CRDTMessage {
   
    
    public OperationBasedMessage concat(CRDTMessage msg){
       return new OperationBasedMessages(this,(OperationBasedMessage)msg);
   }
   //public abstract ReplicatedDocuement apply
    @Override
    public abstract OperationBasedMessage clone();
    
    @Override
    public int size(){
        return 1;
    }

}
