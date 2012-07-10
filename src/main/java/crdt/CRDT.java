/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import java.io.Serializable;
import java.util.Observable;
import jbenchmarker.core.LocalOperation;

/**
 * A CRDT is a factory. create() returns a new CRDT with the same behavior. 
 * @author urso
 */
public abstract class CRDT<L> extends Observable implements Factory<CRDT<L>>,Serializable {
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
    
    abstract public CRDTMessage applyLocal(LocalOperation op) throws PreconditionException ;
    
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
