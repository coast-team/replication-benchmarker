/*
 * Jupiterclient with acknowledgement.
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.jupiter;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.LocalOperation;

/**
 * A client in jupiter system.
 * @author urso
 */
public class JupiterClient<T> {
    private int replicaNumber;
    final private Deque<List<OTOperation>> outGoing; 
    final private OTModel<T> document;

    public JupiterClient(OTModel<T> document) {
        this.document = document;
        this.outGoing = new LinkedList<List<OTOperation>>();
    }
    
    /**
     * Applies a user operation. Add corresponding OT operation to outgoing buffer.
     */
    public void applyLocal(LocalOperation local) {
        List<OTOperation> ops = document.generate(local);
        for (OTOperation op : ops) {
            op.setReplicaNumber(replicaNumber);
        }
        outGoing.add(ops);
    }

    /**
     * Applies server operations. 
     * Transforms this operation against concurrent pending local operations.
     */
    public void applyRemote(OTOperation msg) {
        Iterator<List<OTOperation>> it = outGoing.iterator();
        while (it.hasNext()) {
            for (OTOperation op : it.next()) {
                msg = document.transform(msg, op);
            }
        }
        document.apply(msg);
    }

    public T lookup() {
        return document.lookup();
    }

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    /**
     * Returns and remove first pending local operation.
     */
    public List<OTOperation> nextOperation() {
        return outGoing.pollFirst();
    }
}
