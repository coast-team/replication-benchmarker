/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;

import crdt.Factory;
import crdt.PreconditionException;
import java.util.Observable;

/**
 *  
 * @author Stephane Martin
 */
public abstract class ReplicatedDocument<L> extends Observable implements Factory<ReplicatedDocument<L>> {
    private int replicaNumber;

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public int getReplicaNumber() {
        return replicaNumber;
    }
    
    abstract public Message applyLocal(Operation op) throws PreconditionException ;
    
    public void applyRemote(Message msg){
        
    }
    public abstract void applyRemote(Operation op);
    
    abstract public L lookup();
        
    @Deprecated
    public Long lastExecTime(){
        return 0L;
    }
}
