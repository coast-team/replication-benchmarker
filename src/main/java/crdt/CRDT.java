/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import jbenchmarker.core.Operation;
import java.util.Observable;

/**
 * A CRDT is a factory. create() returns a new CRDT with the same behavior. 
 * @author urso
 */
public abstract class CRDT<L> extends Observable implements Factory<CRDT<L>> {
    private int replicaNumber;

    public CRDT(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public CRDT() {
    }

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public int getReplicaNumber() {
        return replicaNumber;
    }
    
    abstract public CRDTMessage applyLocal(Operation op) throws PreconditionException ;
    
    final public void applyRemote(CRDTMessage msg){
        msg.execute(this);
    }
    
    abstract public void applyOneRemote(CRDTMessage op);
    abstract public L lookup();
        
    @Deprecated
    public Long lastExecTime(){
        return 0L;
    }
}
