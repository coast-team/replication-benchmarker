/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;

import crdt.Factory;
import crdt.PreconditionException;
import jbenchmarker.core.Operation;
import java.util.Observable;

/**
 * A CRDT is a factory. create() returns a new CRDT with the same behavior. 
 * @author urso
 */
public abstract class ReplicatedDocument<L> extends Observable implements Factory<ReplicatedDocument<L>> {
    private int replicaNumber;

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public int getReplicaNumber() {
        return replicaNumber;
    }
    
    abstract public ReplicatedMessage applyLocal(Operation op) throws PreconditionException ;
    
    public void applyRemote(ReplicatedMessage msg){
        
    }
    public abstract void applyRemote(Operation op);
    
    abstract public L lookup();
        
    @Deprecated
    public Long lastExecTime(){
        return 0L;
    }
}
