/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import jbenchmarker.core.*;

/**
 * Logoot document for counting concurrent deletes. 
 * Not most efficient implementation.
 * @author urso
 */
public class LogootCounter extends LogootDocument {
    public static class Factory extends ReplicaFactory {
        @Override public MergeAlgorithm create(int r) {
                return new LogootMerge(new LogootCounter(r, 64, new BoundaryStrategy(1000000000)), 1);
        }
    }
    
    public static int count = 0;

    public LogootCounter(int r, int nbBit, LogootStrategy strategy) {
        super(r, nbBit, strategy);
    }
    
    @Override
    public void apply(Operation op) {
        LogootOperation lg = (LogootOperation) op;
        LogootIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idToSearch);
        //Insertion et Delete
        if ((lg.getType() == SequenceMessage.MessageType.ins) && !getId(pos).equals(idToSearch)) {
            count++;
        }
        super.apply(op);
    }
}
