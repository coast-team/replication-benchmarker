/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import collect.VectorClock;
import crdt.CRDT;
import java.io.Serializable;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
final public class TraceOperationImpl extends TraceOperation implements Serializable {
    LocalOperation op;
    
    public TraceOperationImpl() {
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(LocalOperation op) {
        this.op = op;
    }

    
    
    public TraceOperationImpl(LocalOperation op,int replica, VectorClock VC) {
        super(replica, VC);
        this.op=op;
    }
/**
 * I don't know with kind of algorithm.
 * Its place is on the algorithm itself
 * @param replica
 * @return 
 */
    @Override 
    public LocalOperation getOperation() {
        return op;
    }

    @Override
    public String toString() {
        return "TraceOperationImpl{" + "op=" + op +"N°Rep="+this.getReplica()+"VC="+this.getVectorClock()+ '}';
    }

   
    
}
