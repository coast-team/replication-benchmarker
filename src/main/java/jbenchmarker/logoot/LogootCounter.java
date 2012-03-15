/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.trace.TraceOperation;

/**
 * Logoot document for counting concurrent deletes. 
 * Not most efficient implementation.
 * @author urso
 */
public class LogootCounter extends LogootDocument {
    public static class Factory extends ReplicaFactory {
        @Override public MergeAlgorithm create(int r) {
                return new LogootMerge(new LogootDocument(Long.MAX_VALUE), r, 64, new BoundaryStrategy(1000000000));
        }
    }
    
    public static int count = 0;
    
    public LogootCounter(long max) {
        super(max); 
    }
    
    @Override
    public void apply(SequenceMessage op) {
        LogootOperation lg = (LogootOperation) op;
        LogootIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idToSearch);
        //Insertion et Delete
        if ((lg.getType() == TraceOperation.OpType.del) && !getIdTable().get(pos).equals(idToSearch)) {
            count++;
        }
        super.apply(op);
    }
}
