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

    public OTSet(SOCT2TranformationInterface ot) {
        this.replicaNumber = 0;
        this.ot = ot;
        soct2 = new SOCT2(ot, 0, true);

    }

    /**
     *
     * @param ot OT policy for this set AddWin or DelWin
     * @param replicaNumber Site number of replicat number
     */
    public OTSet(SOCT2TranformationInterface ot, int siteId) {
        this.replicaNumber = siteId;
        soct2 = new SOCT2(ot, siteId, true);
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
        this.replicaNumber = siteId;
        soct2 = new SOCT2(ot, siteId, gc);
        this.ot = ot;
    }

    /**
     * return new Otset
     *
     * @return
     */
    @Override
    public CRDTSet create() {
        return new OTSet(ot, replicaNumber + ++created);
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
}
