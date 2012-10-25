/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logootOneId;

import jbenchmarker.core.*;

/**
 * Logoot document for counting concurrent deletes. 
 * Not most efficient implementation.
 * @author urso
 */
public class LogootOneIdCounter extends LogootOneIdDocument {
    public static class Factory extends ReplicaFactory {
        @Override public MergeAlgorithm create(int r) {
                return new LogootOneIdMerge(new LogootOneIdCounter(r, new BoundaryStrategy(1000000000)), 1);
        }
    }
    
    public static int count = 0;
    
    public LogootOneIdCounter(int r, LogootOneIdStrategy strategy) {
        super(r, strategy);
    }
    
    @Override
    public void apply(Operation op) {
        LogootOneIdOperation lg = (LogootOneIdOperation) op;
        LogootOneIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idToSearch);
        //Insertion et Delete
        if ((lg.getType() == SequenceMessage.MessageType.ins) && !getId(pos).equals(idToSearch)) {
            count++;
        }
        super.apply(op);
    }
}
