/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import crdt.simulator.TraceOperation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public interface OperationBasedMessage extends CRDTMessage, Cloneable {
  /*   public void setTraceOperation(TraceOperation traceOperation);
    public TraceOperation getTraceOperation();*/
    @Override
    public OperationBasedMessage clone();

    @Override
    public CRDTMessage concat(CRDTMessage msg);

    @Override
    void execute(CRDT cmrdt);

    @Override
    int size();
    
}
