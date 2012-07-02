/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import collect.VectorClock;
import crdt.CRDT;
import crdt.simulator.TraceOperation;
import java.util.List;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.git.model.Edition;
import jbenchmarker.trace.git.model.FileEdition;

/**
 * Mapping between couch extracted git trace and SequenceOperation
 * @author urso
 */
public class GitOperation extends TraceOperation {
    SequenceOperation<String> sop;
        
    public GitOperation(FileEdition f, Edition e, int replica, VectorClock VC) {
        super(replica, VC);
        SequenceOperation.OpType type;
        switch (e.getType()) {
        case DELETE: 
            type = SequenceOperation.OpType.del;
            break;
        case INSERT:
            type = SequenceOperation.OpType.ins;
            break;
        case REPLACE:
            type = SequenceOperation.OpType.up;
            break;
        default:
            type = SequenceOperation.OpType.unsupported;    
        }
        sop = new SequenceOperation<String>(type, replica, e.getBeginA(), 
                e.getEndA() - e.getBeginA(), e.getCb(), new VectorClock(VC));
    }

    @Override
    public Operation getOperation(CRDT replica) {
        return sop;
    }

    @Override
    public String toString() {
        return "GitOperation{" + "sop=" + sop + '}';
    }
}
