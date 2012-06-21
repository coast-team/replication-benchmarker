/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import crdt.simulator.TraceOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @author urso
 */
public  class OperationBasedOneMessage implements OperationBasedMessage {
    TraceOperation traceOperation;
    //private LinkedList<CommutativeMessage<T>> msgs = new LinkedList<CommutativeMessage<T>>();

    /*public LinkedList<CommutativeMessage<T>> getMsgs() {
        return msgs;
    }*/
    RemoteOperation operation;

    public OperationBasedOneMessage(TraceOperation traceOperation, RemoteOperation operation) {
        this.traceOperation = traceOperation;
        this.operation = operation;
    }

    public OperationBasedOneMessage(RemoteOperation operation) {
        this.operation = operation;
    }
        
    
    @Override
    public CRDTMessage concat(CRDTMessage msg){
       return new OperationBasedMessagesBag(this,(OperationBasedMessage)msg);
   }

    /*@Override
    public String toString() {
        StringBuilder s = new StringBuilder(toString());
        for (OperationBasedOneMessage m : msgs) {
            s.append(" + ").append(m.toString());
        }
        return s.toString();
    }*/

    //abstract protected String toString();
    
    @Override
    public OperationBasedOneMessage clone(){
        return new OperationBasedOneMessage(this.traceOperation,this.operation);
    }

    @Override
    public int size() {
        return 1;
    }
    @Override
    public void execute(CRDT cmrdt){
        cmrdt.applyOneRemote(this);   
    }

    @Override
    public void setTraceOperation(TraceOperation traceOperation) {
        this.traceOperation=traceOperation;
    }

    @Override
    public TraceOperation getTraceOperation() {
        return traceOperation;
    }

    public RemoteOperation getOperation() {
        return operation;
    }

    public void setOperation(RemoteOperation operation) {
        this.operation = operation;
    }
    
}
