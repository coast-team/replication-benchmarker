/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;



/**
 *
 * @author moi
 */
public abstract class OperationBasedMessage {
   public OperationBasedMessage concat(OperationBasedMessage msg){
       return new OperationBasedMessages(this,msg);
   }
   //public abstract ReplicatedDocuement apply
    public abstract OperationBasedMessage clone();
    
    public int size(){
        return 1;
    }

}
