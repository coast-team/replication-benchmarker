/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.twotier;

import crdt.CRDT;
import crdt.CRDTMessage;
import java.util.List;
import jbenchmarker.jupiter.JupiterClient;
import jbenchmarker.jupiter.OTModel;
import jbenchmarker.jupiter.OTOperation;

/**
 * A core replica in two tier architecture.
 * Associates a jupiter OT server and a crdt.
 * @author urso
 */
public class CoreReplica<T> {
    private final OTModel<T> otdoc;
    private final CRDT<T> crdt;

    public CoreReplica(OTModel<T> otdoc, CRDT<T> crdt) {
        this.otdoc = otdoc;
        this.crdt = crdt;
    }
    
    
    
    CRDTMessage applyLocal(List<OTOperation> opt) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    List<OTOperation> applyRemote(CRDTMessage op) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    void setReplicaNumber(int n) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
