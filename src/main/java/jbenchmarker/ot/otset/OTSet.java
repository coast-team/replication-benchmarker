/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import crdt.CRDTMessage;
import crdt.CommutativeMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import java.util.HashSet;
import java.util.Set;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2GarbageCollector;
import jbenchmarker.ot.soct2.SOCT2Message;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;

/**
 *
 * @param <T> Type of elements in set.
 * @author stephane martin OT set
 */
public class OTSet<T> extends CRDTSet<T> {

    Set set = new HashSet();
    SOCT2 soct2;
    int replicaNumber;
    /*
     * -- Factory --
     */
    SOCT2TranformationInterface ot;
    static int created = 0;
    int GCfrequency = 0;

    public OTSet(SOCT2TranformationInterface ot) {
        this.replicaNumber = ++created ;
        this.ot = ot;
        soct2 = new SOCT2(ot,replicaNumber , GCfrequency);

    }

    /**
     *
     * @param ot OT policy for this set AddWin or DelWin
     * @param replicaNumber Site number of replicat number
     */
    public OTSet(SOCT2TranformationInterface ot, int siteId) {
        this.replicaNumber = siteId;
        soct2 = new SOCT2(ot, siteId, GCfrequency);
        this.ot = ot;
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        soct2.setReplicaNumber(replicaNumber);
        this.replicaNumber = replicaNumber;
    }

    /**
     *
     * @param ot OT policy for this set AddWin or DelWin
     * @param replicaNumber Site number of replicat number
     * @param gc if true Enable the garbage collector
     */
    public OTSet(SOCT2TranformationInterface ot, int siteId, boolean gc) {
        if (gc){
            this.GCfrequency=SOCT2GarbageCollector.RECOMMANDED_GC_FREQUENCY_VALUE;
        }
        this.replicaNumber = siteId;
        
        soct2 = new SOCT2(ot, siteId, GCfrequency);
        this.ot = ot;
    }

    public OTSet(SOCT2TranformationInterface ot, int siteId, int gcFrequency) {
        this.replicaNumber = siteId;
        soct2 = new SOCT2(ot, siteId, gcFrequency);
        this.ot = ot;
        this.GCfrequency=gcFrequency;
    }
    /**
     * return new Otset
     *
     * @return
     */
    @Override
    public CRDTSet create() {
        return new OTSet(ot, replicaNumber + ++created ,GCfrequency);
    }

    /**
     * Local add is perfomed
     *
     * @param t new element t
     * @return Message sent to another replicas.
     * @throws PreconditionException if the element is already present.
     */
    @Override
    protected CRDTMessage innerAdd(T t) throws PreconditionException {
        if (set.contains(t)) {
            throw new PreconditionException("add : set already contains element " + t);
        } else {
            OTSetOperations<T> op = new OTSetOperations(OTSetOperations.OpType.Add, t, replicaNumber);
            set.add(t);
            return soct2.estampileMessage(op);
        }
    }

    /**
     * remove element t from set and return the message to inform another
     * replicas
     *
     * @param t element
     * @return CRDTmessage sent to another
     * @throws PreconditionException if t is not present in set.
     */
    @Override
    protected CRDTMessage innerRemove(T t) throws PreconditionException {
        if (set.contains(t)) {
            OTSetOperations<T> op = new OTSetOperations(OTSetOperations.OpType.Del, t, replicaNumber);
            set.remove(t);
            return soct2.estampileMessage(op);
        } else {
            throw new PreconditionException("del : element " + t + " is not present in set");
        }
    }

    /**
     * check if set containes t
     *
     * @param t
     * @return true if it contains
     */
    @Override
    public boolean contains(T t) {
        return set.contains(t);
    }

    /**
     * is called when a remote message is recieved.
     *
     * @param msg OTSetOperation message from another replicas.
     */
    @Override
    public void applyRemote(CRDTMessage msg) {

        applyOneOperation(msg);
        for (Object mess : ((CommutativeMessage) msg).getMsgs()) {
            applyOneOperation((CRDTMessage) mess);
        }


    }

    private void applyOneOperation(CRDTMessage msg) {
        OTSetOperations<T> op = (OTSetOperations<T>) soct2.integrateRemote((SOCT2Message) msg);
        switch (op.getType()) {
            case Add:
                if (!set.contains(op.getElement())) {
                    set.add(op.getElement());
                    notifyAdd(op.getElement());
                }
                break;
            case Del:
                if (set.contains(op.getElement())) {
                    set.remove(op.getElement());
                    notifyDel(op.getElement());
                }
                break;
            case Nop:
        }

    }

    /**
     *
     * @return set
     */
    @Override
    public Set<T> lookup() {

        return set;
    }

    @Override
    public String toString() {
        return "OTSet{" + "set=" + set + ", soct2=" + soct2 + ", replicaNumber=" + replicaNumber + ", ot=" + ot + '}';
    }
    
}
