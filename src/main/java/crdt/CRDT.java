/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import java.util.Observable;

/**
 * A CRDT is a factory. create() returns a new CRDT with the same behavior. 
 * @author urso
 */
public abstract class CRDT<L> extends Observable implements Factory<CRDT<L>> {
    private int replicaNumber;

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public int getReplicaNumber() {
        return replicaNumber;
    }
    
    abstract public CRDTMessage applyLocal(Operation op) throws PreconditionException ;
    
    abstract public void applyRemote(CRDTMessage msg);
    
    
    abstract public L lookup();
    
    @Deprecated
    public Long lastExecTime(){
        return 0L;
    }
}
